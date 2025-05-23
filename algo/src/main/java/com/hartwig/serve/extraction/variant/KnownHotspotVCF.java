package com.hartwig.serve.extraction.variant;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspotComparator;
import com.hartwig.serve.extraction.util.VCFWriterFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFCodec;

public final class KnownHotspotVCF {

    private static final Logger LOGGER = LogManager.getLogger(KnownHotspotVCF.class);

    private static final String GZIP_EXTENSION = ".gz";
    private static final String KNOWN_HOTSPOT_VCF = "KnownHotspots.SERVE.vcf.gz";

    private KnownHotspotVCF() {
    }

    @NotNull
    public static String knownHotspotVcfPath(@NotNull String outputDir, @NotNull RefGenome refGenome) {
        return addVersionToFilePath(refGenome, outputDir + File.separator + KNOWN_HOTSPOT_VCF);
    }

    public static void write(@NotNull String hotspotVcf, @NotNull IndexedFastaSequenceFile refSequence,
            @NotNull Iterable<KnownHotspot> hotspots) {
        VariantContextWriter writer = VCFWriterFactory.openIndexedVCFWriter(hotspotVcf, refSequence, uniqueSourcesString(hotspots));

        for (KnownHotspot hotspot : sort(hotspots)) {
            List<Allele> hotspotAlleles = buildAlleles(hotspot);

            VariantContext variant = new VariantContextBuilder().noGenotypes()
                    .source("SERVE")
                    .chr(hotspot.chromosome())
                    .start(hotspot.position())
                    .alleles(hotspotAlleles)
                    .computeEndFromAlleles(hotspotAlleles, hotspot.position())
                    .attribute(VCFWriterFactory.INPUT_FIELD,
                            toProteinKey(hotspot.gene(), hotspot.inputTranscript(), hotspot.inputProteinAnnotation()))
                    .attribute(VCFWriterFactory.SOURCES_FIELD, Knowledgebase.toCommaSeparatedSourceString(hotspot.sources()))
                    .make();

            LOGGER.debug(" Writing variant '{}'", variant);
            writer.add(variant);
        }

        writer.close();
    }

    @NotNull
    public static List<KnownHotspot> read(@NotNull String vcfFile) throws IOException {
        List<KnownHotspot> result = Lists.newArrayList();
        AbstractFeatureReader<VariantContext, LineIterator> reader = AbstractFeatureReader.getFeatureReader(vcfFile, new VCFCodec(), false);

        for (VariantContext hotspot : reader.iterator()) {
            String[] inputParts = hotspot.getAttributeAsString(VCFWriterFactory.INPUT_FIELD, Strings.EMPTY).split("\\|");
            String inputGene = inputParts[0];
            String inputProteinAnnotation = inputParts[2];
            String inputTranscript = inputParts[1].equals("null") ? null : inputParts[1];

            List<String> sources = hotspot.getAttributeAsStringList(VCFWriterFactory.SOURCES_FIELD, Strings.EMPTY);
            Set<Knowledgebase> knowledgebaseSet = Sets.newHashSet();
            if (!sources.isEmpty()) {
                for (String source : sources) {
                    knowledgebaseSet = Knowledgebase.fromCommaSeparatedSourceString(source);
                }
            } else {
                LOGGER.warn("No sources found on {}", hotspot);
            }

            result.add(ImmutableKnownHotspot.builder()
                    .chromosome(hotspot.getContig())
                    .gene(inputGene)
                    .geneRole(GeneRole.UNKNOWN)
                    .proteinEffect(ProteinEffect.UNKNOWN)
                    .position(hotspot.getStart())
                    .ref(hotspot.getReference().getBaseString())
                    .alt(hotspot.getAlleles().get(0).getBaseString())
                    .inputTranscript(inputTranscript)
                    .inputProteinAnnotation(inputProteinAnnotation)
                    .sources(knowledgebaseSet)
                    .build());
        }

        return result;
    }

    @NotNull
    private static List<KnownHotspot> sort(@NotNull Iterable<KnownHotspot> hotspots) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<KnownHotspot> sorted = Lists.newArrayList(hotspots);
        sorted.sort(new VariantHotspotComparator());

        return sorted;
    }

    @VisibleForTesting
    @NotNull
    static String addVersionToFilePath(@NotNull RefGenome refGenome, @NotNull String filePath) {
        String modifiedFilePath = filePath;
        if (filePath.endsWith(GZIP_EXTENSION)) {
            modifiedFilePath = filePath.substring(0, filePath.indexOf(GZIP_EXTENSION));
        }

        if (!modifiedFilePath.contains(".")) {
            throw new IllegalStateException("Cannot include ref genome version in file path that has no proper extension: " + filePath);
        }

        String identifier = determineRefGenomeIdentifier(refGenome);
        int extensionStart = modifiedFilePath.lastIndexOf(".");
        String versionedFilePath =
                modifiedFilePath.substring(0, extensionStart) + "." + identifier + modifiedFilePath.substring(extensionStart);

        if (filePath.endsWith(GZIP_EXTENSION)) {
            versionedFilePath = versionedFilePath + GZIP_EXTENSION;
        }

        return versionedFilePath;
    }

    @VisibleForTesting
    @NotNull
    static String determineRefGenomeIdentifier(@NotNull RefGenome refGenome) {
        if (refGenome == RefGenome.V37) {
            return "37";
        } else if (refGenome == RefGenome.V38) {
            return "38";
        } else {
            throw new IllegalStateException("Cannot determine identifier for ref genome version: " + refGenome);
        }
    }

    @VisibleForTesting
    @NotNull
    static String uniqueSourcesString(@NotNull Iterable<KnownHotspot> hotspots) {
        Set<Knowledgebase> sources = Sets.newHashSet();
        for (KnownHotspot hotspot : hotspots) {
            sources.addAll(hotspot.sources());
        }
        return Knowledgebase.toCommaSeparatedSourceString(sources);
    }

    @NotNull
    private static List<Allele> buildAlleles(@NotNull VariantHotspot hotspot) {
        Allele ref = Allele.create(hotspot.ref(), true);
        Allele alt = Allele.create(hotspot.alt(), false);

        return Lists.newArrayList(ref, alt);
    }

    @NotNull
    private static String toProteinKey(@NotNull String gene, @Nullable String transcript, @NotNull String proteinAnnotation) {
        String formattedProteinAnnotation = !proteinAnnotation.isEmpty() ? "p." + proteinAnnotation : "-";
        return gene + "|" + transcript + "|" + formattedProteinAnnotation;
    }
}

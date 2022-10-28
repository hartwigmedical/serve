package com.hartwig.serve.extraction.hotspot.tools;

import static htsjdk.tribble.AbstractFeatureReader.getFeatureReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.ensemblcache.EnsemblDataLoader;
import com.hartwig.serve.common.ensemblcache.GeneData;
import com.hartwig.serve.common.ensemblcache.TranscriptData;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.extraction.snpeff.CanonicalAnnotation;
import com.hartwig.serve.extraction.snpeff.SnpEffAnnotation;
import com.hartwig.serve.extraction.snpeff.SnpEffAnnotationParser;
import com.hartwig.serve.extraction.util.VCFWriterFactory;
import com.hartwig.serve.util.AminoAcids;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

public class AnnotatedHotspotVCFPrinter {

    private static final Logger LOGGER = LogManager.getLogger(AnnotatedHotspotVCFPrinter.class);

    public static void main(String[] args) throws IOException {
        String annotatedInputVcf = System.getProperty("user.home") + "/hmf/tmp/annotatedHotspots_SNPeff.vcf";
        String ensemblDataCacheDir = args[1];
        new AnnotatedHotspotVCFPrinter().run(annotatedInputVcf, ensemblDataCacheDir);
    }

    public void run(@NotNull String annotatedInputVcf, final String ensemblDataCacheDir) throws IOException {
        EnsemblDataCache ensemblDataCache = EnsemblDataLoader.load(ensemblDataCacheDir, RefGenomeVersion.V37);

        Map<String,String> geneNamePerCanonicalTranscriptMap = createGeneNamePerCanonicalTranscriptMap(ensemblDataCache);

        CanonicalAnnotation factory = new CanonicalAnnotation(Sets.newHashSet(), geneNamePerCanonicalTranscriptMap);

        LOGGER.info("Simplifying variants from '{}'", annotatedInputVcf);
        AbstractFeatureReader<VariantContext, LineIterator> reader = getFeatureReader(annotatedInputVcf, new VCFCodec(), false);
        for (VariantContext variant : reader.iterator()) {
            List<SnpEffAnnotation> annotations = SnpEffAnnotationParser.fromContext(variant);
            Optional<SnpEffAnnotation> canonical = factory.canonicalSnpEffAnnotation(annotations);

            String canonicalProtein = canonical.map(SnpEffAnnotation::hgvsProtein).orElse(Strings.EMPTY);
            if (canonicalProtein.isEmpty()) {
                canonicalProtein = "-";
            }

            StringJoiner joiner = new StringJoiner("|");
            joiner.add(variant.getContig())
                    .add(String.valueOf(variant.getStart()))
                    .add(variant.getAlleles().get(0).getBaseString())
                    .add(variant.getAlleles().get(1).getBaseString())
                    .add(canonical.map(SnpEffAnnotation::gene).orElse(Strings.EMPTY))
                    .add(canonical.map(SnpEffAnnotation::transcript).orElse(Strings.EMPTY))
                    .add(canonical.map(SnpEffAnnotation::consequenceString).orElse(Strings.EMPTY))
                    .add(canonical.map(SnpEffAnnotation::hgvsCoding).orElse(Strings.EMPTY))
                    .add(AminoAcids.forceSingleLetterProteinAnnotation(canonicalProtein));

            Object input = variant.getAttribute(VCFWriterFactory.INPUT_FIELD);
            if (input != null) {
                joiner.add(input.toString());
            }

            List<String> sources = variant.getAttributeAsStringList(VCFWriterFactory.SOURCES_FIELD, Strings.EMPTY);
            if (!sources.isEmpty()) {
                StringJoiner sourceJoiner = new StringJoiner(",");
                for (String source : sources) {
                    sourceJoiner.add(source);
                }
                joiner.add(sourceJoiner.toString());
            } else {
                LOGGER.warn("No sources found on {}", variant);
            }

            System.out.println(joiner);
        }
    }

    @NotNull
    private static Map<String, String> createGeneNamePerCanonicalTranscriptMap(@NotNull EnsemblDataCache ensemblDataCache) {
        Map<String, String> canonicalTranscriptToGeneNameMap = Maps.newHashMap();

        for (List<GeneData> genes : ensemblDataCache.genesPerChromosome().values()) {
            for (GeneData geneData : genes) {
                List<TranscriptData> transcripts = ensemblDataCache.transcriptsForGeneId(geneData.geneId());

                for (TranscriptData transcript : transcripts) {
                    if (transcript.isCanonical()) {
                        canonicalTranscriptToGeneNameMap.put(transcript.transcriptName(), geneData.geneName());
                    }
                }
            }
        }

        return canonicalTranscriptToGeneNameMap;
    }
}

package com.hartwig.serve.extraction.codon.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ServeConfig;
import com.hartwig.serve.ServeLocalConfigProvider;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.serialization.ServeJson;
import com.hartwig.serve.extraction.util.GenerateAltBase;
import com.hartwig.serve.extraction.util.KeyFormatter;
import com.hartwig.serve.extraction.util.VCFWriterFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;

public class CodonAnnotationToVCFConverter {

    private static final Logger LOGGER = LogManager.getLogger(CodonAnnotationToVCFConverter.class);

    private static final boolean LOG_DEBUG = false;

    public static void main(String[] args) throws IOException {
        LOGGER.info("Running SERVE codon VCF converter");

        if (LOG_DEBUG) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        ServeConfig config = ServeLocalConfigProvider.create();
        IndexedFastaSequenceFile refSequence37 = new IndexedFastaSequenceFile(new File(config.refGenome37FastaFile()));

        String serveJson = System.getProperty("user.home") + "/hmf/tmp/serve.37.json";
        String outputVcf = System.getProperty("user.home") + "/hmf/tmp/codons.vcf.gz";
        GenerateAltBase altBaseGenerator = new GenerateAltBase(RefGenome.V37, refSequence37);

        ServeRecord serveRecord = ServeJson.read(serveJson);
        List<KnownCodon> codons = serveRecord.knownEvents().codons().stream().sorted().collect(Collectors.toList());
        LOGGER.info("The number of codons in known codon file is {}", codons.size());

        VariantContextWriter writer = VCFWriterFactory.openVCFWriter(outputVcf, uniqueSourcesString(codons));

        for (KnownCodon codon : codons) {
            List<Long> positions = Lists.newArrayList();
            for (long i = codon.start(); i <= codon.end(); i++) {
                positions.add(i);
            }

            String chromosome = codon.chromosome();
            for (long position : positions) {
                String refBaseOfPosition = altBaseGenerator.extractRefBaseAtGenomicPosition(chromosome, position);
                String randomAltBase = altBaseGenerator.createAltForRefBase(chromosome, position);

                writeVariantToVCF(writer,
                        chromosome,
                        position,
                        refBaseOfPosition,
                        randomAltBase,
                        codon.sources(),
                        codon.gene(),
                        codon.inputTranscript(),
                        codon.inputCodonRank());
            }
        }

        writer.close();

        LOGGER.info("All known codons are converted and written to '{}'", outputVcf);
    }

    private static void writeVariantToVCF(@NotNull VariantContextWriter writer, @NotNull String chromosome, long position,
            @NotNull String ref, @NotNull String alt, @NotNull Set<Knowledgebase> knowledgebases, @NotNull String gene,
            @NotNull String transcript, int codonRank) {
        List<Allele> alleles = Lists.newArrayList(Allele.create(ref, true), Allele.create(alt, false));

        VariantContext variant = new VariantContextBuilder().noGenotypes()
                .source("CodonChecker")
                .chr(chromosome)
                .start(position)
                .alleles(alleles)
                .computeEndFromAlleles(alleles, Long.valueOf(position).intValue())
                .attribute(VCFWriterFactory.INPUT_FIELD, KeyFormatter.toCodonKey(gene, transcript, codonRank))
                .attribute(VCFWriterFactory.SOURCES_FIELD, Knowledgebase.toCommaSeparatedSourceString(knowledgebases))
                .make();

        LOGGER.debug(" Writing '{}' to VCF file", variant);
        writer.add(variant);
    }

    @NotNull
    private static String uniqueSourcesString(@NotNull Iterable<? extends KnownEvent> events) {
        Set<Knowledgebase> sources = Sets.newHashSet();
        for (KnownEvent event : events) {
            sources.addAll(event.sources());
        }
        return Knowledgebase.toCommaSeparatedSourceString(sources);
    }
}

package com.hartwig.serve.extraction.exon.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ServeConfig;
import com.hartwig.serve.ServeLocalConfigProvider;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.serialization.KnownExonFile;
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

public class ExonAnnotationToVCFConverter {

    private static final Logger LOGGER = LogManager.getLogger(ExonAnnotationToVCFConverter.class);

    private static final boolean LOG_DEBUG = false;

    public static void main(String[] args) throws IOException {
        LOGGER.info("Running SERVE exon VCF converter");

        if (LOG_DEBUG) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        ServeConfig config = ServeLocalConfigProvider.create();
        IndexedFastaSequenceFile refSequence37 = new IndexedFastaSequenceFile(new File(config.refGenome37FastaFile()));

        String knownExonsTsv = System.getProperty("user.home") + "/hmf/tmp/KnownExons.SERVE.37.tsv";
        String outputVcf = System.getProperty("user.home") + "/hmf/tmp/exons.vcf.gz";
        GenerateAltBase altBaseGenerator = new GenerateAltBase(RefGenome.V37, refSequence37);

        List<KnownExon> exons = KnownExonFile.read(knownExonsTsv);
        LOGGER.info("The number of known exons in the known exon file is {}", exons.size());

        VariantContextWriter writer = VCFWriterFactory.openVCFWriter(outputVcf, uniqueSourcesString(exons));

        for (KnownExon exon : exons) {
            String chromosome = exon.chromosome();
            long start = exon.start() + 10; // remove the first 10 non-coding positions before exon
            long end = exon.end() - 10; // remove the last 10 non-coding positions after exon
            long middle = start + Math.round((end - start) / 2D); // take middle position of exon

            List<Long> positions = Lists.newArrayList(start, middle, end);
            for (long position : positions) {
                String refBaseOfPosition = altBaseGenerator.extractRefBaseAtGenomicPosition(chromosome, position);
                String randomAltBase = altBaseGenerator.createAltForRefBase(chromosome, position);

                writeVariantToVCF(writer,
                        chromosome,
                        position,
                        refBaseOfPosition,
                        randomAltBase,
                        exon.sources(),
                        exon.gene(),
                        exon.inputTranscript(),
                        exon.inputExonRank());
            }
        }

        writer.close();

        LOGGER.info("All known exons are converted and written to '{}'", outputVcf);
    }

    private static void writeVariantToVCF(@NotNull VariantContextWriter writer, @NotNull String chromosome, long position,
            @NotNull String ref, @NotNull String alt, @NotNull Set<Knowledgebase> knowledgebases, @NotNull String gene,
            @NotNull String transcript, int exonRank) {
        List<Allele> alleles = Lists.newArrayList(Allele.create(ref, true), Allele.create(alt, false));

        VariantContext variant = new VariantContextBuilder().noGenotypes()
                .source("ExonChecker")
                .chr(chromosome)
                .start(position)
                .alleles(alleles)
                .computeEndFromAlleles(alleles, Long.valueOf(position).intValue())
                .attribute(VCFWriterFactory.INPUT_FIELD, KeyFormatter.toExonKey(gene, transcript, exonRank))
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

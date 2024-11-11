package com.hartwig.serve.extraction.hotspot.tools;

import static htsjdk.tribble.AbstractFeatureReader.getFeatureReader;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import com.hartwig.serve.common.variant.impact.VariantImpact;
import com.hartwig.serve.common.variant.impact.VariantImpactSerialiser;
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
        String annotatedInputVcf = System.getProperty("user.home") + "/hmf/tmp/KnownHotspots.somatic.37.pave.vcf";
        new AnnotatedHotspotVCFPrinter().run(annotatedInputVcf);
    }

    public void run(@NotNull String annotatedInputVcf) throws IOException {
        LOGGER.info("Simplifying variants from '{}'", annotatedInputVcf);
        AbstractFeatureReader<VariantContext, LineIterator> reader = getFeatureReader(annotatedInputVcf, new VCFCodec(), false);
        for (VariantContext variant : reader.iterator()) {
            VariantImpact impact = VariantImpactSerialiser.fromVariantContext(variant);

            String canonicalProtein = impact.canonicalHgvsProtein();

            if (canonicalProtein.isEmpty()) {
                canonicalProtein = "-";
            }

            StringJoiner joiner = new StringJoiner("|");
            joiner.add(variant.getContig())
                    .add(String.valueOf(variant.getStart()))
                    .add(variant.getAlleles().get(0).getBaseString())
                    .add(variant.getAlleles().get(1).getBaseString())
                    .add(impact.canonicalGeneName())
                    .add(impact.canonicalTranscript())
                    .add(impact.canonicalEffect())
                    .add(impact.canonicalHgvsCoding())
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
}
package com.hartwig.serve.extraction.codon.tools;

import java.io.IOException;

import com.hartwig.serve.common.variant.impact.VariantImpact;
import com.hartwig.serve.common.variant.impact.VariantImpactSerialiser;
import com.hartwig.serve.datamodel.hotspot.VCFWriterFactory;
import com.hartwig.serve.util.AminoAcids;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

public class AnnotatedCodonVCFCheckerPAVE {

    private static final Logger LOGGER = LogManager.getLogger(AnnotatedCodonVCFCheckerPAVE.class);

    private static final boolean LOG_DEBUG = false;

    public static void main(String[] args) throws IOException {
        LOGGER.info("Running SERVE Codon VCF checker");

        if (LOG_DEBUG) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        int totalCount = 0;
        int matchCount = 0;
        int diffCount = 0;

        String annotatedCodonVcf = System.getProperty("user.home") + "/hmf/tmp/annotatedCodons.vcf";

        LOGGER.info("Loading codons from '{}'", annotatedCodonVcf);
        AbstractFeatureReader<VariantContext, LineIterator> reader =
                AbstractFeatureReader.getFeatureReader(annotatedCodonVcf, new VCFCodec(), false);
        for (VariantContext variant : reader.iterator()) {
            totalCount++;

            String[] inputParts = variant.getAttributeAsString(VCFWriterFactory.INPUT_FIELD, Strings.EMPTY).split("\\|");
            String inputGene = inputParts[0];
            String inputTranscript = inputParts[1].equals("null") ? null : inputParts[1];
            int inputCodon = Integer.parseInt(inputParts[2]);

            VariantImpact impact = VariantImpactSerialiser.fromVariantContext(variant);

            if (isMatch(inputGene, inputTranscript, inputCodon, impact)) {
                matchCount++;
            } else {
                diffCount++;
            }
        }

        LOGGER.info("Done comparing {} codons: {} matches and {} differences found.", totalCount, matchCount, diffCount);
    }

    @Nullable
    private static VariantImpact annotationForTranscript(@NotNull VariantImpact impact, @NotNull String transcript) {

        if (impact.CanonicalTranscript.equals(transcript)) {
            return impact;
        }

        return null;
    }

    private static boolean isMatch(@NotNull String inputGene, @Nullable String inputTranscript, int inputCodon,
            @NotNull VariantImpact impact) {
        if (inputTranscript != null) {
            VariantImpact variantImpact = annotationForTranscript(impact, inputTranscript);

            if (variantImpact != null) {
                int snpEffCodon = extractCodon(variantImpact.CanonicalHgvsProtein);
                if (inputCodon == snpEffCodon) {
                    LOGGER.debug("Identical on gene '{}': SERVE input codon '{}' vs PAVE codon '{}'", inputGene, inputCodon, snpEffCodon);
                    return true;
                } else {
                    LOGGER.warn("Difference on gene '{}': SERVE input codon '{}' vs PAVE codon '{}'", inputGene, inputCodon, snpEffCodon);
                    return false;
                }
            } else {
                LOGGER.warn("No suitable PAVE annotation found on gene '{}': SERVE input codon '{}'", inputGene, inputCodon);
                return false;
            }
        } else {
            // In case input transcript is missing we try to match against any transcript.
            boolean matchFound = false;

            int snpEffCodon = extractCodon(impact.CanonicalHgvsProtein);
            if (inputCodon == snpEffCodon) {
                matchFound = true;

            }

            if (matchFound) {
                LOGGER.debug("Found a match amongst candidate transcripts for '{}' on '{}", inputCodon, inputGene);
                return true;
            } else {
                LOGGER.warn("Could not find a match amongst candidate transcripts for '{}' on '{}'", inputCodon, inputGene);
                return false;
            }
        }
    }

    private static int extractCodon(@NotNull String hgvsProteinAnnotation) {
        String singleLetterAA = AminoAcids.forceSingleLetterProteinAnnotation(hgvsProteinAnnotation);
        // The single letter AA should always start with "p.{A}"
        return Integer.parseInt(singleLetterAA.substring(3, singleLetterAA.length() - 1));
    }
}

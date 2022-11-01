package com.hartwig.serve.snpeff;

import static com.hartwig.serve.common.variant.VariantConsequence.VARIANT_CONSEQ_DELIM;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.common.variant.VariantConsequence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import htsjdk.variant.variantcontext.VariantContext;

public final class SnpEffAnnotationParser {

    private static final Logger LOGGER = LogManager.getLogger(SnpEffAnnotationParser.class);

    static final String SNPEFF_IDENTIFIER = "ANN";
    static final String MICROHOMOLOGY_FLAG = "MH";
    static final String REPEAT_COUNT_FLAG = "REP_C";
    static final String REPEAT_SEQUENCE_FLAG = "REP_S";

    static final String SPLICE_DONOR_CONSEQUENCE = "splice_donor_variant";

    private static final String FIELD_SEPARATOR = "\\|";

    private static final int EXPECTED_FIELD_SIZE_PER_ANNOTATION = 16;

    private SnpEffAnnotationParser() {
    }

    @NotNull
    public static List<SnpEffAnnotation> fromContext(@NotNull VariantContext context) {
        if (context.hasAttribute(SNPEFF_IDENTIFIER)) {
            return fromAnnotationList(context, context.getAttributeAsStringList(SNPEFF_IDENTIFIER, ""));
        }

        return Collections.emptyList();
    }

    @NotNull
    private static List<SnpEffAnnotation> fromAnnotationList(@NotNull VariantContext context, @NotNull List<String> annotation) {
        return annotation.stream()
                .map(x -> enforceMinLength(x.trim().split(FIELD_SEPARATOR), EXPECTED_FIELD_SIZE_PER_ANNOTATION))
                .filter(SnpEffAnnotationParser::isCorrectNumberOfParts)
                .map(x -> fromParts(context, x))
                .collect(Collectors.toList());
    }

    private static boolean isCorrectNumberOfParts(@NotNull String[] parts) {
        if (parts.length == EXPECTED_FIELD_SIZE_PER_ANNOTATION) {
            return true;
        }

        StringJoiner joiner = new StringJoiner("|");
        Stream.of(parts).forEach(joiner::add);

        LOGGER.warn("Annotation found with invalid field count: " + joiner);
        return false;
    }

    @NotNull
    private static SnpEffAnnotation fromParts(@NotNull VariantContext context, @NotNull String[] parts) {
        String effects = extractAnnotationEffects(context, parts);

        return ImmutableSnpEffAnnotation.builder()
                .effects(effects)
                .consequences(VariantConsequence.convertFromEffects(toEffects(effects)))
                .gene(parts[3])
                .geneID(parts[4])
                .featureType(parts[5])
                .featureID(parts[6])
                .rank(parts[8])
                .hgvsCoding(parts[9])
                .hgvsProtein(parts[10])
                .build();
    }

    @NotNull
    private static List<String> toEffects(@NotNull String effectString) {
        return Lists.newArrayList(effectString.split(VARIANT_CONSEQ_DELIM));
    }

    @NotNull
    private static String addEffect(@NotNull String effect, @NotNull String effects, boolean atStart) {
        return atStart ? effect + VARIANT_CONSEQ_DELIM + effects : effects + VARIANT_CONSEQ_DELIM + effect;
    }

    @NotNull
    private static String extractAnnotationEffects(@NotNull VariantContext variant, @NotNull String[] parts) {
        String hgvsCoding = parts[9];
        String effects = parts[1];
        if (!effects.contains("splice")) {
            return effects;
        }

        // Below is to support additional donor annotation for variants affecting +5 base. See also DEV-1650
        // Note, no longer strictly necessary to do here as have also got this in CodingEffectFactory.java
        boolean indel = variant.isIndel();
        if (!indel) {
            return hgvsCoding.contains("+5") ? addEffect(SPLICE_DONOR_CONSEQUENCE, effects, true) : effects;
        }

        String hgvsCodingType;
        if (hgvsCoding.contains("ins")) {
            hgvsCodingType = "ins";
        } else if (hgvsCoding.contains("del")) {
            hgvsCodingType = "del";
        } else if (hgvsCoding.contains("dup")) {
            hgvsCodingType = "dup";
        } else {
            return effects;
        }

        int initialSpliceBase = initialIndelSpliceBase(hgvsCodingType.equals("ins"), hgvsCoding);
        if (initialSpliceBase == -1) {
            return effects;
        }

        int adjustedSpliceBase;
        String ref = variant.getReference().getBaseString();
        String alt = variant.getAlternateAllele(0).getBaseString();
        if (isPositiveStrand(ref, alt, hgvsCoding)) {
            String variantBases = ref.length() > alt.length() ? ref.substring(1) : alt.substring(1);
            int microhomologyAdditionalBases = variant.getAttributeAsString(MICROHOMOLOGY_FLAG, Strings.EMPTY).length();
            String repeatSequence = variant.getAttributeAsString(REPEAT_SEQUENCE_FLAG, Strings.EMPTY);
            int repeatCount = variant.getAttributeAsInt(REPEAT_COUNT_FLAG, 0);
            int repeatCountAdditionalBases = variantBases.equals(repeatSequence) ? repeatCount * repeatSequence.length() : 0;
            adjustedSpliceBase = initialSpliceBase + Math.max(microhomologyAdditionalBases, repeatCountAdditionalBases);
        } else {
            adjustedSpliceBase = initialSpliceBase;
        }

        return adjustedSpliceBase <= 5 ? addEffect(SPLICE_DONOR_CONSEQUENCE, effects, true) : effects;
    }

    @VisibleForTesting
    static int initialIndelSpliceBase(boolean isInsert, @NotNull String hgvsCoding) {
        int firstIndexOfPlus = hgvsCoding.indexOf("+");
        if (firstIndexOfPlus < 0) {
            return -1;
        }

        try {
            int spliceLocation = Integer.parseInt(hgvsCoding.substring(firstIndexOfPlus + 1, firstIndexOfPlus + 2));
            int result = isInsert ? spliceLocation + 1 : spliceLocation;
            return result <= 5 ? result : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean isPositiveStrand(@NotNull String ref, @NotNull String alt, @NotNull String hgvsCoding) {
        char lastBaseOfCoding = hgvsCoding.charAt(hgvsCoding.length() - 1);
        return ref.length() > alt.length()
                ? ref.charAt(ref.length() - 1) == lastBaseOfCoding
                : alt.charAt(alt.length() - 1) == lastBaseOfCoding;
    }

    @NotNull
    private static String[] enforceMinLength(@NotNull String[] parts, int minSize) {
        if (parts.length > minSize) {
            return parts;
        }

        String[] values = new String[minSize];
        for (int i = 0; i < minSize; i++) {
            values[i] = i < parts.length ? parts[i] : Strings.EMPTY;
        }
        System.arraycopy(parts, 0, values, 0, parts.length);

        return values;
    }
}

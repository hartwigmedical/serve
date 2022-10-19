package com.hartwig.serve.common.classification.matchers;

import com.hartwig.serve.common.classification.EventPreprocessor;
import com.hartwig.serve.common.variant.hgvs.HgvsConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotspotMatcher implements EventMatcher {

    private static final int MAX_INFRAME_BASE_LENGTH = 50;

    @NotNull
    private final EventPreprocessor proteinAnnotationExtractor;
    @NotNull
    private final FusionPairMatcher fusionPairMatcher;

    HotspotMatcher(@NotNull final EventPreprocessor proteinAnnotationExtractor, @NotNull final FusionPairMatcher fusionPairMatcher) {
        this.proteinAnnotationExtractor = proteinAnnotationExtractor;
        this.fusionPairMatcher = fusionPairMatcher;
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        String processedEvent = proteinAnnotationExtractor.apply(event);

        if (isProteinAnnotationWithMaxLength(processedEvent, MAX_INFRAME_BASE_LENGTH)) {
            return !isHotspotOnFusionGene(gene, event);
        }

        return false;
    }

    boolean isComplexMatch(@NotNull String gene, @NotNull String event) {
        String processedEvent = proteinAnnotationExtractor.apply(event);

        // Complex matches are hotspots which don't pass "match"
        return isProteinAnnotationWithMaxLength(processedEvent, null) && !(isHotspotOnFusionGene(gene, event)) && (!matches(gene, event));
    }

    private static boolean isProteinAnnotationWithMaxLength(@NotNull String event, @Nullable Integer maxLength) {
        if (isValidFrameshift(event)) {
            return true;
        } else if (event.contains(HgvsConstants.HGVS_RANGE_INDICATOR)) {
            int mutationLength = extractRangeLength(event);
            return mutationLength > 0 && (maxLength == null || mutationLength <= maxLength);
        } else if (event.contains(HgvsConstants.HGVS_DELETION + HgvsConstants.HGVS_INSERTION)) {
            int mutationLength = extractComplexDeletionInsertionLength(event);
            return mutationLength > 0 && (maxLength == null || mutationLength <= maxLength);
        } else if (event.startsWith(HgvsConstants.HGVS_START_LOST)) {
            return true;
        } else {
            return isValidSingleCodonMutation(event);
        }
    }

    private static boolean isValidFrameshift(@NotNull String event) {
        if (event.endsWith(HgvsConstants.HGVS_FRAMESHIFT_SUFFIX) || event.endsWith(HgvsConstants.HGVS_FRAMESHIFT_SUFFIX_WITH_STOP_GAINED)) {
            int frameshiftPosition = event.indexOf(HgvsConstants.HGVS_FRAMESHIFT_SUFFIX);
            if (frameshiftPosition > 1) {
                return Character.isDigit(event.charAt(frameshiftPosition - 1));
            }
        }

        return false;
    }

    private static int extractRangeLength(@NotNull String event) {
        assert event.contains(HgvsConstants.HGVS_RANGE_INDICATOR);

        // Features could be ranges such as E102_I103del. We whitelist specific feature types when analyzing a range.
        String[] parts = event.split(HgvsConstants.HGVS_RANGE_INDICATOR);
        String startPart = parts[0];
        String endPart = parts[1];
        if (endPart.contains(HgvsConstants.HGVS_INSERTION) || endPart.contains(HgvsConstants.HGVS_DUPLICATION) || endPart.contains(
                HgvsConstants.HGVS_DELETION)) {
            int indexOfEvent;
            // Keep in mind that 'del' always comes prior to 'ins' in situations of complex inframes.
            if (endPart.contains(HgvsConstants.HGVS_DELETION)) {
                indexOfEvent = endPart.indexOf(HgvsConstants.HGVS_DELETION);
            } else if (endPart.contains(HgvsConstants.HGVS_DUPLICATION)) {
                indexOfEvent = endPart.indexOf(HgvsConstants.HGVS_DUPLICATION);
            } else {
                indexOfEvent = endPart.indexOf(HgvsConstants.HGVS_INSERTION);
            }

            String startRange = startPart.substring(1);
            String endRange = endPart.substring(1, indexOfEvent);

            if (isLong(startRange) && isLong(endRange)) {
                return (int) (3 * (1 + Long.parseLong(endRange) - Long.parseLong(startRange)));
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private static int extractComplexDeletionInsertionLength(@NotNull String event) {
        assert event.contains(HgvsConstants.HGVS_DELETION + HgvsConstants.HGVS_INSERTION);

        String[] parts = event.split(HgvsConstants.HGVS_DELETION + HgvsConstants.HGVS_INSERTION);

        // Format is expected to be something like D770delinsGY
        if (Character.isDigit(parts[0].charAt(1))) {
            return 3 * parts[1].length();
        } else {
            return -1;
        }
    }

    private static boolean isValidSingleCodonMutation(@NotNull String event) {
        // Single codon mutations are expected to look something like V600E (1 char - N digits - M chars (1 char, or "del" or "dup"))

        if (event.length() < 3) {
            return false;
        }

        if (!Character.isLetter(event.charAt(0))) {
            return false;
        }

        if (!Character.isDigit(event.charAt(1))) {
            return false;
        }

        boolean haveObservedNonDigit = !Character.isDigit(event.charAt(2));
        int firstNotDigit = haveObservedNonDigit ? 2 : -1;
        for (int i = 3; i < event.length(); i++) {
            char charToEvaluate = event.charAt(i);
            if (haveObservedNonDigit && Character.isDigit(charToEvaluate)) {
                return false;
            }
            boolean isDigit = Character.isDigit(charToEvaluate);
            if (!isDigit && firstNotDigit == -1) {
                firstNotDigit = i;
            }

            haveObservedNonDigit = haveObservedNonDigit || !isDigit;
        }

        if (!haveObservedNonDigit) {
            return false;
        }

        String newAminoAcid = event.substring(firstNotDigit);
        // X is a wildcard which should become a codon range rather than a single codon mutation.
        return !newAminoAcid.equals("X") && (newAminoAcid.length() == 1 || newAminoAcid.equals(HgvsConstants.HGVS_DELETION) || newAminoAcid.equals(
                HgvsConstants.HGVS_DUPLICATION));
    }

    private boolean isHotspotOnFusionGene(@NotNull String gene, @NotNull String event) {
        String trimmedEvent = event.trim();
        if (trimmedEvent.contains(" ")) {
            String[] parts = trimmedEvent.split(" ");
            return fusionPairMatcher.matches(gene, parts[0]);
        }
        return false;
    }

    private static boolean isLong(@NotNull String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException exp) {
            return false;
        }
    }
}

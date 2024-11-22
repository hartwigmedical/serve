package com.hartwig.serve.datamodel.molecular.range;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.common.GeneAlteration;
import com.hartwig.serve.datamodel.molecular.common.GeneAlterationComparator;

import org.jetbrains.annotations.NotNull;

public class KnownCodonComparator implements Comparator<KnownCodon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();
    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();

    @Override
    public int compare(@NotNull KnownCodon codon1, @NotNull KnownCodon codon2) {
        int rangeAnnotationCompare = rangeAnnotationComparator.compare(codon1, codon2);
        if (rangeAnnotationCompare != 0) {
            return rangeAnnotationCompare;
        }

        int geneAlterationCompare = geneAlterationComparator.compare(codon1, codon2);
        if (geneAlterationCompare != 0) {
            return geneAlterationCompare;
        }

        int inputTranscriptCompare = codon1.inputTranscript().compareTo(codon2.inputTranscript());
        if (inputTranscriptCompare != 0) {
            return inputTranscriptCompare;
        }

        return Integer.compare(codon1.inputCodonRank(), codon2.inputCodonRank());
    }
}

package com.hartwig.serve.datamodel.range;

import java.util.Comparator;

import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GeneAlterationComparator;

import org.jetbrains.annotations.NotNull;

public class KnownExonComparator implements Comparator<KnownExon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();
    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();

    @Override
    public int compare(@NotNull KnownExon exon1, @NotNull KnownExon exon2) {
        int rangeAnnotationCompare = rangeAnnotationComparator.compare(exon1, exon2);
        if (rangeAnnotationCompare != 0) {
            return rangeAnnotationCompare;
        }

        int geneAlterationCompare = geneAlterationComparator.compare(exon1, exon2);
        if (geneAlterationCompare != 0) {
            return geneAlterationCompare;
        }

        int inputTranscriptCompare = exon1.inputTranscript().compareTo(exon2.inputTranscript());
        if (inputTranscriptCompare != 0) {
            return inputTranscriptCompare;
        }

        return Integer.compare(exon1.inputExonRank(), exon2.inputExonRank());
    }
}

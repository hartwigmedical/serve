package com.hartwig.serve.datamodel.range;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class KnownCodonComparator implements Comparator<KnownCodon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();

    @Override
    public int compare(@NotNull KnownCodon codon1, @NotNull KnownCodon codon2) {
        return rangeAnnotationComparator.compare(codon1.annotation(), codon2.annotation());
    }
}

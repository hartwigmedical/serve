package com.hartwig.serve.datamodel.range;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class KnownExonComparator implements Comparator<KnownExon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();

    @Override
    public int compare(@NotNull KnownExon exon1, @NotNull KnownExon exon2) {
        return rangeAnnotationComparator.compare(exon1.annotation(), exon2.annotation());
    }
}

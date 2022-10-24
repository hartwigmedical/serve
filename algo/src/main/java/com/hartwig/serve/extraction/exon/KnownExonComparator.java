package com.hartwig.serve.extraction.exon;

import java.util.Comparator;

import com.hartwig.serve.datamodel.range.RangeAnnotation;
import com.hartwig.serve.datamodel.range.RangeAnnotationComparator;

import org.jetbrains.annotations.NotNull;

class KnownExonComparator implements Comparator<KnownExon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();

    @Override
    public int compare(@NotNull KnownExon exon1, @NotNull KnownExon exon2) {
        return rangeAnnotationComparator.compare(exon1.annotation(), exon2.annotation());
    }
}

package com.hartwig.serve.extraction.codon;

import java.util.Comparator;

import com.hartwig.serve.datamodel.range.RangeAnnotation;
import com.hartwig.serve.datamodel.range.RangeAnnotationComparator;

import org.jetbrains.annotations.NotNull;

class KnownCodonComparator implements Comparator<KnownCodon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();

    @Override
    public int compare(@NotNull KnownCodon codon1, @NotNull KnownCodon codon2) {
        return rangeAnnotationComparator.compare(codon1.annotation(), codon2.annotation());
    }
}

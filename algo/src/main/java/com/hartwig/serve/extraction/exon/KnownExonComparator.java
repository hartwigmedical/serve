package com.hartwig.serve.extraction.exon;

import java.util.Comparator;

import com.hartwig.serve.datamodel.range.RangeCompare;

import org.jetbrains.annotations.NotNull;

class KnownExonComparator implements Comparator<KnownExon> {

    @Override
    public int compare(@NotNull KnownExon exon1, @NotNull KnownExon exon2) {
        return RangeCompare.compare(exon1.annotation(), exon2.annotation());
    }
}

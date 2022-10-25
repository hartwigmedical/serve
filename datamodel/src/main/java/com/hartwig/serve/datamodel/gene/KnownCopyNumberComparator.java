package com.hartwig.serve.datamodel.gene;

import java.util.Comparator;

import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GeneAlterationComparator;

import org.jetbrains.annotations.NotNull;

class KnownCopyNumberComparator implements Comparator<KnownCopyNumber> {

    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();

    @Override
    public int compare(@NotNull KnownCopyNumber copyNumber1, @NotNull KnownCopyNumber copyNumber2) {
        int geneAlterationCompare = geneAlterationComparator.compare(copyNumber1, copyNumber2);
        if (geneAlterationCompare != 0) {
            return geneAlterationCompare;
        }

        return copyNumber1.type().toString().compareTo(copyNumber2.type().toString());
    }
}

package com.hartwig.serve.datamodel.molecular.gene;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.common.GeneAlteration;
import com.hartwig.serve.datamodel.molecular.common.GeneAlterationComparator;

import org.jetbrains.annotations.NotNull;

public class KnownCopyNumberComparator implements Comparator<KnownCopyNumber> {

    @NotNull
    private final Comparator<GeneAnnotation> geneAnnotationComparator = new GeneAnnotationComparator();
    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();

    @Override
    public int compare(@NotNull KnownCopyNumber copyNumber1, @NotNull KnownCopyNumber copyNumber2) {
        int geneAnnotationCompare = geneAnnotationComparator.compare(copyNumber1, copyNumber2);
        if (geneAnnotationCompare != 0) {
            return geneAnnotationCompare;
        }

        return geneAlterationComparator.compare(copyNumber1, copyNumber2);
    }
}

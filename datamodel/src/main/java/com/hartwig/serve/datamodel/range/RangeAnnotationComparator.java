package com.hartwig.serve.datamodel.range;

import java.util.Comparator;

import com.hartwig.serve.datamodel.common.GenomeRegion;
import com.hartwig.serve.datamodel.common.GenomeRegionComparator;

import org.jetbrains.annotations.NotNull;

public class RangeAnnotationComparator implements Comparator<RangeAnnotation> {

    @NotNull
    private final Comparator<GenomeRegion> genomeRegionComparator = new GenomeRegionComparator();

    @Override
    public int compare(@NotNull RangeAnnotation annotation1, @NotNull RangeAnnotation annotation2) {
        int regionCompare = genomeRegionComparator.compare(annotation1, annotation2);
        if (regionCompare != 0) {
            return regionCompare;
        }

        int geneCompare = annotation1.gene().compareTo(annotation2.gene());
        if (geneCompare != 0) {
            return geneCompare;
        }

        return annotation1.applicableMutationType().toString().compareTo(annotation2.applicableMutationType().toString());
    }
}

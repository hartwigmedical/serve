package com.hartwig.serve.datamodel.range;

import java.util.Comparator;

import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GeneAlterationComparator;
import com.hartwig.serve.datamodel.common.GenomeRegion;
import com.hartwig.serve.datamodel.common.GenomeRegionComparator;

import org.jetbrains.annotations.NotNull;

public class RangeAnnotationComparator implements Comparator<RangeAnnotation> {

    @NotNull
    private final Comparator<GenomeRegion> genomeRegionComparator = new GenomeRegionComparator();
    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();

    @Override
    public int compare(@NotNull RangeAnnotation annotation1, @NotNull RangeAnnotation annotation2) {
        int regionCompare = genomeRegionComparator.compare(annotation1, annotation2);
        if (regionCompare != 0) {
            return regionCompare;
        }

        int geneAlterationCompare = geneAlterationComparator.compare(annotation1, annotation2);
        if (geneAlterationCompare != 0) {
            return geneAlterationCompare;
        }

        int transcriptCompare = annotation1.transcript().compareTo(annotation2.transcript());
        if (transcriptCompare != 0) {
            return transcriptCompare;
        }

        int rankCompare = Integer.compare(annotation1.rank(), annotation2.rank());
        if (rankCompare != 0) {
            return rankCompare;
        }

        return annotation1.applicableMutationType().toString().compareTo(annotation2.applicableMutationType().toString());
    }
}

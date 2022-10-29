package com.hartwig.serve.datamodel.range;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class RangeAnnotationComparatorTest {

    @Test
    public void canSortRangeAnnotations() {
        RangeAnnotation annotation1 = create("gene A", "22", "transcript A", 10, MutationType.INFRAME_DELETION);
        RangeAnnotation annotation2 = create("gene A", "22", "transcript A", 20, MutationType.INFRAME_DELETION);
        RangeAnnotation annotation3 = create("gene A", "22", "transcript A", 20, MutationType.NONSENSE_OR_FRAMESHIFT);
        RangeAnnotation annotation4 = create("gene A", "22", "transcript B", 5, MutationType.ANY);
        RangeAnnotation annotation5 = create("gene B", "22", "transcript A", 5, MutationType.ANY);
        RangeAnnotation annotation6 = create("gene A", "X", "transcript A", 5, MutationType.ANY);

        List<RangeAnnotation> annotations =
                Lists.newArrayList(annotation5, annotation3, annotation6, annotation4, annotation1, annotation2);
        annotations.sort(new RangeAnnotationComparator());

        assertEquals(annotation1, annotations.get(0));
        assertEquals(annotation2, annotations.get(1));
        assertEquals(annotation3, annotations.get(2));
        assertEquals(annotation4, annotations.get(3));
        assertEquals(annotation5, annotations.get(4));
        assertEquals(annotation6, annotations.get(5));
    }

    @NotNull
    private static RangeAnnotation create(@NotNull String gene, @NotNull String chromosome, @NotNull String transcript, int rank,
            @NotNull MutationType applicableMutationType) {
        return RangeTestFactory.createRangeAnnotation(gene,
                GeneRole.UNKNOWN,
                ProteinEffect.UNKNOWN,
                null,
                chromosome,
                0,
                0,
                transcript,
                rank,
                applicableMutationType);
    }
}
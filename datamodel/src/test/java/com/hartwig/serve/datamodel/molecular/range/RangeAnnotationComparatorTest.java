package com.hartwig.serve.datamodel.molecular.range;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.hartwig.serve.datamodel.molecular.MutationType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class RangeAnnotationComparatorTest {

    @Test
    public void canSortRangeAnnotations() {
        RangeAnnotation annotation1 = create("gene A", "22", MutationType.INFRAME_DELETION);
        RangeAnnotation annotation2 = create("gene A", "22", MutationType.NONSENSE_OR_FRAMESHIFT);
        RangeAnnotation annotation3 = create("gene B", "22", MutationType.ANY);
        RangeAnnotation annotation4 = create("gene A", "X", MutationType.ANY);

        List<RangeAnnotation> annotations = new ArrayList<>(List.of(annotation3, annotation4, annotation1, annotation2));
        annotations.sort(new RangeAnnotationComparator());

        assertEquals(annotation1, annotations.get(0));
        assertEquals(annotation2, annotations.get(1));
        assertEquals(annotation3, annotations.get(2));
        assertEquals(annotation4, annotations.get(3));
    }

    @NotNull
    private static RangeAnnotation create(@NotNull String gene, @NotNull String chromosome, @NotNull MutationType applicableMutationType) {
        return RangeTestFactory.createRangeAnnotation(gene, chromosome, 0, 0, applicableMutationType);
    }
}
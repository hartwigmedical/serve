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
    public void canCompareRanges() {
        RangeAnnotation annotation1 = create("X", 1, 10, "gene", "transcript", 1, MutationType.INFRAME_DELETION);
        RangeAnnotation annotation2 = create("X", 1, 10, "gene", "transcript", 1, MutationType.INFRAME_INSERTION);
        RangeAnnotation annotation3 = create("X", 1, 10, "a other gene", "transcript", 1, MutationType.INFRAME_DELETION);
        RangeAnnotation annotation4 = create("X", 2, 10, "gene", "transcript", 1, MutationType.INFRAME_DELETION);

        List<RangeAnnotation> annotations = Lists.newArrayList(annotation1, annotation2, annotation3, annotation4);
        annotations.sort(new RangeAnnotationComparator());

        assertEquals(annotation3, annotations.get(0));
        assertEquals(annotation1, annotations.get(1));
        assertEquals(annotation2, annotations.get(2));
        assertEquals(annotation4, annotations.get(3));
    }

    @NotNull
    private static RangeAnnotation create(@NotNull String chromosome, int start, int end, @NotNull String gene, @NotNull String transcript,
            int rank, @NotNull MutationType applicableMutationType) {
        return new RangeAnnotation() {
            @NotNull
            @Override
            public String gene() {
                return gene;
            }

            @NotNull
            @Override
            public GeneRole geneRole() {
                return GeneRole.UNKNOWN;
            }

            @NotNull
            @Override
            public ProteinEffect proteinEffect() {
                return ProteinEffect.UNKNOWN;
            }

            @NotNull
            @Override
            public String transcript() {
                return transcript;
            }

            @Override
            public int rank() {
                return rank;
            }

            @NotNull
            @Override
            public MutationType applicableMutationType() {
                return applicableMutationType;
            }

            @NotNull
            @Override
            public String chromosome() {
                return chromosome;
            }

            @Override
            public int start() {
                return start;
            }

            @Override
            public int end() {
                return end;
            }
        };
    }
}
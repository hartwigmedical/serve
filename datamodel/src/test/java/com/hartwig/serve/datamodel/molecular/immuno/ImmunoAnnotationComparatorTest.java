package com.hartwig.serve.datamodel.molecular.immuno;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ImmunoAnnotationComparatorTest {

    @Test
    public void canSortFusionPairs() {
        ImmunoAnnotation annotation1 = ImmunoTestFactory.actionableHLABuilder().gene("A").alleleGroup("01").build();
        ImmunoAnnotation annotation2 = ImmunoTestFactory.actionableHLABuilder().gene("A").alleleGroup("02").build();
        ImmunoAnnotation annotation3 = ImmunoTestFactory.actionableHLABuilder().gene("B").alleleGroup("01").build();
        ImmunoAnnotation annotation4 = ImmunoTestFactory.actionableHLABuilder().gene("B").alleleGroup("02").build();

        List<ImmunoAnnotation> fusionPairs = new ArrayList<>(List.of(annotation4, annotation1, annotation3, annotation2));
        fusionPairs.sort(new ImmunoAnnotationComparator());

        assertEquals(annotation1, fusionPairs.get(0));
        assertEquals(annotation2, fusionPairs.get(1));
        assertEquals(annotation3, fusionPairs.get(2));
        assertEquals(annotation4, fusionPairs.get(3));
    }
}
package com.hartwig.serve.datamodel.molecular.fusion;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class FusionPairComparatorTest {

    @Test
    public void canSortFusionPairs() {
        FusionPair pair1 = FusionTestFactory.createFusionPair("A", "B", 2, 3);
        FusionPair pair2 = FusionTestFactory.createFusionPair("A", "B", null, null);
        FusionPair pair3 = FusionTestFactory.createFusionPair("A", "C", null, null);
        FusionPair pair4 = FusionTestFactory.createFusionPair("B", "C", null, null);
        FusionPair pair5 = FusionTestFactory.createFusionPair("X", "A", null, null);

        List<FusionPair> fusionPairs = Lists.newArrayList(pair4, pair1, pair5, pair3, pair2);
        fusionPairs.sort(new FusionPairComparator());

        assertEquals(pair1, fusionPairs.get(0));
        assertEquals(pair2, fusionPairs.get(1));
        assertEquals(pair3, fusionPairs.get(2));
        assertEquals(pair4, fusionPairs.get(3));
        assertEquals(pair5, fusionPairs.get(4));
    }
}
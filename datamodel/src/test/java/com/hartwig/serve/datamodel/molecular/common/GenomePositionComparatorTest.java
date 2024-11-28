package com.hartwig.serve.datamodel.molecular.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GenomePositionComparatorTest {

    @Test
    public void canSortGenomePositions() {
        GenomePosition position1 = CommonTestFactory.createGenomePosition("chr1", 10);
        GenomePosition position2 = CommonTestFactory.createGenomePosition("1", 20);
        GenomePosition position3 = CommonTestFactory.createGenomePosition("2", 5);
        GenomePosition position4 = CommonTestFactory.createGenomePosition("X", 1);

        List<GenomePosition> positions = new ArrayList<>(List.of(position3, position1, position4, position2));
        positions.sort(new GenomePositionComparator());

        assertEquals(position1, positions.get(0));
        assertEquals(position2, positions.get(1));
        assertEquals(position3, positions.get(2));
        assertEquals(position4, positions.get(3));
    }
}
package com.hartwig.serve.datamodel.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class GenomeRegionComparatorTest {

    @Test
    public void canSortGenomeRegions() {
        GenomeRegion region1 = CommonTestFactory.createGenomeRegion("1", 10, 20);
        GenomeRegion region2 = CommonTestFactory.createGenomeRegion("1", 10, 25);
        GenomeRegion region3 = CommonTestFactory.createGenomeRegion("1", 15, 20);
        GenomeRegion region4 = CommonTestFactory.createGenomeRegion("X", 5, 20);
        GenomeRegion region5 = CommonTestFactory.createGenomeRegion("X", 10, 15);

        List<GenomeRegion> regions = Lists.newArrayList(region3, region5, region1, region2, region4);
        regions.sort(new GenomeRegionComparator());

        assertEquals(region1, regions.get(0));
        assertEquals(region2, regions.get(1));
        assertEquals(region3, regions.get(2));
        assertEquals(region4, regions.get(3));
        assertEquals(region5, regions.get(4));
    }
}
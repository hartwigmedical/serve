package com.hartwig.serve.datamodel.hotspot;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class ActionableHotspotComparatorTest {

    @Test
    public void canSortActionableHotspots() {
        ActionableHotspot hotspot1 = HotspotTestFactory.actionableHotspotBuilder().chromosome("1").sourceEvent("event 1").build();
        ActionableHotspot hotspot2 = HotspotTestFactory.actionableHotspotBuilder().chromosome("1").sourceEvent("event 2").build();
        ActionableHotspot hotspot3 = HotspotTestFactory.actionableHotspotBuilder().chromosome("2").sourceEvent("event 1").build();

        List<ActionableHotspot> hotspots = Lists.newArrayList(hotspot3, hotspot1, hotspot2);
        hotspots.sort(new ActionableHotspotComparator());

        assertEquals(hotspot1, hotspots.get(0));
        assertEquals(hotspot2, hotspots.get(1));
        assertEquals(hotspot3, hotspots.get(2));
    }
}
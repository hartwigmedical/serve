package com.hartwig.serve.datamodel.molecular.hotspot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ActionableHotspotComparatorTest {

    @Test
    public void canSortActionableHotspots() {
        ActionableHotspot hotspot1 = HotspotTestFactory.actionableHotspotBuilder().chromosome("1").sourceEvent("event1").build();
        ActionableHotspot hotspot2 = HotspotTestFactory.actionableHotspotBuilder().chromosome("1").sourceEvent("event2").build();
        ActionableHotspot hotspot3 = HotspotTestFactory.actionableHotspotBuilder().chromosome("2").sourceEvent("event3").build();

        List<ActionableHotspot> hotspots = new ArrayList<>(List.of(hotspot3, hotspot1, hotspot2));
        hotspots.sort(new ActionableHotspotComparator());

        assertEquals(hotspot1, hotspots.get(0));
        assertEquals(hotspot2, hotspots.get(1));
        assertEquals(hotspot3, hotspots.get(2));
    }
}
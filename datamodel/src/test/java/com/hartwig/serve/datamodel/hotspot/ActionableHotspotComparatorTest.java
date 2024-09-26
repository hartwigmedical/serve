package com.hartwig.serve.datamodel.hotspot;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.junit.Test;

public class ActionableHotspotComparatorTest {

    @Test
    public void canSortActionableHotspots() {
        ActionableHotspot hotspot1 = HotspotTestFactory.actionableHotspotBuilder().chromosome("1").evidenceLevel(EvidenceLevel.A).build();
        ActionableHotspot hotspot2 = HotspotTestFactory.actionableHotspotBuilder().chromosome("1").evidenceLevel(EvidenceLevel.B).build();
        ActionableHotspot hotspot3 = HotspotTestFactory.actionableHotspotBuilder().chromosome("2").evidenceLevel(EvidenceLevel.A).build();

        List<ActionableHotspot> hotspots = Lists.newArrayList(hotspot3, hotspot1, hotspot2);
        hotspots.sort(new ActionableHotspotComparator());

        assertEquals(hotspot1, hotspots.get(0));
        assertEquals(hotspot2, hotspots.get(1));
        assertEquals(hotspot3, hotspots.get(2));
    }
}
package com.hartwig.serve.datamodel.molecular.hotspot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.hartwig.serve.datamodel.molecular.common.GeneRole;

import org.junit.Test;

public class KnownHotspotComparatorTest {

    @Test
    public void canSortKnownHotspots() {
        KnownHotspot hotspot1 = HotspotTestFactory.knownHotspotBuilder().gene("gene A").geneRole(GeneRole.ONCO).build();
        KnownHotspot hotspot2 = HotspotTestFactory.knownHotspotBuilder().gene("gene A").geneRole(GeneRole.TSG).build();
        KnownHotspot hotspot3 = HotspotTestFactory.knownHotspotBuilder().gene("gene B").geneRole(GeneRole.ONCO).build();

        List<KnownHotspot> hotspots = new ArrayList<>(List.of(hotspot3, hotspot2, hotspot1));
        hotspots.sort(new KnownHotspotComparator());

        assertEquals(hotspot1, hotspots.get(0));
        assertEquals(hotspot2, hotspots.get(1));
        assertEquals(hotspot3, hotspots.get(2));
    }
}
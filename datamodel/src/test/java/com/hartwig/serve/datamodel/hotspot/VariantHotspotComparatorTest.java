package com.hartwig.serve.datamodel.hotspot;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class VariantHotspotComparatorTest {

    @Test
    public void canSortVariantHotspots() {
        VariantHotspot hotspot1 = create("gene B", "1", 10, "A", "T");
        VariantHotspot hotspot2 = create("gene B", "1", 15, "A", "C");
        VariantHotspot hotspot3 = create("gene B", "1", 15, "A", "T");
        VariantHotspot hotspot4 = create("gene B", "1", 15, "T", "A");
        VariantHotspot hotspot5 = create("gene A", "X", 10, "A", "T");
        VariantHotspot hotspot6 = create("gene A", "X", 15, "A", "T");

        List<VariantHotspot> hotspots = Lists.newArrayList(hotspot4, hotspot3, hotspot1, hotspot6, hotspot5, hotspot2);
        hotspots.sort(new VariantHotspotComparator());

        assertEquals(hotspot1, hotspots.get(0));
        assertEquals(hotspot2, hotspots.get(1));
        assertEquals(hotspot3, hotspots.get(2));
        assertEquals(hotspot4, hotspots.get(3));
        assertEquals(hotspot5, hotspots.get(4));
        assertEquals(hotspot6, hotspots.get(5));
    }

    @NotNull
    private static VariantHotspot create(@NotNull String gene, @NotNull String chromosome, int position, @NotNull String ref,
            @NotNull String alt) {
        return HotspotTestFactory.createVariantHotspot(gene, GeneRole.UNKNOWN, ProteinEffect.UNKNOWN, null, chromosome, position, ref, alt);
    }

}
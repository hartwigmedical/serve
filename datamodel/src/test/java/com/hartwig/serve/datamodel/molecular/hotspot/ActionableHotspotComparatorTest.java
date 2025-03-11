package com.hartwig.serve.datamodel.molecular.hotspot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableHotspotComparatorTest {

    @Test
    public void canSortActionableHotspots() {
        ActionableHotspot hotspot1 =
                HotspotTestFactory.actionableHotspotBuilder().sourceEvent("event1").addVariants(createWithChromosome("1")).build();
        ActionableHotspot hotspot2 =
                HotspotTestFactory.actionableHotspotBuilder().sourceEvent("event1").addVariants(createWithChromosome("2")).build();
        ActionableHotspot hotspot3 =
                HotspotTestFactory.actionableHotspotBuilder().sourceEvent("event1").addVariants(createWithChromosome("2")).build();

        List<ActionableHotspot> hotspots = new ArrayList<>(List.of(hotspot3, hotspot1, hotspot2));
        hotspots.sort(new ActionableHotspotComparator());

        assertEquals(hotspot1, hotspots.get(0));
        assertEquals(hotspot2, hotspots.get(1));
        assertEquals(hotspot3, hotspots.get(2));
    }

    @NotNull
    private static VariantAnnotation createWithChromosome(@NotNull String chromosome) {
        return HotspotTestFactory.createVariantAnnotation("gene", chromosome, 0, "ref", "alt");
    }
}
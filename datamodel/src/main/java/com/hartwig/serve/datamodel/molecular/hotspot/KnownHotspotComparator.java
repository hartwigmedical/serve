package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.common.GeneAlteration;
import com.hartwig.serve.datamodel.molecular.common.GeneAlterationComparator;

import org.jetbrains.annotations.NotNull;

public class KnownHotspotComparator implements Comparator<KnownHotspot> {

    @NotNull
    private final Comparator<VariantHotspot> variantHotspotComparator = new VariantHotspotComparator();
    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();

    @Override
    public int compare(@NotNull KnownHotspot hotspot1, @NotNull KnownHotspot hotspot2) {
        int hotspotCompare = variantHotspotComparator.compare(hotspot1, hotspot2);
        if (hotspotCompare != 0) {
            return hotspotCompare;
        }

        return geneAlterationComparator.compare(hotspot1, hotspot2);
    }
}

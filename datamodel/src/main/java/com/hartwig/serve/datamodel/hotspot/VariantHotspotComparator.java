package com.hartwig.serve.datamodel.hotspot;

import java.util.Comparator;

import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GeneAlterationComparator;
import com.hartwig.serve.datamodel.common.Variant;
import com.hartwig.serve.datamodel.common.VariantComparator;

import org.jetbrains.annotations.NotNull;

public class VariantHotspotComparator implements Comparator<VariantHotspot> {

    @NotNull
    private final Comparator<Variant> variantComparator = new VariantComparator();
    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();

    @Override
    public int compare(@NotNull VariantHotspot hotspot1, @NotNull VariantHotspot hotspot2) {
        int variantCompare = variantComparator.compare(hotspot1, hotspot2);
        if (variantCompare != 0) {
            return variantCompare;
        }

        return geneAlterationComparator.compare(hotspot1, hotspot2);
    }
}

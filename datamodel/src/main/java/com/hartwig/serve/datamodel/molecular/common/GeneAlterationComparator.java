package com.hartwig.serve.datamodel.molecular.common;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class GeneAlterationComparator implements Comparator<GeneAlteration> {

    @Override
    public int compare(@NotNull GeneAlteration alteration1, @NotNull GeneAlteration alteration2) {
        int geneRoleCompare = alteration1.geneRole().toString().compareTo(alteration2.geneRole().toString());
        if (geneRoleCompare != 0) {
            return geneRoleCompare;
        }

        int proteinEffectCompare = alteration1.proteinEffect().toString().compareTo(alteration2.proteinEffect().toString());
        if (proteinEffectCompare != 0) {
            return proteinEffectCompare;
        }

        return CompareFunctions.compareNullableBoolean(alteration1.associatedWithDrugResistance(),
                alteration2.associatedWithDrugResistance());
    }
}

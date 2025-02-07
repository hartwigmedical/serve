package com.hartwig.serve.datamodel.molecular;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class MolecularCriteriumComparator implements Comparator<MolecularCriterium> {

    @Override
    public int compare(@NotNull MolecularCriterium molecularCriterium1, @NotNull MolecularCriterium molecularCriterium2) {
        int allOfAnyHotspotsCompare = CompareFunctions.compareSetOfSetOfComparable(molecularCriterium1.oneOfEachHotspots(),
                molecularCriterium2.oneOfEachHotspots());
        if (allOfAnyHotspotsCompare != 0) {
            return allOfAnyHotspotsCompare;
        }

        int codonsCompare = CompareFunctions.compareSetOfComparable(molecularCriterium1.codons(), molecularCriterium2.codons());
        if (codonsCompare != 0) {
            return codonsCompare;
        }

        int exonsCompare = CompareFunctions.compareSetOfComparable(molecularCriterium1.exons(), molecularCriterium2.exons());
        if (exonsCompare != 0) {
            return exonsCompare;
        }

        int genesCompare = CompareFunctions.compareSetOfComparable(molecularCriterium1.genes(), molecularCriterium2.genes());
        if (genesCompare != 0) {
            return genesCompare;
        }

        int fusionsCompare = CompareFunctions.compareSetOfComparable(molecularCriterium1.fusions(), molecularCriterium2.fusions());
        if (fusionsCompare != 0) {
            return fusionsCompare;
        }

        int characteristicsCompare =
                CompareFunctions.compareSetOfComparable(molecularCriterium1.characteristics(), molecularCriterium2.characteristics());
        if (characteristicsCompare != 0) {
            return characteristicsCompare;
        }

        return CompareFunctions.compareSetOfComparable(molecularCriterium1.hla(), molecularCriterium2.hla());
    }
}

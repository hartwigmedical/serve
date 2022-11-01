package com.hartwig.serve.datamodel.fusion;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class FusionPairComparator implements Comparator<FusionPair> {

    @Override
    public int compare(@NotNull FusionPair pair1, @NotNull FusionPair pair2) {
        int geneUpCompare = pair1.geneUp().compareTo(pair2.geneUp());
        if (geneUpCompare != 0) {
            return geneUpCompare;
        }

        int geneDownCompare = pair1.geneDown().compareTo(pair2.geneDown());
        if (geneDownCompare != 0) {
            return geneDownCompare;
        }

        int minExonUpCompare = CompareFunctions.compareNullableIntegers(pair1.minExonUp(), pair2.minExonUp());
        if (minExonUpCompare != 0) {
            return minExonUpCompare;
        }

        return CompareFunctions.compareNullableIntegers(pair1.minExonDown(), pair2.minExonDown());
    }
}

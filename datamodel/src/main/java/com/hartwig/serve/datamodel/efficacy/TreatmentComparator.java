package com.hartwig.serve.datamodel.efficacy;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class TreatmentComparator implements Comparator<Treatment> {

    @Override
    public int compare(@NotNull Treatment treatment1, @NotNull Treatment treatment2) {
        int nameCompare = treatment1.name().compareTo(treatment2.name());
        if (nameCompare != 0) {
            return nameCompare;
        }

        int drugClassCompare = CompareFunctions.compareSetOfComparable(treatment1.treatmentApproachesDrugClass(),
                treatment2.treatmentApproachesDrugClass());
        if (drugClassCompare != 0) {
            return drugClassCompare;
        }

        return CompareFunctions.compareSetOfComparable(treatment1.treatmentApproachesTherapy(), treatment2.treatmentApproachesTherapy());
    }
}

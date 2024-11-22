package com.hartwig.serve.datamodel.trial;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class HospitalComparator implements Comparator<Hospital> {

    @Override
    public int compare(@NotNull Hospital hospital1, @NotNull Hospital hospital2) {
        int nameCompare = hospital1.name().compareTo(hospital2.name());
        if (nameCompare != 0) {
            return nameCompare;
        }

        return CompareFunctions.compareNullableBoolean(hospital1.isChildrensHospital(), hospital2.isChildrensHospital());
    }
}

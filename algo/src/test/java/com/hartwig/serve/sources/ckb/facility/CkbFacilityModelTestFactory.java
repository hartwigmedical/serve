package com.hartwig.serve.sources.ckb.facility;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class CkbFacilityModelTestFactory {

    private CkbFacilityModelTestFactory() {
    }

    @NotNull
    public static CkbFacilityModel createEmptyFacilityModel() {
        return new CkbFacilityModel(Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
    }

    @NotNull
    public static CkbFacilityModel createProperFacilityModel() {
        List<CkbFacilityCityEntry> facilityCityEntries = Lists.newArrayList();
        CkbFacilityCityEntry entry1 = ImmutableCkbFacilityCityEntry.builder().city("Almere").curatedFacilityName("Flevoziekenhuis").build();

        facilityCityEntries.add(entry1);

        List<CkbFacilityNameEntry> facilityNameEntries = Lists.newArrayList();
        CkbFacilityNameEntry entry2 = ImmutableCkbFacilityNameEntry.builder().facilityName("Erasmus").curatedFacilityName("EMC").build();

        facilityNameEntries.add(entry2);

        List<CkbFacilityZipEntry> facilityZipEntries = Lists.newArrayList();
        CkbFacilityZipEntry entry3 =
                ImmutableCkbFacilityZipEntry.builder().city("Groningen").zip("9713").curatedFacilityName("UMCG").build();

        facilityZipEntries.add(entry3);

        return new CkbFacilityModel(facilityCityEntries, facilityNameEntries, facilityZipEntries);
    }
}
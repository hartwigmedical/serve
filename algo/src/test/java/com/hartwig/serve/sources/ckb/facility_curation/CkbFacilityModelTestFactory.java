package com.hartwig.serve.sources.ckb.facility_curation;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class CkbFacilityModelTestFactory {

    private CkbFacilityModelTestFactory() {
    }

    @NotNull
    public static CkbFacilityCurationModel createEmptyFacilityModel() {
        return new CkbFacilityCurationModel(Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
    }

    @NotNull
    public static CkbFacilityCurationModel createProperFacilityModel() {
        List<CkbFacilityCurationCityEntry> facilityCityEntries = Lists.newArrayList();
        CkbFacilityCurationCityEntry entry1 =
                ImmutableCkbFacilityCurationCityEntry.builder().city("Almere").curatedFacilityName("Flevoziekenhuis").build();

        facilityCityEntries.add(entry1);

        List<CkbFacilityCurationNameEntry> facilityNameEntries = Lists.newArrayList();
        CkbFacilityCurationNameEntry entry2 =
                ImmutableCkbFacilityCurationNameEntry.builder().facilityName("Erasmus").curatedFacilityName("EMC").build();

        facilityNameEntries.add(entry2);

        List<CkbFacilityCurationZipEntry> facilityZipEntries = Lists.newArrayList();
        CkbFacilityCurationZipEntry entry3 =
                ImmutableCkbFacilityCurationZipEntry.builder().city("Groningen").zip("9713").curatedFacilityName("UMCG").build();

        facilityZipEntries.add(entry3);

        List<CkbFacilityCurationFilterEntry> facilityFilterEntries = Lists.newArrayList();
        CkbFacilityCurationFilterEntry entry4 = ImmutableCkbFacilityCurationFilterEntry.builder()
                .facilityName("Amsterdam UMC")
                .city("Amsterdam")
                .zip("")
                .curatedFacilityName("Amsterdam UMC")
                .build();

        facilityFilterEntries.add(entry4);

        return new CkbFacilityCurationModel(facilityCityEntries, facilityNameEntries, facilityZipEntries, facilityFilterEntries);
    }
}
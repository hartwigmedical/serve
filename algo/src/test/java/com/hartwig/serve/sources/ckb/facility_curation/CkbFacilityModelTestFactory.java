package com.hartwig.serve.sources.ckb.facility_curation;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class CkbFacilityModelTestFactory {

    private CkbFacilityModelTestFactory() {
    }

    @NotNull
    public static CkbFacilityCurationModel createEmptyFacilityModel() {
        return new CkbFacilityCurationModel(Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
    }

    @NotNull
    public static CkbFacilityCurationModel createProperFacilityModel() {
        List<CkbFacilityCurationNameEntry> facilityNameEntries = Lists.newArrayList();
        CkbFacilityCurationNameEntry entry1 = ImmutableCkbFacilityCurationNameEntry.builder()
                .facilityName("Erasmus")
                .city("Rotterdam")
                .curatedFacilityName("EMC")
                .build();

        facilityNameEntries.add(entry1);

        List<CkbFacilityCurationZipEntry> facilityZipEntries = Lists.newArrayList();
        CkbFacilityCurationZipEntry entry2 =
                ImmutableCkbFacilityCurationZipEntry.builder().city("Groningen").zip("9713").curatedFacilityName("UMCG").build();

        facilityZipEntries.add(entry2);

        List<CkbFacilityCurationFilterEntry> facilityFilterEntries = Lists.newArrayList();
        CkbFacilityCurationFilterEntry entry3 = ImmutableCkbFacilityCurationFilterEntry.builder()
                .facilityName("Amsterdam UMC")
                .city("Amsterdam")
                .zip("")
                .curatedFacilityName("Amsterdam UMC")
                .build();

        facilityFilterEntries.add(entry3);

        return new CkbFacilityCurationModel(facilityNameEntries, facilityZipEntries, facilityFilterEntries);
    }
}
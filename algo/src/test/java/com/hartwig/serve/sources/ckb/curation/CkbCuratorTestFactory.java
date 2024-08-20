package com.hartwig.serve.sources.ckb.curation;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class CkbCuratorTestFactory {

    private CkbCuratorTestFactory() {
    }

    @NotNull
    public static CkbCurator createProperModel() {
        List<CkbFacilityCurationNameEntry> facilityNameEntries = Lists.newArrayList();
        CkbFacilityCurationNameEntry entry1 = ImmutableCkbFacilityCurationNameEntry.builder()
                .facilityName("erasmus")
                .city("rotterdam")
                .curatedFacilityName("EMC")
                .build();

        facilityNameEntries.add(entry1);

        List<CkbFacilityCurationZipEntry> facilityZipEntries = Lists.newArrayList();
        CkbFacilityCurationZipEntry entry2 =
                ImmutableCkbFacilityCurationZipEntry.builder().city("groningen").zip("9713").curatedFacilityName("UMCG").build();
        CkbFacilityCurationZipEntry entry3 =
                ImmutableCkbFacilityCurationZipEntry.builder().city("almere").zip("").curatedFacilityName("Flevoziekenhuis").build();

        facilityZipEntries.add(entry2);
        facilityZipEntries.add(entry3);

        List<CkbFacilityCurationManualEntry> facilityFilterEntries = Lists.newArrayList();
        CkbFacilityCurationManualEntry entry4 = ImmutableCkbFacilityCurationManualEntry.builder()
                .facilityName("Amsterdam UMC")
                .city("Amsterdam")
                .zip("")
                .curatedFacilityName("Amsterdam UMC")
                .build();

        facilityFilterEntries.add(entry4);

        return new CkbCurator(facilityNameEntries, facilityZipEntries, facilityFilterEntries);
    }
}
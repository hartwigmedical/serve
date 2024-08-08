package com.hartwig.serve.sources.ckb.facility_curation;

import java.util.List;

import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbFacilityCurationModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbFacilityCurationModel.class);

    @NotNull
    private final List<CkbFacilityCurationCityEntry> facilityCityEntries;

    @NotNull
    private final List<CkbFacilityCurationNameEntry> facilityNameEntries;

    @NotNull
    private final List<CkbFacilityCurationZipEntry> facilityZipEntries;

    @NotNull
    private final List<CkbFacilityCurationFilterEntry> facilityFilterEntries;

    public CkbFacilityCurationModel(@NotNull final List<CkbFacilityCurationCityEntry> facilityCityList,
            @NotNull final List<CkbFacilityCurationNameEntry> facilityNameList,
            @NotNull final List<CkbFacilityCurationZipEntry> facilityZipList,
            @NotNull final List<CkbFacilityCurationFilterEntry> facilityFilterList) {
        this.facilityCityEntries = facilityCityList;
        this.facilityNameEntries = facilityNameList;
        this.facilityZipEntries = facilityZipList;
        this.facilityFilterEntries = facilityFilterList;
    }

    public String curateFacilityName(@NotNull Location location) {
        for (CkbFacilityCurationCityEntry facilityCityEntry : facilityCityEntries) {
            if (location.city().toLowerCase().contains(facilityCityEntry.city().toLowerCase())) {
                return facilityCityEntry.curatedFacilityName();
            }
        }

        if (location.facility() != null) {
            for (CkbFacilityCurationNameEntry facilityNameEntry : facilityNameEntries) {
                if (location.facility().toLowerCase().contains(facilityNameEntry.facilityName().toLowerCase())) {
                    return facilityNameEntry.curatedFacilityName();
                }
            }
        }

        if (location.zip() != null) {
            for (CkbFacilityCurationZipEntry facilityZipEntry : facilityZipEntries) {
                if (location.city().toLowerCase().contains(facilityZipEntry.city().toLowerCase()) && location.zip()
                        .replaceAll("\\s", "")
                        .contains(facilityZipEntry.zip())) {
                    return facilityZipEntry.curatedFacilityName();
                }
            }
        }

        for (CkbFacilityCurationFilterEntry facilityFilterEntry : facilityFilterEntries) {
            if (equalStringsIncludingNull(location.facility(), facilityFilterEntry.facilityName()) && location.city()
                    .equals(facilityFilterEntry.city()) && equalStringsIncludingNull(location.zip(), facilityFilterEntry.zip())) {
                return facilityFilterEntry.curatedFacilityName();
            }
        }

        LOGGER.warn(" Couldn't curate facility name for location '{}'", location);
        return "Unknown (" + location.city() + ")";
    }

    private Boolean equalStringsIncludingNull(@Nullable String string1, @NotNull String string2) {
        if (string1 == null && string2.equals("")) {
            return true;
        }
        if (string1 == null) {
            return false;
        }
        return string1.equals(string2);
    }
}

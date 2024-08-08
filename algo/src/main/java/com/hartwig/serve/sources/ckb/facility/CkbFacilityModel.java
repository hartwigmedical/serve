package com.hartwig.serve.sources.ckb.facility;

import java.util.List;

import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbFacilityModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbFacilityModel.class);

    @NotNull
    private final List<CkbFacilityCityEntry> facilityCityEntries;

    @NotNull
    private final List<CkbFacilityNameEntry> facilityNameEntries;

    @NotNull
    private final List<CkbFacilityZipEntry> facilityZipEntries;

    @NotNull
    private final List<CkbFacilityFilterEntry> facilityFilterEntries;

    public CkbFacilityModel(@NotNull final List<CkbFacilityCityEntry> facilityCityList,
            @NotNull final List<CkbFacilityNameEntry> facilityNameList, @NotNull final List<CkbFacilityZipEntry> facilityZipList,
            @NotNull final List<CkbFacilityFilterEntry> facilityFilterList) {
        this.facilityCityEntries = facilityCityList;
        this.facilityNameEntries = facilityNameList;
        this.facilityZipEntries = facilityZipList;
        this.facilityFilterEntries = facilityFilterList;
    }

    public String curateFacilityName(@NotNull Location location) {
        for (CkbFacilityCityEntry facilityCityEntry : facilityCityEntries) {
            if (location.city().toLowerCase().contains(facilityCityEntry.city().toLowerCase())) {
                return facilityCityEntry.curatedFacilityName();
            }
        }

        if (location.facility() != null) {
            for (CkbFacilityNameEntry facilityNameEntry : facilityNameEntries) {
                if (location.facility().toLowerCase().contains(facilityNameEntry.facilityName().toLowerCase())) {
                    return facilityNameEntry.curatedFacilityName();
                }
            }
        }

        if (location.zip() != null) {
            for (CkbFacilityZipEntry facilityZipEntry : facilityZipEntries) {
                if (location.city().toLowerCase().contains(facilityZipEntry.city().toLowerCase()) && location.zip()
                        .replaceAll("\\s", "")
                        .contains(facilityZipEntry.zip())) {
                    return facilityZipEntry.curatedFacilityName();
                }
            }
        }

        for (CkbFacilityFilterEntry facilityFilterEntry : facilityFilterEntries) {
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

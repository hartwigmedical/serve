package com.hartwig.serve.sources.ckb.facility;

import java.util.List;

import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.jetbrains.annotations.NotNull;

public class CkbFacilityModel {

    @NotNull
    private final List<CkbFacilityCityEntry> facilityCityEntries;

    @NotNull
    private final List<CkbFacilityNameEntry> facilityNameEntries;

    @NotNull
    private final List<CkbFacilityZipEntry> facilityZipEntries;

    public CkbFacilityModel(@NotNull final List<CkbFacilityCityEntry> facilityCityList,
            @NotNull final List<CkbFacilityNameEntry> facilityNameList, @NotNull final List<CkbFacilityZipEntry> facilityZipList) {
        this.facilityCityEntries = facilityCityList;
        this.facilityNameEntries = facilityNameList;
        this.facilityZipEntries = facilityZipList;
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

        return "Unknown (" + location.city() + ")";
    }
}

package com.hartwig.serve.sources.ckb.facility_curation;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbFacilityCurationModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbFacilityCurationModel.class);

    @NotNull
    private final List<CkbFacilityCurationNameEntry> facilityCurationNameEntries;

    @NotNull
    private final List<CkbFacilityCurationZipEntry> facilityCurationZipEntries;

    @NotNull
    private final List<CkbFacilityCurationFilterEntry> facilityCurationFilterEntries;

    public CkbFacilityCurationModel(@NotNull final List<CkbFacilityCurationNameEntry> facilityCurationNameList,
            @NotNull final List<CkbFacilityCurationZipEntry> facilityCurationZipList,
            @NotNull final List<CkbFacilityCurationFilterEntry> facilityCurationFilterList) {
        this.facilityCurationNameEntries = facilityCurationNameList;
        this.facilityCurationZipEntries = facilityCurationZipList;
        this.facilityCurationFilterEntries = facilityCurationFilterList;
    }

    @VisibleForTesting
    public String curateFacilityName(@NotNull Location location) {
        if (location.zip() != null) {
            for (CkbFacilityCurationZipEntry facilityCurationZipEntry : facilityCurationZipEntries) {
                if (location.city().toLowerCase().equals(facilityCurationZipEntry.city())) {
                    if (facilityCurationZipEntry.zip() == null) {
                        return facilityCurationZipEntry.curatedFacilityName();
                    } else if (location.zip().toLowerCase().replaceAll("\\s", "").contains(facilityCurationZipEntry.zip())) {
                        return facilityCurationZipEntry.curatedFacilityName();
                    }
                }
            }
        }

        if (location.facility() != null) {
            for (CkbFacilityCurationNameEntry facilityCurationNameEntry : facilityCurationNameEntries) {
                if (location.facility().toLowerCase().contains(facilityCurationNameEntry.facilityName()) && location.city()
                        .toLowerCase()
                        .equals(facilityCurationNameEntry.city())) {
                    return facilityCurationNameEntry.curatedFacilityName();
                }
            }
        }

        for (CkbFacilityCurationFilterEntry facilityCurationFilterEntry : facilityCurationFilterEntries) {
            if (equalStringsOrNull(location.facility().toLowerCase(), facilityCurationFilterEntry.facilityName()) && location.city()
                    .toLowerCase()
                    .equals(facilityCurationFilterEntry.city()) && equalStringsOrNull(location.zip().toLowerCase(),
                    facilityCurationFilterEntry.zip())) {
                return facilityCurationFilterEntry.curatedFacilityName();
            }
        }

        LOGGER.warn(" Couldn't curate facility name for location '{}'", location);
        return "Unknown (" + location.city() + ")";
    }

    @VisibleForTesting
    private Boolean equalStringsOrNull(@Nullable String string1, @NotNull String string2) {
        if (string1 == null && string2.equals("")) {
            return true;
        }
        if (string1 == null) {
            return false;
        }
        return string1.equals(string2);
    }
}

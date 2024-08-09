package com.hartwig.serve.sources.ckb.facility_curation;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
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

    @NotNull
    private final Set<CkbFacilityCurationFilterEntry> usedFacilityCurationFilterEntries = Sets.newHashSet();

    public CkbFacilityCurationModel(@NotNull final List<CkbFacilityCurationNameEntry> facilityCurationNameList,
            @NotNull final List<CkbFacilityCurationZipEntry> facilityCurationZipList,
            @NotNull final List<CkbFacilityCurationFilterEntry> facilityCurationFilterList) {
        this.facilityCurationNameEntries = facilityCurationNameList;
        this.facilityCurationZipEntries = facilityCurationZipList;
        this.facilityCurationFilterEntries = facilityCurationFilterList;
    }

    @NotNull
    public String curateFacilityName(@NotNull Location location) {
        for (CkbFacilityCurationZipEntry facilityCurationZipEntry : facilityCurationZipEntries) {
            if (containsWord(facilityCurationZipEntry.city(), location.city().toLowerCase())) {
                String zip = location.zip() != null ? location.zip().toLowerCase().replaceAll("\\s", "") : "";
                if ((facilityCurationZipEntry.zip().equals("")) || (zip.contains(facilityCurationZipEntry.zip()))) {
                    return facilityCurationZipEntry.curatedFacilityName();
                }
            }
        }

        if (location.facility() != null) {
            for (CkbFacilityCurationNameEntry facilityCurationNameEntry : facilityCurationNameEntries) {
                if (containsWord(facilityCurationNameEntry.facilityName(), location.facility().toLowerCase()) && containsWord(
                        facilityCurationNameEntry.city(),
                        location.city().toLowerCase())) {
                    return facilityCurationNameEntry.curatedFacilityName();
                }
            }
        }

        for (CkbFacilityCurationFilterEntry facilityCurationFilterEntry : facilityCurationFilterEntries) {
            if (equalStringsOrNull(location.facility(), facilityCurationFilterEntry.facilityName()) && location.city()
                    .equals(facilityCurationFilterEntry.city()) && equalStringsOrNull(location.zip(), facilityCurationFilterEntry.zip())) {
                usedFacilityCurationFilterEntries.add(facilityCurationFilterEntry);
                return facilityCurationFilterEntry.curatedFacilityName();
            }
        }

        LOGGER.warn(" Couldn't curate facility name for location '{}'", location);
        return "Unknown (" + location.city() + ")";
    }

    public void reportUnusedFacilityCurationFilterEntries() {
        int unusedFacilityCurationFilterCount = 0;
        for (CkbFacilityCurationFilterEntry entry : facilityCurationFilterEntries) {
            if (!usedFacilityCurationFilterEntries.contains(entry)) {
                unusedFacilityCurationFilterCount++;
                LOGGER.warn(" Facility curation filter entry '{}' hasn't been used for CKB filtering", entry);
            }
        }
        LOGGER.debug(" Found {} unused facility curation filters during CKB filtering", unusedFacilityCurationFilterCount);
    }

    @VisibleForTesting
    @NotNull
    Boolean equalStringsOrNull(@Nullable String string1, @NotNull String string2) {
        if (string1 == null && string2.equals("")) {
            return true;
        }
        if (string1 == null) {
            return false;
        }
        return string1.equals(string2);
    }

    @VisibleForTesting
    @NotNull
    Boolean containsWord(@Nullable String string1, @NotNull String string2) {
        String pattern = "\\b" + string1 + "\\b";
        return string2.matches(".*" + pattern + ".*");
    }
}

package com.hartwig.serve.sources.ckb.curation;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableGene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableVariant;
import com.hartwig.serve.ckb.datamodel.variant.Variant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbCurator {

    private static final Logger LOGGER = LogManager.getLogger(CkbCurator.class);

    @NotNull
    private final List<CkbFacilityCurationNameEntry> facilityCurationNameEntries;
    @NotNull
    private final List<CkbFacilityCurationZipEntry> facilityCurationZipEntries;
    @NotNull
    private final List<CkbFacilityCurationManualEntry> facilityCurationManualEntries;
    @NotNull
    private final Set<CkbFacilityCurationManualEntry> usedFacilityCurationManualEntries = Sets.newHashSet();
    @NotNull
    private final Set<CkbVariantCurationEntry> evaluatedVariantCurationEntries = Sets.newHashSet();

    public CkbCurator(@NotNull final List<CkbFacilityCurationNameEntry> facilityCurationNameList,
            @NotNull final List<CkbFacilityCurationZipEntry> facilityCurationZipList,
            @NotNull final List<CkbFacilityCurationManualEntry> facilityCurationManualList) {
        this.facilityCurationNameEntries = facilityCurationNameList;
        this.facilityCurationZipEntries = facilityCurationZipList;
        this.facilityCurationManualEntries = facilityCurationManualList;
    }

    @NotNull
    public List<CkbEntry> run(@NotNull List<CkbEntry> ckbEntries) {
        List<CkbEntry> curatedCkbEntries = Lists.newArrayList();

        for (CkbEntry ckbEntry : ckbEntries) {
            List<Variant> curatedVariants = Lists.newArrayList();
            List<ClinicalTrial> curatedClinicalTrials = Lists.newArrayList();
            for (Variant variant : ckbEntry.variants()) {
                curatedVariants.add(curateVariant(variant));
            }
            for (ClinicalTrial clinicalTrial : ckbEntry.clinicalTrials()) {
                List<Location> locations = Lists.newArrayList();
                for (Location location : clinicalTrial.locations()) {
                    locations.add(curateFacilityName(location));
                }
                curatedClinicalTrials.add(ImmutableClinicalTrial.builder().from(clinicalTrial).locations(locations).build());
            }
            curatedCkbEntries.add(ImmutableCkbEntry.builder()
                    .from(ckbEntry)
                    .variants(curatedVariants)
                    .clinicalTrials(curatedClinicalTrials)
                    .build());
        }

        return curatedCkbEntries;
    }

    @NotNull
    private Variant curateVariant(@NotNull Variant variant) {
        String geneSymbol = variant.gene() != null ? variant.gene().geneSymbol() : null;
        if (geneSymbol == null) {
            LOGGER.debug("No gene symbol known, skipping curation on '{}'", variant);
            return variant;
        }

        CkbVariantCurationEntry entry = new CkbVariantCurationEntry(geneSymbol, variant.variant());
        evaluatedVariantCurationEntries.add(entry);

        Variant curatedVariant = variant;
        if (CkbVariantCurationFactory.VARIANT_MAPPINGS.containsKey(entry)) {
            String mappedVariant = CkbVariantCurationFactory.VARIANT_MAPPINGS.get(entry).variant();
            String mappedGeneSymbol = CkbVariantCurationFactory.VARIANT_MAPPINGS.get(entry).geneSymbol();

            LOGGER.debug("Mapping variant '{}' on '{}' to '{}' on '{}'",
                    entry.variant(),
                    entry.geneSymbol(),
                    mappedVariant,
                    mappedGeneSymbol);

            curatedVariant = ImmutableVariant.builder()
                    .from(curatedVariant)
                    .gene(ImmutableGene.builder().from(variant.gene()).geneSymbol(mappedGeneSymbol).build())
                    .variant(mappedVariant)
                    .build();
        }

        return curatedVariant;
    }

    @NotNull
    public Location curateFacilityName(@NotNull Location location) {
        for (CkbFacilityCurationZipEntry facilityCurationZipEntry : facilityCurationZipEntries) {
            if (containsWord(facilityCurationZipEntry.city(), location.city().toLowerCase())) {
                String zip = location.zip() != null ? location.zip().toLowerCase().replaceAll("\\s", "") : "";
                if ((facilityCurationZipEntry.zip().equals("")) || (zip.contains(facilityCurationZipEntry.zip()))) {
                    return ImmutableLocation.builder().from(location).facility(facilityCurationZipEntry.curatedFacilityName()).build();
                }
            }
        }

        if (location.facility() != null) {
            for (CkbFacilityCurationNameEntry facilityCurationNameEntry : facilityCurationNameEntries) {
                if (containsWord(facilityCurationNameEntry.facilityName(), location.facility().toLowerCase()) && containsWord(
                        facilityCurationNameEntry.city(),
                        location.city().toLowerCase())) {
                    return ImmutableLocation.builder().from(location).facility(facilityCurationNameEntry.curatedFacilityName()).build();
                }
            }
        }

        for (CkbFacilityCurationManualEntry facilityCurationManualEntry : facilityCurationManualEntries) {
            if (equalStringsOrNull(location.facility(), facilityCurationManualEntry.facilityName()) && location.city()
                    .equals(facilityCurationManualEntry.city()) && equalStringsOrNull(location.zip(), facilityCurationManualEntry.zip())) {
                usedFacilityCurationManualEntries.add(facilityCurationManualEntry);
                return ImmutableLocation.builder().from(location).facility(facilityCurationManualEntry.curatedFacilityName()).build();
            }
        }

        if (location.country().equals("Netherlands")) {
            LOGGER.warn(" Couldn't curate facility name for location '{}'", location);
        }
        return location.facility() == null ? ImmutableLocation.builder()
                .from(location)
                .facility("Unknown [" + location.city() + "]")
                .build() : ImmutableLocation.builder().from(location).facility(location.facility().replaceAll("[(),]", "")).build();
    }

    public void reportUnusedCurationEntries() {
        int unusedEntryCount = 0;
        for (CkbVariantCurationEntry entry : CkbVariantCurationFactory.VARIANT_MAPPINGS.keySet()) {
            if (!evaluatedVariantCurationEntries.contains(entry)) {
                unusedEntryCount++;
                LOGGER.warn(" Entry '{}' hasn't been used during CKB curation", entry);
            }
        }

        LOGGER.debug(" Found {} unused CKB curation entries. {} keys have been requested against {} curation entries",
                unusedEntryCount,
                evaluatedVariantCurationEntries.size(),
                CkbVariantCurationFactory.VARIANT_MAPPINGS.size());
    }

    public void reportUnusedFacilityCurationManualEntries() {
        int unusedFacilityCurationManualCount = 0;
        for (CkbFacilityCurationManualEntry entry : facilityCurationManualEntries) {
            if (!usedFacilityCurationManualEntries.contains(entry)) {
                unusedFacilityCurationManualCount++;
                LOGGER.warn(" Facility curation manual entry '{}' hasn't been used for CKB filtering", entry);
            }
        }
        LOGGER.debug(" Found {} unused manual facility curation entries during CKB filtering", unusedFacilityCurationManualCount);
    }

    @VisibleForTesting
    boolean equalStringsOrNull(@Nullable String string1, @NotNull String string2) {
        if (string1 == null && string2.equals("")) {
            return true;
        }
        if (string1 == null) {
            return false;
        }
        return string1.equals(string2);
    }

    @VisibleForTesting
    boolean containsWord(@Nullable String string1, @NotNull String string2) {
        String pattern = "\\b" + string1 + "\\b";
        return string2.matches(".*" + pattern + ".*");
    }
}

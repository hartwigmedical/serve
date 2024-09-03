package com.hartwig.serve.ckb;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrialFactory;
import com.hartwig.serve.ckb.datamodel.evidence.EvidenceFactory;
import com.hartwig.serve.ckb.datamodel.variant.VariantFactory;
import com.hartwig.serve.ckb.json.CkbJsonDatabase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

final class JsonDatabaseToCkbEntryConverter {

    private static final Logger LOGGER = LogManager.getLogger(JsonDatabaseToCkbEntryConverter.class);

    private JsonDatabaseToCkbEntryConverter() {
    }

    @NotNull
    public static List<CkbEntry> convert(@NotNull CkbJsonDatabase ckbJsonDatabase) {
        int profileCount = ckbJsonDatabase.molecularProfiles().size();
        LOGGER.debug(" Converting {} CKB molecular profiles to interpreted CKB entries", profileCount);

        VariantFactory variantFactory = new VariantFactory(ckbJsonDatabase);
        AtomicInteger current = new AtomicInteger(0);
        int report = Math.max((int) Math.round(profileCount / 10D), 1);

        return ckbJsonDatabase.molecularProfiles().parallelStream().map(molecularProfile -> {
            CkbEntry entry = ImmutableCkbEntry.builder()
                    .profileId(molecularProfile.id())
                    .createDate(molecularProfile.createDate())
                    .updateDate(molecularProfile.updateDate())
                    .profileName(molecularProfile.profileName())
                    .variants(variantFactory.extractVariants(molecularProfile.geneVariants()))
                    .evidences(EvidenceFactory.extractEvidences(ckbJsonDatabase, molecularProfile.variantLevelEvidence().evidences()))
                    .clinicalTrials(ClinicalTrialFactory.extractClinicalTrials(ckbJsonDatabase,
                            molecularProfile.variantAssociatedClinicalTrials()))
                    .build();

            int processed = current.addAndGet(1);
            if (processed > 1 && processed % report == 0) {
                LOGGER.debug("  Processed {} of {} molecular profiles", processed, profileCount);
            }
            return entry;
        }).collect(Collectors.toList());
    }
}
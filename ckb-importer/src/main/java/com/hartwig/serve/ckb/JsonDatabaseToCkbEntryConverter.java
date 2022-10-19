package com.hartwig.serve.ckb;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrialFactory;
import com.hartwig.serve.ckb.datamodel.evidence.EvidenceFactory;
import com.hartwig.serve.ckb.datamodel.variant.VariantFactory;
import com.hartwig.serve.ckb.json.CkbJsonDatabase;
import com.hartwig.serve.ckb.json.molecularprofile.JsonMolecularProfile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

final class JsonDatabaseToCkbEntryConverter {

    private static final Logger LOGGER = LogManager.getLogger(JsonDatabaseToCkbEntryConverter.class);

    private JsonDatabaseToCkbEntryConverter() {
    }

    @NotNull
    public static List<CkbEntry> convert(@NotNull CkbJsonDatabase ckbJsonDatabase) {
        List<CkbEntry> ckbEntries = Lists.newArrayList();

        int profileCount = ckbJsonDatabase.molecularProfiles().size();
        LOGGER.debug(" Converting {} CKB molecular profiles to interpreted CKB entries", profileCount);

        int current = 0;
        int report = (int) Math.round(profileCount / 10D);
        for (JsonMolecularProfile molecularProfile : ckbJsonDatabase.molecularProfiles()) {
            ckbEntries.add(ImmutableCkbEntry.builder()
                    .profileId(molecularProfile.id())
                    .createDate(molecularProfile.createDate())
                    .updateDate(molecularProfile.updateDate())
                    .profileName(molecularProfile.profileName())
                    .variants(VariantFactory.extractVariants(ckbJsonDatabase, molecularProfile.geneVariants()))
                    .evidences(EvidenceFactory.extractEvidences(ckbJsonDatabase, molecularProfile.variantLevelEvidence().evidences()))
                    .clinicalTrials(ClinicalTrialFactory.extractClinicalTrials(ckbJsonDatabase,
                            molecularProfile.variantAssociatedClinicalTrials()))
                    .build());
            current++;

            if (current > 1) {
                if (current % report == 0) {
                    LOGGER.debug("  Processed {} of {} molecular profiles", current, profileCount);
                }
            }
        }

        return ckbEntries;
    }
}
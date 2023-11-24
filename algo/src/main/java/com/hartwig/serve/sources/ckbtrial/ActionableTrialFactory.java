package com.hartwig.serve.sources.ckbtrial;

import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ActionableTrialFactory {

    private static final Logger LOGGER = LogManager.getLogger(ActionableTrialFactory.class);

    ActionableTrialFactory() {
    }

    @NotNull
    public static Set<ActionableTrial> toActionableTrials(@NotNull CkbEntry entry, @NotNull String sourceEvent) {
        Set<ActionableTrial> actionableTrials = Sets.newHashSet();

        for (ClinicalTrial trial : entry.clinicalTrials()) {
            for (Indication indication : trial.indications()) {
                String[] sourceCancerTypes = extractSourceCancerTypeId(indication.termId());
                String doid = extractAndCurateDoid(sourceCancerTypes);

                if (doid != null) {
                    String cancerType = indication.name();

                    Set<CancerType> blacklistedCancerTypes = Sets.newHashSet();
                    if (doid.equals(CancerTypeConstants.CANCER_DOID)) {
                        blacklistedCancerTypes.add(CancerTypeConstants.LEUKEMIA_TYPE);
                        blacklistedCancerTypes.add(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE);
                        blacklistedCancerTypes.add(CancerTypeConstants.BONE_MARROW_TYPE);
                    }

                    actionableTrials.add(ImmutableActionableTrial.builder()
                            .source(Knowledgebase.CKB_TRIAL)
                            .sourceEvent(sourceEvent)
                            .sourceUrls(Sets.newHashSet("https://clinicaltrials.gov/study/" + trial.nctId()))
                            .treatment(ImmutableTreatment.builder().name(trial.nctId()).build())
                            .applicableCancerType(ImmutableCancerType.builder().name(cancerType).doid(doid).build())
                            .blacklistCancerTypes(blacklistedCancerTypes)
                            .level(EvidenceLevel.B)
                            .direction(EvidenceDirection.RESPONSIVE)
                            .evidenceUrls(Sets.newHashSet())
                            .build());
                }
            }
        }
        return actionableTrials;
    }

    @Nullable
    @VisibleForTesting
    static String[] extractSourceCancerTypeId(@Nullable String doidString) {
        if (doidString == null) {
            return null;
        }

        String[] parts = doidString.split(":");
        if (parts.length == 2) {
            String source = parts[0];
            if (source.equalsIgnoreCase("doid") || source.equalsIgnoreCase("jax")) {
                return parts;
            } else {
                LOGGER.warn("Unexpected length of doid string '{}'", doidString);
                return null;
            }
        } else {
            LOGGER.warn("Unexpected DOID string in CKB: '{}'", doidString);
            return null;
        }
    }

    @Nullable
    @VisibleForTesting
    static String extractAndCurateDoid(@Nullable String[] doidString) {
        if (doidString == null) {
            return null;
        }

        assert doidString.length == 2;
        String source = doidString[0];
        String id = doidString[1];
        if (source.equalsIgnoreCase("doid")) {
            return id;
        } else if (source.equalsIgnoreCase("jax")) {
            switch (id) {
                case CancerTypeConstants.JAX_ADVANCES_SOLID_TUMORS:
                case CancerTypeConstants.JAX_CANCER_OF_UNKNOWN_PRIMARY:
                    return CancerTypeConstants.CANCER_DOID;
                case CancerTypeConstants.JAX_CARCINOMA_OF_UNKNOWN_PRIMARY:
                    return CancerTypeConstants.CARCINOMA_OF_UNKNOWN_PRIMARY;
                case CancerTypeConstants.JAX_ADENOCARCINOMA_OF_UNKNOWN_PRIMARY:
                    return CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY;
                case CancerTypeConstants.JAX_SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY:
                    return CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY;
                default:
                    // CKB uses 10000005 for configuring "Not a cancer". We can ignore these.
                    if (!id.equals(CancerTypeConstants.JAX_NOT_CANCER)) {
                        LOGGER.warn("Unexpected DOID string annotated by CKB: '{}'", source + ":" + id);
                    }
                    return null;
            }
        } else {
            LOGGER.warn("Unexpected source '{}'", source);
            return null;
        }
    }
}
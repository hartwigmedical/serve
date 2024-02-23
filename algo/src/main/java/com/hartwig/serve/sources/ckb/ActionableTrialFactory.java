package com.hartwig.serve.sources.ckb;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

class ActionableTrialFactory implements ActionableEntryFactory {

    private static final Set<String> POTENTIALLY_OPEN_RECRUITMENT_TYPES = Sets.newHashSet();
    private static final Set<String> COUNTRIES_TO_INCLUDE = Sets.newHashSet();
    private static final Set<String> VARIANT_REQUIREMENT_TYPES_TO_INCLUDE = Sets.newHashSet();

    static {
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("active, not recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("approved for marketing");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("available");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("not yet recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("unknown status");

        COUNTRIES_TO_INCLUDE.add("netherlands");

        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("partial - required");
        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("required");
    }

    @NotNull
    private final CkbBlacklistStudy blacklistStudy;

    public ActionableTrialFactory(@NotNull CkbBlacklistStudy blacklistStudy) {
        this.blacklistStudy = blacklistStudy;
    }

    @NotNull
    @Override
    public Set<ActionableEntry> create(@NotNull CkbEntry entry, @NotNull String sourceEvent, @NotNull String sourceGene) {
        Set<ActionableEntry> actionableTrials = Sets.newHashSet();

        for (ClinicalTrial trial : trialsToInclude(entry)) {
            Set<String> countries = countriesToInclude(trial);

            if (!countries.isEmpty()) {
                for (Therapy therapy : trial.therapies()) {
                    String therapyName = therapy.therapyName();
                    for (Indication indication : trial.indications()) {
                        CancerTypeExtraction cancerTypeExtraction = ActionableFunctions.extractCancerTypeDetails(indication);
                        if (cancerTypeExtraction != null) {
                            if (!blacklistStudy.isBlacklistStudy(trial.title(), therapyName, cancerTypeExtraction.applicableCancerType().name(),
                                    sourceGene, sourceEvent)) {
                                actionableTrials.add(ImmutableActionableEntry.builder()
                                        .source(Knowledgebase.CKB_TRIAL)
                                        .sourceEvent(sourceEvent)
                                        .sourceUrls(Sets.newHashSet("https://ckbhome.jax.org/clinicalTrial/show?nctId=" + trial.nctId()))
                                        .treatment(ImmutableTreatment.builder().name(trial.title()).build())
                                        .applicableCancerType(cancerTypeExtraction.applicableCancerType())
                                        .blacklistCancerTypes(cancerTypeExtraction.blacklistedCancerTypes())
                                        .level(EvidenceLevel.B)
                                        .direction(EvidenceDirection.RESPONSIVE)
                                        .evidenceUrls(Sets.newHashSet("https://clinicaltrials.gov/study/" + trial.nctId()))
                                        .build());
                            }
                        }
                    }
                }
            }
        }
        return actionableTrials;
    }

    @NotNull
    private static List<ClinicalTrial> trialsToInclude(@NotNull CkbEntry entry) {
        List<ClinicalTrial> filtered = Lists.newArrayList();
        for (ClinicalTrial trial : entry.clinicalTrials()) {
            if (hasVariantRequirementTypeToInclude(trial.variantRequirementDetails(), entry) && POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(
                    trial.recruitment().toLowerCase())) {
                filtered.add(trial);
            }
        }
        return filtered;
    }

    @NotNull
    @VisibleForTesting
    static Set<String> countriesToInclude(@NotNull ClinicalTrial trial) {
        Set<String> countries = Sets.newHashSet();
        for (Location location : trial.locations()) {
            if (COUNTRIES_TO_INCLUDE.contains(location.country().toLowerCase()) && (location.status() == null
                    || POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(location.status().toLowerCase()))) {
                countries.add(location.country());
            }
        }

        return countries;
    }

    @VisibleForTesting
    static boolean hasVariantRequirementTypeToInclude(@NotNull List<VariantRequirementDetail> variantRequirementDetails,
                                                      @NotNull CkbEntry entry) {
        for (VariantRequirementDetail variantRequirementDetail : variantRequirementDetails) {
            // Check if trial should be included based on the molecular profile of the current entry (trial can be linked to multiple molecular profiles)
            if (entry.profileId() == variantRequirementDetail.profileId() && VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.contains(
                    variantRequirementDetail.requirementType().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
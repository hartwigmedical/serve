package com.hartwig.serve.sources.ckb;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;

class ActionableTrialFactory implements ActionableEntryFactory {

    private static final Set<String> POTENTIALLY_OPEN_RECRUITMENT_TYPES = Sets.newHashSet();
    private static final Set<String> COUNTRIES_TO_INCLUDE = Sets.newHashSet();
    private static final Set<String> VARIANT_REQUIREMENT_TYPES_TO_INCLUDE = Sets.newHashSet();

    static {
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("Recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("Active, not recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("Unknown status");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("RECRUITING");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("ACTIVE_NOT_RECRUITING");

        COUNTRIES_TO_INCLUDE.add("Netherlands");
        COUNTRIES_TO_INCLUDE.add("Belgium");
        COUNTRIES_TO_INCLUDE.add("Germany");

        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("partial - required");
        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("required");
    }

    public ActionableTrialFactory() {
    }

    @NotNull
    @Override
    public Set<ActionableEntry> create(@NotNull CkbEntry entry, @NotNull String sourceEvent, @NotNull String gene) {
        Set<ActionableEntry> actionableTrials = Sets.newHashSet();

        for (ClinicalTrial trial : trialsToInclude(entry)) {
            for (Indication indication : trial.indications()) {
                CancerTypeExtraction cancerTypeExtraction = ActionableFunctions.extractCancerTypeDetails(indication);

                if (cancerTypeExtraction != null) {
                    Set<String> countries = countriesToInclude(trial);

                    actionableTrials.add(ImmutableActionableEntry.builder()
                            .source(Knowledgebase.CKB_TRIAL)
                            .sourceEvent(sourceEvent)
                            .sourceUrls(Sets.newHashSet("https://clinicaltrials.gov/study/" + trial.nctId()))
                            .treatment(ImmutableTreatment.builder().name(trial.nctId()).build())
                            .applicableCancerType(cancerTypeExtraction.applicableCancerType())
                            .blacklistCancerTypes(cancerTypeExtraction.blacklistedCancerTypes())
                            .level(EvidenceLevel.B)
                            .direction(EvidenceDirection.RESPONSIVE)
                            .evidenceUrls(countries)
                            .build());
                }
            }
        }
        return actionableTrials;
    }

    @NotNull
    private static List<ClinicalTrial> trialsToInclude(@NotNull CkbEntry entry) {
        List<ClinicalTrial> filtered = Lists.newArrayList();
        for (ClinicalTrial trial : entry.clinicalTrials()) {
            if (!countriesToInclude(trial).isEmpty()
                    && hasVariantRequirementTypeToInclude(trial.variantRequirementDetails(), entry)) {
                filtered.add(trial);
            }
        }
        return filtered;
    }

    @VisibleForTesting
    private static Set<String> countriesToInclude(ClinicalTrial trial) {
        Set<String> countries = Sets.newHashSet();
        for (Location location : trial.locations()) {
            if (COUNTRIES_TO_INCLUDE.contains(location.country()) && POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(trial.recruitment()) && (
                    POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(location.status()) || location.status() == null)) {
                countries.add(location.country());
            }
        }
        return countries;
    }

    @VisibleForTesting
    static boolean hasVariantRequirementTypeToInclude(@NotNull List<VariantRequirementDetail> variantRequirementDetails, CkbEntry entry) {
        for (VariantRequirementDetail variantRequirementDetail : variantRequirementDetails) {
            if (entry.profileId() == variantRequirementDetail.profileId() && VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.contains(
                    variantRequirementDetail.requirementType())) {
                return true;
            }
        }
        return false;
    }
}
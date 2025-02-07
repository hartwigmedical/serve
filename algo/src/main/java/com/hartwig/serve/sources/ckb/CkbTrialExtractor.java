package com.hartwig.serve.sources.ckb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.trial.ActionableTrial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbTrialExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbTrialExtractor.class);

    @NotNull
    private final ActionableTrialFactory actionableTrialFactory;
    @NotNull
    private final CkbMolecularCriteriaExtractor molecularCriteriaExtractor;

    CkbTrialExtractor(@NotNull ActionableTrialFactory actionableTrialFactory,
            @NotNull CkbMolecularCriteriaExtractor molecularCriteriaExtractor) {
        this.actionableTrialFactory = actionableTrialFactory;
        this.molecularCriteriaExtractor = molecularCriteriaExtractor;
    }

    public List<ActionableTrial> processTrials(@NotNull List<CkbEntry> ckbEntries) {
        Map<Integer, CkbEntry> idToEntry = ckbEntries.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(CkbEntry::profileId, entry -> entry),
                        Collections::unmodifiableMap));

        Set<ClinicalTrial> uniqueTrials = ckbEntries.stream()
                .flatMap(entry -> entry.clinicalTrials().stream())
                .collect(Collectors.toSet());

        // TODO does parallelism here make sense?
        List<ActionableTrial> allActionableTrials = uniqueTrials.parallelStream()
                .map(trial -> processTrial(trial, idToEntry))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        logSummaryStats(allActionableTrials);
        return allActionableTrials;
    }

    @Nullable
    private ActionableTrial processTrial(@NotNull ClinicalTrial trial, @NotNull Map<Integer, CkbEntry> idToEntry) {
        LOGGER.debug("trial: {}", trial.nctId());

        ArrayList<MolecularCriterium> requiredCriterium = new ArrayList<>();
        ArrayList<MolecularCriterium> partiallyRequiredCriterium = new ArrayList<>();

        for (VariantRequirementDetail details : trial.variantRequirementDetails()) {
            Integer profileId = details.profileId();
            if (!idToEntry.containsKey(profileId)) {
                throw new IllegalStateException("No entry found for profile ID: " + profileId);
            }

            CkbEntry entry = idToEntry.get(profileId);
            MolecularCriterium criteria = molecularCriteriaExtractor.criteriumForEntry(entry);

            if (details.requirementType().equals("required")) {
                requiredCriterium.add(criteria);
            } else if (details.requirementType().equals("partial - required")) {
                partiallyRequiredCriterium.add(criteria);
            }
        }

        if (requiredCriterium.isEmpty() && partiallyRequiredCriterium.isEmpty()) {
            LOGGER.warn("No required criteria found for trial: {}", trial.nctId());
            return null;
        }

        MolecularCriterium combinedRequiredCriterium = MolecularCriteriaCombiner.combine(requiredCriterium);
        Set<MolecularCriterium> anyMolecularCriteria = combinePartialWithRequired(combinedRequiredCriterium, partiallyRequiredCriterium);
        return actionableTrialFactory.createV2(anyMolecularCriteria, trial);
    }

    @VisibleForTesting
    @NotNull
    static Set<MolecularCriterium> combinePartialWithRequired(MolecularCriterium requiredCriterium,
            List<MolecularCriterium> partiallyRequiredCriterium) {

        if (partiallyRequiredCriterium.isEmpty()) {
            return Set.of(requiredCriterium);
        } else {
            return partiallyRequiredCriterium.stream().map(partialMolecularCriterium ->
                            MolecularCriteriaCombiner.combine(requiredCriterium, partialMolecularCriterium))
                    .collect(Collectors.toSet());
        }
    }

    private void logSummaryStats(@NotNull List<ActionableTrial> trials) {
        List<ActionableTrial> singleCriteriaTrials =
                trials.stream().filter(trial -> trial.anyMolecularCriteria().size() == 1)
                        .collect(Collectors.toList());
        List<ActionableTrial> singleSimpleCriteriaTrials =
                singleCriteriaTrials.stream().filter(trial -> criteriaCount(trial.anyMolecularCriteria().iterator().next()) == 1)
                        .collect(Collectors.toList());

        int totalTrials = trials.size();
        int totalSingleCriteriaTrials = singleCriteriaTrials.size();
        int totalSingleSimpleCriteriaTrials = singleSimpleCriteriaTrials.size();

        LOGGER.info("Total: {} trials", totalTrials);
        LOGGER.info("Trials with single simple criteria: {}", totalSingleSimpleCriteriaTrials);
        LOGGER.info("Trials with single complex criteria: {}", totalSingleCriteriaTrials - totalSingleSimpleCriteriaTrials);
        LOGGER.info("trials with multiple criteria: {}", totalTrials - totalSingleCriteriaTrials);
    }

    private int criteriaCount(@NotNull MolecularCriterium molecularCriterium) {
        return molecularCriterium.allOfAnyHotspots().size() +
                molecularCriterium.codons().size() +
                molecularCriterium.exons().size() +
                molecularCriterium.genes().size() +
                molecularCriterium.fusions().size() +
                molecularCriterium.characteristics().size() +
                molecularCriterium.hla().size();
    }
}

package com.hartwig.serve.sources.ckb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public CkbTrialExtractor(@NotNull ActionableTrialFactory actionableTrialFactory,
            @NotNull CkbMolecularCriteriaExtractor molecularCriteriaExtractor) {
        this.actionableTrialFactory = actionableTrialFactory;
        this.molecularCriteriaExtractor = molecularCriteriaExtractor;
    }

    // Extract all trials from the given list of CkbEntries
    // A trial can have multiple MolecularProfiles == CkbEntries (with differing variant match criteria, required,
    // partial-required, excluded)
    //
    // * Create a map from profile Id to its corresponding CkbEntry
    // * Create a set of all the trials (so unique-ified) by extracting from all the CkbEntry's
    // * Now loop over each trial:
    //   * the trial will have a list of molecular profiles, for each of these:
    //     - look up in the profile to entry map, yield the CkbEntry.
    //     - for each CkbEntry:
    //       * Extract all the variants for that entry
    //       * for each variant, create a MolecularCriterium - this will only have one hotspot-group or gene or exon represented
    //       * join all the molecular criterium's into a single one (conjunction, AND of all the variants), this will now have
    //         N items in the criterium where N is the number of Variants for the CkbEntry
    //     * Now finish up the molecular criteria for for the trial by creating a Set<MolecularCriteria>, built from the individual
    //       criterium for each entry, depending of if required or partial.

    public List<ActionableTrial> processTrials(@NotNull List<CkbEntry> ckbEntries) {

        Map<Integer, CkbEntry> idToEntry = ckbEntries.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(CkbEntry::profileId, entry -> entry),
                        Collections::unmodifiableMap));

        Set<ClinicalTrial> uniqueTrials = ckbEntries.stream()
                .flatMap(entry -> entry.clinicalTrials().stream())
                .collect(Collectors.toSet());

        // TODO does parallelism here make sense? presumably there are underlying calls to transvar
        // to extract hotspots that need to be parallelized until we replace. though could we
        // run them from a higher level?
        List<ActionableTrial> allActionableTrials = uniqueTrials.parallelStream()
                .map(trial -> processTrial(trial, idToEntry))
                .collect(Collectors.toList());

        // number of non-null elements in alternative
        long nonNullTrials = allActionableTrials.stream().filter(Objects::nonNull).count();
        LOGGER.info("Created: {} trials", allActionableTrials.size());
        LOGGER.info("  number non null: {}", nonNullTrials);  // TODO shouldn't get nulls, fix
        @NotNull List<ActionableTrial> result = allActionableTrials.stream().filter(Objects::nonNull).collect(Collectors.toList());
        reportSomeSummaryStats(result);
        return result;
    }

    @Nullable
    public ActionableTrial processTrial(ClinicalTrial trial, Map<Integer, CkbEntry> idToEntry) {
        LOGGER.info("trial: {}", trial.nctId());
        if (!trial.variantRequirement().equalsIgnoreCase("yes")) {
            LOGGER.warn("Trial {} does not have variant requirement yes", trial.nctId());
        }

        ArrayList<MolecularCriterium> requiredCriterium = new ArrayList<MolecularCriterium>();
        ArrayList<MolecularCriterium> partialRequiredCriterium = new ArrayList<MolecularCriterium>();

        for (VariantRequirementDetail details : trial.variantRequirementDetails()) {
            LOGGER.info("details: {}", details);

            Integer profileId = details.profileId();
            if (!idToEntry.containsKey(profileId)) {
                LOGGER.warn("No entry found for profile ID: {}", profileId);
            } else {
                CkbEntry entry = idToEntry.get(profileId);

                // this is a single criterium because even if multiple variants,
                // they are ANDed together.
                MolecularCriterium criteria = molecularCriteriaExtractor.criteriumForEntry(entry);

                // now what? all the required criteria could be joined into a single one,
                // except they have different profileIds/dates etc.
                // and for each partial required we'd need to create a criteria joining the
                // required and partial required ones.
                //
                // if there is one partial required, is the output the full required by
                // itself, and then the full required with the partial required?

                if (details.requirementType().equals("required")) { /// TODO put required in a constant. also partial below
                    requiredCriterium.add(criteria);
                } else if (details.requirementType().equals("partial - required")) {
                    partialRequiredCriterium.add(criteria);
                } else {
                    // TODO won't need this log message long term, just for testing
                    LOGGER.info("Skipping molecular criteria with requirement type: {}", details.requirementType());
                }
            }
        }

        // now we have all the required and partial required criteria for the trial, we'll try
        // creating a set of molecular critera as follows:
        // Joint all the required's into one (there may actually be none of these)
        // then join this with all the partials individually
        //
        // e.g. if N required's and M partials, then we end up with M criteria
        // if 0 required, M partials then still M criteria
        // if N required and 0 partials, then 1 criteria

        if (requiredCriterium.isEmpty() && partialRequiredCriterium.isEmpty()) {
            // are these filtered out earlier?
            LOGGER.warn("No required criteria found for trial: {}", trial.nctId());
            return null;
        } else {
            MolecularCriterium combinedRequiredCriteria = MolecularCriteriaCombiner.combineCriteriaList(requiredCriterium);

            Set<MolecularCriterium> all;
            if (partialRequiredCriterium.isEmpty()) {
                all = Set.of(combinedRequiredCriteria);
            } else {
                // create actionable trial for each partial required criteria joined with the required criteria
                all = partialRequiredCriterium.stream()
                        .map(partial -> MolecularCriteriaCombiner.combineCriteria(combinedRequiredCriteria, partial))
                        .collect(Collectors.toSet());
            }

            ActionableTrial actionableTrials = actionableTrialFactory.createV2(all, trial);
            return actionableTrials;
        }
    }

    private void reportSomeSummaryStats(@NotNull List<ActionableTrial> trials) {

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

    private int criteriaCount(MolecularCriterium molecularCriterium) {
        return molecularCriterium.hotspots().size() +
                molecularCriterium.codons().size() +
                molecularCriterium.exons().size() +
                molecularCriterium.genes().size() +
                molecularCriterium.fusions().size() +
                molecularCriterium.characteristics().size() +
                molecularCriterium.hla().size();
    }
}

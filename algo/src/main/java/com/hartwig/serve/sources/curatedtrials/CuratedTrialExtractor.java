package com.hartwig.serve.sources.curatedtrials;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.trial.Phase;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CuratedTrialExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CuratedTrialExtractor.class);

    @NotNull
    public ExtractionResult extract(@NotNull List<CuratedTrialEntry> entries) {
        Set<ActionableTrial> actionableTrials = new HashSet<>();
        ProgressTracker tracker = new ProgressTracker("CuratedTrials", entries.size());

        for (CuratedTrialEntry entry : entries) {

            if (entry.anyMolecularCriteria().isEmpty()) {
                LOGGER.warn("Curated trial '{}' has no molecular criteria, skipping", entry.trialId());
                tracker.update();
                continue;
            }

            for (MolecularCriterium criterium : entry.anyMolecularCriteria()) {
                actionableTrials.add(ImmutableActionableTrial.builder()
                        .source(Knowledgebase.CURATED_TRIALS)
                        .nctId(null)
                        .title(entry.title())
                        .acronym(entry.acronym())
                        .phase(Phase.UNKNOWN)
                        .countries(entry.countries())
                        .therapyNames(entry.therapyNames())
                        .genderCriterium(entry.genderCriterium())
                        .indications(entry.indications())
                        .anyMolecularCriteria(Set.of(criterium))
                        .urls(entry.urls())
                        .build());
            }

            tracker.update();
        }

        LOGGER.info("Extracted {} actionable trials from {} curated trial entries",
                actionableTrials.size(), entries.size());

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CURATED_TRIALS.refGenomeVersion())
                .eventInterpretations(new HashSet<>())
                .trials(new ArrayList<>(actionableTrials))
                .build();
    }
}
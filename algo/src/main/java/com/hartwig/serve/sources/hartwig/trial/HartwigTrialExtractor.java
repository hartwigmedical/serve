package com.hartwig.serve.sources.hartwig.trial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ImmutableCancerType;
import com.hartwig.serve.datamodel.common.ImmutableIndication;
import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.trial.ImmutableCountry;
import com.hartwig.serve.datamodel.trial.Phase;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class HartwigTrialExtractor {

    private static final Logger LOGGER = LogManager.getLogger(HartwigTrialExtractor.class);

    @NotNull
    private final EventExtractor eventExtractor;

    HartwigTrialExtractor(@NotNull EventExtractor eventExtractor) {
        this.eventExtractor = eventExtractor;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<HartwigTrialEntry> entries) {
        Set<ActionableTrial> actionableTrials = new HashSet<>();
        ProgressTracker tracker = new ProgressTracker("CuratedTrials", entries.size());

        for (HartwigTrialEntry entry : entries) {
            Indication indication = ImmutableIndication.builder()
                    .applicableType(ImmutableCancerType.builder().name(entry.cancerType()).doid(entry.cancerTypeDoid()).build())
                    .build();
            
            actionableTrials.add(ImmutableActionableTrial.builder()
                    .source(Knowledgebase.HARTWIG_TRIAL_CURATED)
                    .nctId(entry.nctId())
                    .title(entry.title())
                    .acronym(entry.acronym())
                    .phase(Phase.UNKNOWN)
                    .countries(Sets.newHashSet(ImmutableCountry.builder().name(entry.country()).build()))
                    .genderCriterium(entry.genderCriterium())
                    .indications(Sets.newHashSet(indication))
                    //                    .anyMolecularCriteria(new HashSet<>(entry.anyMolecularCriteria()))
                    .urls(Sets.newHashSet(entry.url()))
                    .build());

            tracker.update();
        }

        LOGGER.info("Extracted {} actionable trials from {} curated trial entries", actionableTrials.size(), entries.size());

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.HARTWIG_TRIAL_CURATED.refGenomeVersion())
                .eventInterpretations(new HashSet<>())
                .trials(new ArrayList<>(actionableTrials))
                .build();
    }
}
package com.hartwig.serve.sources.hartwig.trial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ImmutableCancerType;
import com.hartwig.serve.datamodel.common.ImmutableIndication;
import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.Country;
import com.hartwig.serve.datamodel.trial.GenderCriterium;
import com.hartwig.serve.datamodel.trial.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.trial.ImmutableCountry;
import com.hartwig.serve.datamodel.trial.Phase;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HartwigTrialExtractor {

    private static final Logger LOGGER = LogManager.getLogger(HartwigTrialExtractor.class);

    @NotNull
    private final MolecularCriteriumExtractor molecularCriteriumExtractor;

    HartwigTrialExtractor(@NotNull MolecularCriteriumExtractor molecularCriteriumExtractor) {
        this.molecularCriteriumExtractor = molecularCriteriumExtractor;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<HartwigTrialEntry> entries) {
        Set<ActionableTrial> actionableTrials = new HashSet<>();
        ProgressTracker tracker = new ProgressTracker("Hartwig Trials", entries.size());

        Map<String, List<HartwigTrialEntry>> trialsPerNctId = entries.stream().collect(Collectors.groupingBy(HartwigTrialEntry::nctId));

        for (Map.Entry<String, List<HartwigTrialEntry>> entry : trialsPerNctId.entrySet()) {
            List<HartwigTrialEntry> entriesForNctId = entry.getValue();

            Set<MolecularCriterium> anyMolecularCriteria = extractMolecularCriteria(entriesForNctId);
            if (!anyMolecularCriteria.isEmpty()) {
                actionableTrials.add(ImmutableActionableTrial.builder()
                        .source(Knowledgebase.HARTWIG_TRIAL_CURATED)
                        .nctId(entry.getKey())
                        .title(extractTitle(entriesForNctId))
                        .acronym(extractAcronym(entriesForNctId))
                        .phase(Phase.UNKNOWN)
                        .countries(Sets.newHashSet(extractCountry(entriesForNctId)))
                        .genderCriterium(extractGenderCriterium(entriesForNctId))
                        .indications(extractIndications(entriesForNctId))
                        .anyMolecularCriteria(anyMolecularCriteria)
                        .urls(Sets.newHashSet(extractUrl(entriesForNctId)))
                        .build());
            }

            tracker.update();
        }

        LOGGER.info("Extracted {} actionable trials from {} curated trial entries", actionableTrials.size(), entries.size());

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.HARTWIG_TRIAL_CURATED.refGenomeVersion())
                .eventInterpretations(new HashSet<>())
                .trials(new ArrayList<>(actionableTrials))
                .build();
    }

    @NotNull
    private static String extractTitle(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return extractSingleValueNotNull(entriesForNctId, HartwigTrialEntry::title);
    }

    @Nullable
    private static String extractAcronym(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return extractSingleValue(entriesForNctId, HartwigTrialEntry::acronym);
    }

    @NotNull
    private static Country extractCountry(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return extractSingleValueNotNull(entriesForNctId, entry -> ImmutableCountry.builder().name(entry.country()).build());
    }

    @Nullable
    private static GenderCriterium extractGenderCriterium(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return extractSingleValue(entriesForNctId, HartwigTrialEntry::genderCriterium);
    }

    @NotNull
    private static Set<Indication> extractIndications(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return entriesForNctId.stream()
                .map(entry -> ImmutableIndication.builder()
                        .applicableType(ImmutableCancerType.builder().name(entry.cancerType()).doid(entry.cancerTypeDoid()).build())
                        .build())
                .collect(Collectors.toSet());
    }

    @NotNull
    private Set<MolecularCriterium> extractMolecularCriteria(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return entriesForNctId.stream().map(molecularCriteriumExtractor::create).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @NotNull
    private static String extractUrl(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return extractSingleValueNotNull(entriesForNctId, HartwigTrialEntry::url);
    }

    @NotNull
    private static <T> T extractSingleValueNotNull(@NotNull List<HartwigTrialEntry> entries,
            @NotNull Function<HartwigTrialEntry, T> mapper) {
        T item = extractSingleValue(entries, mapper);
        if (item == null) {
            throw new IllegalStateException("Could not extract a not-null for value for one NCT ID");
        }
        return item;
    }

    @Nullable
    private static <T> T extractSingleValue(@NotNull List<HartwigTrialEntry> entries, @NotNull Function<HartwigTrialEntry, T> mapper) {
        Set<T> items = entries.stream().map(mapper).collect(Collectors.toSet());

        if (items.size() != 1) {
            throw new IllegalStateException("Invalid number of items for one NCT ID");
        }

        return items.iterator().next();
    }
}
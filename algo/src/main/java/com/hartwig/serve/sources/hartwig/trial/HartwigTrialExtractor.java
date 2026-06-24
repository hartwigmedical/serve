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
import com.hartwig.serve.common.classification.EventClassifier;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.ImmutableActionableEventImpl;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ImmutableCancerType;
import com.hartwig.serve.datamodel.common.ImmutableIndication;
import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.molecular.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantAnnotation;
import com.hartwig.serve.datamodel.molecular.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.molecular.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.molecular.range.ActionableRange;
import com.hartwig.serve.datamodel.molecular.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.molecular.range.RangeAnnotation;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.Country;
import com.hartwig.serve.datamodel.trial.GenderCriterium;
import com.hartwig.serve.datamodel.trial.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.trial.ImmutableCountry;
import com.hartwig.serve.datamodel.trial.Phase;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.EventExtractorOutputFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HartwigTrialExtractor {

    private static final Logger LOGGER = LogManager.getLogger(HartwigTrialExtractor.class);
    
    @NotNull
    private final EventClassifier classifier;
    @NotNull
    private final EventExtractor extractor;
    @NotNull
    private final String noGeneIdentifier;

    public HartwigTrialExtractor(@NotNull EventClassifier classifier, @NotNull EventExtractor extractor, @NotNull String noGeneIdentifier) {
        this.classifier = classifier;
        this.extractor = extractor;
        this.noGeneIdentifier = noGeneIdentifier;
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
    private Set<MolecularCriterium> extractMolecularCriteria(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return entriesForNctId.stream().map(this::createMolecularCriteria).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Nullable
    private MolecularCriterium createMolecularCriteria(@NotNull HartwigTrialEntry entry) {
        String gene = entry.actionableGene();
        String event = entry.actionableEvent();

        EventType type = classifier.determineType(gene, event);
        if (type == EventType.UNKNOWN) {
            LOGGER.warn("Could not resolve event type for Hartwig Trial with gene '{}' and event '{}'", gene, event);
            return null;
        }

        if (type == EventType.COMBINED) {
            LOGGER.warn("COMBINED rules for Hartwig Trial should be configured independently");
            return null;
        }

        EventExtractorOutput output = extractor.extract(gene, null, type, event);
        if (EventExtractorOutputFunctions.eventCount(output) == 0) {
            LOGGER.warn("Could not extract event from for Hartwig Trial for gene '{}' and event '{}'", gene, event);
            return null;
        }
        
        ActionableEvent actionableEvent = ImmutableActionableEventImpl.builder()
                .sourceDate(entry.date())
                .sourceEvent(gene.equals(noGeneIdentifier) ? event : gene + " " + event)
                .sourceUrls(Sets.newHashSet(entry.url()))
                .build();

        return ImmutableMolecularCriterium.builder()
                .hotspots(createActionableHotspots(actionableEvent, output.variants()))
                .codons(createActionableRanges(actionableEvent, output.codons()))
                .exons(createActionableRanges(actionableEvent, output.exons()))
                .genes(createActionableGene(actionableEvent, output.geneLevel(), output.copyNumber()))
                .fusions(createActionableFusions(actionableEvent, output.fusionPair()))
                .characteristics(createActionableCharacteristics(actionableEvent, output.characteristic()))
                .hla(createActionableHla(actionableEvent, output.hla()))
                .build();
    }

    @NotNull
    private static Set<ActionableHotspot> createActionableHotspots(@NotNull ActionableEvent event,
            @Nullable List<VariantAnnotation> variants) {
        if (variants == null) {
            return Sets.newHashSet();
        }

        return Sets.newHashSet(ImmutableActionableHotspot.builder().from(event).variants(variants).build());
    }

    @NotNull
    private static Set<ActionableRange> createActionableRanges(@NotNull ActionableEvent event,
            @Nullable List<? extends RangeAnnotation> ranges) {
        if (ranges == null) {
            return Sets.newHashSet();
        }

        return ranges.stream().map(range -> ImmutableActionableRange.builder().from(event).from(range).build()).collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ActionableGene> createActionableGene(@NotNull ActionableEvent event,
            @Nullable GeneAnnotation geneAnnotation1, @Nullable GeneAnnotation geneAnnotation2) {
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        
        if (geneAnnotation1 != null) {
            actionableGenes.add(ImmutableActionableGene.builder().from(event).from(geneAnnotation1).build());
        }

        if (geneAnnotation2 != null) {
            actionableGenes.add(ImmutableActionableGene.builder().from(event).from(geneAnnotation2).build());
        }
        
        return actionableGenes;
    }

    @NotNull
    private static Set<ActionableFusion> createActionableFusions(@NotNull ActionableEvent event, @Nullable FusionPair fusionPair) {
        if (fusionPair == null) {
            return Sets.newHashSet();
        }
        
        return Sets.newHashSet(ImmutableActionableFusion.builder().from(event).from(fusionPair).build());
    }

    @NotNull
    private Set<ActionableCharacteristic> createActionableCharacteristics(@NotNull ActionableEvent event,
            @Nullable TumorCharacteristic characteristic) {
        if (characteristic == null) {
            return Sets.newHashSet();
        }
        
        return Sets.newHashSet(ImmutableActionableCharacteristic.builder().from(event).from(characteristic).build());
    }

    @NotNull
    private Set<ActionableHLA> createActionableHla(@NotNull ActionableEvent event, @Nullable ImmunoHLA hla) {
        if (hla == null) {
            return Sets.newHashSet();
        }

        return Sets.newHashSet(ImmutableActionableHLA.builder().from(event).from(hla).build());
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

    @NotNull
    private static GenderCriterium extractGenderCriterium(@NotNull List<HartwigTrialEntry> entriesForNctId) {
        return extractSingleValueNotNull(entriesForNctId, HartwigTrialEntry::genderCriterium);
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
package com.hartwig.serve.sources.ckb;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.classification.CkbEventTypeExtractor;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
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
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.molecular.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.molecular.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.molecular.range.ActionableRange;
import com.hartwig.serve.datamodel.molecular.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.molecular.range.RangeAnnotation;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// maybe this should be folded into CkbMolecularCriteriaExtractor
public class CkbVariantCriteriaExtractor {
    private static final Logger LOGGER = LogManager.getLogger(CkbVariantCriteriaExtractor.class);

    @NotNull
    private final EventExtractor eventExtractor;

    public CkbVariantCriteriaExtractor(@NotNull EventExtractor eventExtractor) {
        this.eventExtractor = eventExtractor;
    }

    // Note the actionableEvent parameter comes from the combined event, feels suspicious
    @Nullable
    public MolecularCriterium extractCriteria(@NotNull Variant variant, ActionableEvent actionableEvent) {
        EventType eventType = CkbEventTypeExtractor.classify(variant);

        if (eventType == EventType.COMBINED) {
            throw new IllegalStateException("Should not have combined event for single variant: " + variant.fullName());
        } else if (eventType == EventType.UNKNOWN) {
            LOGGER.warn("No known event type for variant: '{}'", variant.fullName());
            return null;
        }

        String event = CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = CkbEventAndGeneExtractor.extractGene(variant);

        EventExtractorOutput extractionOutput = curateCodons(eventExtractor.extract(gene, null, eventType, event));

        return createMolecularCriterium(extractionOutput, actionableEvent);
    }

    @NotNull
    private MolecularCriterium createMolecularCriterium(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        return ImmutableMolecularCriterium.builder()
                .oneOfEachHotspots(Set.of(hotspotCriteria(extractionOutput, actionableEvent)))
                .codons(codonCriteria(extractionOutput, actionableEvent))
                .exons(exonCriteria(extractionOutput, actionableEvent))
                .genes(geneCriteria(extractionOutput, actionableEvent))
                .fusions(fusionCriteria(extractionOutput, actionableEvent))
                .characteristics(extractActionableCharacteristic(extractionOutput.characteristic(), actionableEvent))
                .hla(extractActionableHLA(extractionOutput.hla(), actionableEvent))
                .build();
    }

    @NotNull
    private Set<ActionableHotspot> hotspotCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        List<VariantHotspot> extractionHotspots = extractionOutput.hotspots();
        if (extractionHotspots == null) {
            return Collections.emptySet();
        }

        return extractActionableHotspots(extractionHotspots, actionableEvent);
    }

    @NotNull
    private Set<ActionableRange> codonCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        List<CodonAnnotation> extractionCodons = extractionOutput.codons();
        if (extractionCodons == null) {
            return Collections.emptySet();
        }

        return extractActionableRanges(extractionCodons, actionableEvent);
    }

    @NotNull
    private Set<ActionableRange> exonCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        List<ExonAnnotation> extractionExons = extractionOutput.exons();
        if (extractionExons == null) {
            return Collections.emptySet();
        }

        return extractActionableRanges(extractionExons, actionableEvent);
    }

    @NotNull
    private Set<ActionableGene> geneCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        return Stream.of(extractionOutput.geneLevel(), extractionOutput.copyNumber())
                .filter(Objects::nonNull)
                .map(annotation -> extractActionableGenes(annotation, actionableEvent))
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ActionableFusion> extractActionableFusions(@Nullable FusionPair fusionPair,
            @NotNull ActionableEvent actionableEvent) {
        return Set.of(ImmutableActionableFusion.builder().from(fusionPair).from(actionableEvent).build());
    }

    @NotNull
    private Set<ActionableFusion> fusionCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent) {
        if (extractionOutput.fusionPair() == null) {
            return Collections.emptySet();
        }

        return extractActionableFusions(extractionOutput.fusionPair(), actionableEvent);
    }

    @Nullable
    private MolecularCriterium characteristicsCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {
        if (extractionOutput.characteristic() == null) {
            return null;
        }

        Set<ActionableCharacteristic> actionableCharacteristic =
                extractActionableCharacteristic(extractionOutput.characteristic(), actionableEvent);
        return ImmutableMolecularCriterium.builder().characteristics(actionableCharacteristic).build();
    }

    @Nullable
    private MolecularCriterium hlaCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {
        if (extractionOutput.hla() == null) {
            return null;
        }

        Set<ActionableHLA> actionableHla = extractActionableHLA(extractionOutput.hla(), actionableEvent);
        return ImmutableMolecularCriterium.builder().hla(actionableHla).build();
    }

    @NotNull
    private static Set<ActionableHotspot> extractActionableHotspots(@NotNull List<VariantHotspot> hotspots,
            @NotNull ActionableEvent actionableEvent) {
        return hotspots.stream()
                .map(hotspot -> ImmutableActionableHotspot.builder().from(hotspot).from(actionableEvent).build())
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ActionableCharacteristic> extractActionableCharacteristic(@Nullable TumorCharacteristic characteristic,
            @NotNull ActionableEvent actionableEvent) {
        if (characteristic == null) {
            return Collections.emptySet();
        } else {
            return Set.of(ImmutableActionableCharacteristic.builder().from(characteristic).from(actionableEvent).build());
        }
    }

    @NotNull
    private static Set<ActionableRange> extractActionableRanges(@NotNull List<? extends RangeAnnotation> ranges,
            @NotNull ActionableEvent actionableEvent) {
        return ranges.stream()
                .map(range -> ImmutableActionableRange.builder().from(range).from(actionableEvent).build())
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ActionableHLA> extractActionableHLA(@Nullable ImmunoHLA hla, @NotNull ActionableEvent actionableEvent) {
        if (hla == null) {
            return Collections.emptySet();
        } else {
            return Set.of(ImmutableActionableHLA.builder().from(hla).from(actionableEvent).build());
        }
    }

    @NotNull
    private static ActionableFusion extractActionableFusion(@Nullable FusionPair fusionPair,
            @NotNull ActionableEvent actionableEvent) {
        return ImmutableActionableFusion.builder().from(fusionPair).from(actionableEvent).build();
    }

    @NotNull
    public static ActionableGene extractActionableGenes(@NotNull GeneAnnotation geneAnnotation, @NotNull ActionableEvent actionableEvent) {
        return ImmutableActionableGene.builder().from(geneAnnotation).from(actionableEvent).build();
    }

    @VisibleForTesting
    @NotNull
    static EventExtractorOutput curateCodons(@NotNull EventExtractorOutput extractorOutput) {
        List<CodonAnnotation> codonAnnotations = extractorOutput.codons();
        if (codonAnnotations == null) {
            return extractorOutput;
        }
        List<CodonAnnotation> codons = codonAnnotations.stream().map(codon -> {
            if (codon.gene().equals("BRAF") && codon.inputCodonRank() == 600) {
                return ImmutableCodonAnnotation.copyOf(codon)
                        .withInputTranscript("ENST00000646891")
                        .withStart(140753335)
                        .withEnd(140753337);
            }
            return codon;
        }).collect(Collectors.toList());

        return ImmutableEventExtractorOutput.copyOf(extractorOutput).withCodons(codons);
    }
}

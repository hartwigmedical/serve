package com.hartwig.serve.sources.ckb;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.datamodel.ImmutableActionableEventImpl;
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
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CkbMolecularCriteriaExtractor {

    @NotNull
    public static MolecularCriterium createMolecularCriterium(CkbEntry entry, Set<EventExtractorOutput> eventExtractorOutputs) {
        String sourceEvent = combinedSourceEvent(entry);
        ActionableEvent actionableEvent = toActionableEvent(sourceEvent, entry);

        List<MolecularCriterium> molecularCriteria = eventExtractorOutputs.stream()
                .map(eventExtractorOutput -> createMolecularCriterium(eventExtractorOutput, actionableEvent))
                .collect(Collectors.toList());

        return combine(molecularCriteria);
    }

    @NotNull
    public static MolecularCriterium combine(@NotNull List<MolecularCriterium> criteria) {
        return criteria.stream().reduce(CkbMolecularCriteriaExtractor::combine)
                .orElse(ImmutableMolecularCriterium.builder().build());
    }

    @NotNull
    public static MolecularCriterium combine(@NotNull MolecularCriterium criteria1, @NotNull MolecularCriterium criteria2) {
        return ImmutableMolecularCriterium.builder()
                .addAllHotspots(Sets.union(criteria1.hotspots(), criteria2.hotspots()))
                .addAllCodons(Sets.union(criteria1.codons(), criteria2.codons()))
                .addAllExons(Sets.union(criteria1.exons(), criteria2.exons()))
                .addAllGenes(Sets.union(criteria1.genes(), criteria2.genes()))
                .addAllFusions(Sets.union(criteria1.fusions(), criteria2.fusions()))
                .addAllCharacteristics(Sets.union(criteria1.characteristics(), criteria2.characteristics()))
                .addAllHla(Sets.union(criteria1.hla(), criteria2.hla()))
                .build();
    }

    @NotNull
    public static String combinedSourceEvent(@NotNull CkbEntry entry) {
        return entry.variants().stream()
                .map(variant -> {
                    String event = CkbEventAndGeneExtractor.extractEvent(variant);
                    String gene = CkbEventAndGeneExtractor.extractGene(variant);
                    return gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;
                })
                .collect(Collectors.joining(" & "));
    }

    @VisibleForTesting
    @NotNull
    static MolecularCriterium createMolecularCriterium(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {
        Set<ActionableHotspot> hotspot = hotspotCriteria(extractionOutput, actionableEvent);
        return ImmutableMolecularCriterium.builder()
                .hotspots(hotspotCriteria(extractionOutput, actionableEvent))
                .codons(codonCriteria(extractionOutput, actionableEvent))
                .exons(exonCriteria(extractionOutput, actionableEvent))
                .genes(geneCriteria(extractionOutput, actionableEvent))
                .fusions(fusionCriteria(extractionOutput, actionableEvent))
                .characteristics(extractActionableCharacteristic(extractionOutput.characteristic(), actionableEvent))
                .hla(extractActionableHLA(extractionOutput.hla(), actionableEvent))
                .build();
    }

    @NotNull
    private static Set<ActionableHotspot> hotspotCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        if (extractionOutput.variants() == null) {
            return Collections.emptySet();
        }

        return Set.of(extractActionableHotspots(extractionOutput.variants(), actionableEvent));
    }

    @NotNull
    private static Set<ActionableRange> codonCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        List<CodonAnnotation> extractionCodons = extractionOutput.codons();
        if (extractionCodons == null) {
            return Collections.emptySet();
        }

        return extractActionableRanges(extractionCodons, actionableEvent);
    }

    @NotNull
    private static Set<ActionableRange> exonCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {

        List<ExonAnnotation> extractionExons = extractionOutput.exons();
        if (extractionExons == null) {
            return Collections.emptySet();
        }

        return extractActionableRanges(extractionExons, actionableEvent);
    }

    @NotNull
    private static Set<ActionableGene> geneCriteria(@NotNull EventExtractorOutput extractionOutput,
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
    private static Set<ActionableFusion> fusionCriteria(@NotNull EventExtractorOutput extractionOutput,
            @NotNull ActionableEvent actionableEvent) {
        if (extractionOutput.fusionPair() == null) {
            return Collections.emptySet();
        }

        return extractActionableFusions(extractionOutput.fusionPair(), actionableEvent);
    }

    @NotNull
    private static ActionableHotspot extractActionableHotspots(@NotNull List<VariantAnnotation> variants,
            @NotNull ActionableEvent actionableEvent) {
        return ImmutableActionableHotspot.builder().from(actionableEvent)
                .addAllVariants(variants)
                .build();
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

    @NotNull
    private static ActionableEvent toActionableEvent(@NotNull String sourceEvent, @NotNull CkbEntry entry) {
        String sourceUrl = "https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=" + entry.profileId();
        LocalDate sourceDate = entry.createDate();
        return ImmutableActionableEventImpl.builder().sourceDate(sourceDate).sourceEvent(sourceEvent).sourceUrls(Set.of(sourceUrl)).build();
    }
}

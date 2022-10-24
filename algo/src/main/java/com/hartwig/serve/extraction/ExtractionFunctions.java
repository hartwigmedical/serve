package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.actionability.ActionableCharacteristicUrlConsolidator;
import com.hartwig.serve.actionability.ActionableEventUrlMerger;
import com.hartwig.serve.actionability.ActionableFusionUrlConsolidator;
import com.hartwig.serve.actionability.ActionableGeneUrlConsolidator;
import com.hartwig.serve.actionability.ActionableHLAUrlConsolidator;
import com.hartwig.serve.actionability.ActionableHotspotUrlConsolidator;
import com.hartwig.serve.actionability.ActionableRangeUrlConsolidator;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.extraction.codon.CodonFunctions;
import com.hartwig.serve.extraction.codon.KnownCodon;
import com.hartwig.serve.extraction.copynumber.CopyNumberFunctions;
import com.hartwig.serve.extraction.copynumber.KnownCopyNumber;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.exon.ExonFunctions;
import com.hartwig.serve.extraction.exon.KnownExon;
import com.hartwig.serve.extraction.fusion.FusionFunctions;
import com.hartwig.serve.extraction.fusion.KnownFusionPair;
import com.hartwig.serve.extraction.hotspot.HotspotFunctions;
import com.hartwig.serve.extraction.hotspot.KnownHotspot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ExtractionFunctions {

    private static final Logger LOGGER = LogManager.getLogger(ExtractionFunctions.class);

    private ExtractionFunctions() {
    }

    @NotNull
    public static ExtractionResult consolidateActionableEvents(@NotNull ExtractionResult result) {
        return ImmutableExtractionResult.builder()
                .from(result)
                .actionableHotspots(ActionableEventUrlMerger.merge(result.actionableHotspots(), new ActionableHotspotUrlConsolidator()))
                .actionableRanges(ActionableEventUrlMerger.merge(result.actionableRanges(), new ActionableRangeUrlConsolidator()))
                .actionableGenes(ActionableEventUrlMerger.merge(result.actionableGenes(), new ActionableGeneUrlConsolidator()))
                .actionableFusions(ActionableEventUrlMerger.merge(result.actionableFusions(), new ActionableFusionUrlConsolidator()))
                .actionableCharacteristics(ActionableEventUrlMerger.merge(result.actionableCharacteristics(),
                        new ActionableCharacteristicUrlConsolidator()))
                .actionableHLA(ActionableEventUrlMerger.merge(result.actionableHLA(), new ActionableHLAUrlConsolidator()))
                .build();
    }

    @NotNull
    public static ExtractionResult merge(@NotNull List<ExtractionResult> results) {
        RefGenomeVersion version = uniqueVersion(results);
        ImmutableExtractionResult.Builder mergedBuilder = ImmutableExtractionResult.builder().refGenomeVersion(version);

        Set<EventInterpretation> allEventInterpretations = Sets.newHashSet();
        Set<KnownHotspot> allHotspots = Sets.newHashSet();
        Set<KnownCodon> allCodons = Sets.newHashSet();
        Set<KnownExon> allExons = Sets.newHashSet();
        Set<KnownCopyNumber> allCopyNumbers = Sets.newHashSet();
        Set<KnownFusionPair> allFusionPairs = Sets.newHashSet();

        for (ExtractionResult result : results) {
            allEventInterpretations.addAll(result.eventInterpretations());
            allHotspots.addAll(result.knownHotspots());
            allCodons.addAll(result.knownCodons());
            allExons.addAll(result.knownExons());
            allCopyNumbers.addAll(result.knownCopyNumbers());
            allFusionPairs.addAll(result.knownFusionPairs());

            mergedBuilder.addAllActionableHotspots(result.actionableHotspots());
            mergedBuilder.addAllActionableRanges(result.actionableRanges());
            mergedBuilder.addAllActionableGenes(result.actionableGenes());
            mergedBuilder.addAllActionableFusions(result.actionableFusions());
            mergedBuilder.addAllActionableCharacteristics(result.actionableCharacteristics());
            mergedBuilder.addAllActionableHLA(result.actionableHLA());
        }

        ExtractionResult mergedResult = mergedBuilder.eventInterpretations(allEventInterpretations)
                .knownHotspots(HotspotFunctions.consolidate(allHotspots))
                .knownCodons(CodonFunctions.consolidate(allCodons))
                .knownExons(ExonFunctions.consolidate(allExons))
                .knownCopyNumbers(CopyNumberFunctions.consolidate(allCopyNumbers))
                .knownFusionPairs(FusionFunctions.consolidate(allFusionPairs))
                .build();

        return consolidateActionableEvents(mergedResult);
    }

    @NotNull
    private static RefGenomeVersion uniqueVersion(@NotNull List<ExtractionResult> results) {
        if (results.isEmpty()) {
            RefGenomeVersion defaultVersion = RefGenomeVersion.V38;
            LOGGER.warn("Cannot extract ref genome version for empty list of results. Reverting to default {}", defaultVersion);
            return defaultVersion;
        }

        RefGenomeVersion version = results.get(0).refGenomeVersion();
        for (ExtractionResult result : results) {
            if (result.refGenomeVersion() != version) {
                throw new IllegalStateException("Ref genome version is not unique amongst list of extraction results");
            }
        }

        return version;
    }
}
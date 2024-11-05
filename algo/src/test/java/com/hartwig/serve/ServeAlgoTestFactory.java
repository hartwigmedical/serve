package com.hartwig.serve;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.events.TestEventInterpretationFactory;

import org.jetbrains.annotations.NotNull;

public final class ServeAlgoTestFactory {

    private ServeAlgoTestFactory() {
    }

    @NotNull
    public static ExtractionResult createResultForSource(@NotNull Knowledgebase source) {
        KnownEvents knownEvents = ImmutableKnownEvents.builder()
                .addHotspots(HotspotTestFactory.createTestKnownHotspotForSource(source))
                .addCodons(RangeTestFactory.createTestKnownCodonForSource(source))
                .addExons(RangeTestFactory.createTestKnownExonForSource(source))
                .addGenes(GeneTestFactory.createTestKnownGeneForSource(source))
                .addCopyNumbers(GeneTestFactory.createTestKnownCopyNumberForSource(source))
                .addFusions(FusionTestFactory.createTestKnownFusionForSource(source))
                .build();

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(source.refGenomeVersion())
                .knownEvents(knownEvents)
                .eventInterpretations(Set.of(TestEventInterpretationFactory.createTestEventInterpretationForSource(source)))
                .efficacyEvidences(List.of(DatamodelTestFactory.efficacyEvidenceBuilder().source(source).build()))
                .clinicalTrials(List.of(DatamodelTestFactory.clinicalTrialBuilder().source(source).build()))
                .build();
    }
}
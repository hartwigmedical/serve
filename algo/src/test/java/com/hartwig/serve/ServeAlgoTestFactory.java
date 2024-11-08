package com.hartwig.serve;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.ActionableTrialTestFactory;
import com.hartwig.serve.datamodel.EfficacyEvidenceTestFactory;
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

    @NotNull
    public static ExtractionResult createMinimalResultForSource(@NotNull Knowledgebase source) {
        return ImmutableExtractionResult.builder().refGenomeVersion(source.refGenomeVersion()).build();
    }

    @NotNull
    public static ExtractionResult createProperResultForSource(@NotNull Knowledgebase source) {
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
                .efficacyEvidences(List.of(EfficacyEvidenceTestFactory.builder().source(source).build()))
                .trials(List.of(ActionableTrialTestFactory.builder().source(source).build()))
                .build();
    }
}
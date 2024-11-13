package com.hartwig.serve;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidenceTestFactory;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.molecular.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.molecular.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.molecular.range.RangeTestFactory;
import com.hartwig.serve.datamodel.trial.TrialTestFactory;
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
                .evidences(List.of(EfficacyEvidenceTestFactory.builder().source(source).build()))
                .trials(List.of(TrialTestFactory.builder().source(source).build()))
                .build();
    }
}
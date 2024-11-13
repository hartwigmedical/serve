package com.hartwig.serve.datamodel;

import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.molecular.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.molecular.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.molecular.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.molecular.range.RangeTestFactory;

import org.jetbrains.annotations.NotNull;

public class TestServeDatabaseFactory {

    @NotNull
    public static ServeDatabase create() {
        Knowledgebase source = Knowledgebase.UNKNOWN;
        return ImmutableServeDatabase.builder().version("unittest").records(Map.of(RefGenome.V37, createServeRecord(source))).build();
    }

    @NotNull
    public static ServeRecord createServeRecord(@NotNull Knowledgebase source) {
        return ImmutableServeRecord.builder()
                .knownEvents(createKnownEvents(source))
                .evidences(createEfficacyEvidences())
                .trials(createActionableTrials())
                .build();
    }

    @NotNull
    public static KnownEvents createKnownEvents(@NotNull Knowledgebase source) {
        return ImmutableKnownEvents.builder()
                .addHotspots(HotspotTestFactory.createTestKnownHotspotForSource(source))
                .addCodons(RangeTestFactory.createTestKnownCodonForSource(source))
                .addExons(RangeTestFactory.createTestKnownExonForSource(source))
                .addGenes(GeneTestFactory.createTestKnownGeneForSource(source))
                .addCopyNumbers(GeneTestFactory.createTestKnownCopyNumberForSource(source))
                .addFusions(FusionTestFactory.createTestKnownFusionForSource(source))
                .build();
    }

    @NotNull
    public static List<EfficacyEvidence> createEfficacyEvidences() {
        return List.of(EfficacyEvidenceTestFactory.builder().build());
    }

    @NotNull
    public static List<ActionableTrial> createActionableTrials() {
        return List.of(ActionableTrialTestFactory.builder().build());
    }
}

package com.hartwig.serve.datamodel;

import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.jetbrains.annotations.NotNull;

public class TestServeDatabaseFactory {

    @NotNull
    public static ServeDatabase create() {
        Knowledgebase source = Knowledgebase.CKB_EVIDENCE;
        return ImmutableServeDatabase.builder()
                .serveVersion("unittest")
                .serveDataPerRefGenome(Map.of(RefGenome.V37, createServeRecord(source)))
                .build();
    }

    @NotNull
    public static ServeRecord createServeRecord(@NotNull Knowledgebase source) {
        return ImmutableServeRecord.builder()
                .knownEvents(createKnownEvents(source))
                .efficacyEvidences(createEfficacyEvidences())
                .clinicalTrials(createClinicalTrials())
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
        return List.of(DatamodelTestFactory.createTestEfficacyEvidence());
    }

    @NotNull
    public static List<ClinicalTrial> createClinicalTrials() {
        return List.of(DatamodelTestFactory.createTestClinicalTrial());
    }
}

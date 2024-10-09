package com.hartwig.serve.datamodel;

import com.hartwig.serve.datamodel.characteristic.CharacteristicTestFactory;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.immuno.ImmunoTestFactory;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.jetbrains.annotations.NotNull;

public class TestServeRecordFactory {

    public static final String GENE = "EGFR";

    @NotNull
    public static ServeRecord create() {
        Knowledgebase source = Knowledgebase.CKB_EVIDENCE;
        return ImmutableServeRecord.builder()
                .refGenomeVersion(RefGenome.V37)
                .serveVersion("unittest")
                .knownEvents(createKnownEvents(source))
                .actionableEvents(createActionableEvents(source))
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
    public static ActionableEvents createActionableEvents(@NotNull Knowledgebase source) {
        return ImmutableActionableEvents.builder()
                .addHotspots(HotspotTestFactory.createTestActionableHotspotForSource(source))
                .addCodons(RangeTestFactory.createTestActionableRangeForSource(source))
                .addExons(RangeTestFactory.createTestActionableRangeForSource(source))
                .addGenes(GeneTestFactory.createTestActionableGeneForSource(source))
                .addFusions(FusionTestFactory.createTestActionableFusionForSource(source))
                .addCharacteristics(CharacteristicTestFactory.createTestActionableCharacteristicForSource(source))
                .addHla(ImmunoTestFactory.createTestActionableHLAForSource(source))
                .build();
    }
}

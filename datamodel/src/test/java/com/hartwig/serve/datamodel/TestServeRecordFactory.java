package com.hartwig.serve.datamodel;

import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.jetbrains.annotations.NotNull;

public class TestServeRecordFactory {

    public static final String GENE = "EGFR";

    @NotNull
    public static ServeRecord create() {
        return ImmutableServeRecord.builder()
                .refGenomeVersion(RefGenome.V37)
                .knownEvents(createKnownEvents())
                .actionableEvents(createActionableEvents())
                .build();
    }

    @NotNull
    public static KnownEvents createKnownEvents() {
        return ImmutableKnownEvents.builder()
                .addHotspots(HotspotTestFactory.knownHotspotBuilder().gene(GENE).build())
                .addCodons(RangeTestFactory.knownCodonBuilder().gene(GENE).build())
                .addExons(RangeTestFactory.knownExonBuilder().gene(GENE).build())
                .addGenes(GeneTestFactory.knownGeneBuilder().gene(GENE).build())
                .addCopyNumbers(GeneTestFactory.knownCopyNumberBuilder().gene(GENE).build())
                .addFusions(FusionTestFactory.knownFusionBuilder().geneUp(GENE).build())
                .addFusions(FusionTestFactory.knownFusionBuilder().geneDown(GENE).build())
                .build();
    }

    @NotNull
    public static ActionableEvents createActionableEvents() {
        return ImmutableActionableEvents.builder()
                .addGenes(GeneTestFactory.actionableGeneBuilder().gene(GENE).build())
                .addFusions(FusionTestFactory.actionableFusionBuilder().geneUp(GENE).build())
                .addFusions(FusionTestFactory.actionableFusionBuilder().geneDown(GENE).build())
                .build();
    }
}

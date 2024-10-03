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
                .addKnownHotspots(HotspotTestFactory.knownHotspotBuilder().gene(GENE).build())
                .addKnownCodons(RangeTestFactory.knownCodonBuilder().gene(GENE).build())
                .addKnownExons(RangeTestFactory.knownExonBuilder().gene(GENE).build())
                .addKnownGenes(GeneTestFactory.knownGeneBuilder().gene(GENE).build())
                .addKnownCopyNumbers(GeneTestFactory.knownCopyNumberBuilder().gene(GENE).build())
                .addKnownFusions(FusionTestFactory.knownFusionBuilder().geneUp(GENE).build())
                .addKnownFusions(FusionTestFactory.knownFusionBuilder().geneDown(GENE).build())
                .addActionableGenes(GeneTestFactory.actionableGeneBuilder().gene(GENE).build())
                .addActionableFusions(FusionTestFactory.actionableFusionBuilder().geneUp(GENE).build())
                .addActionableFusions(FusionTestFactory.actionableFusionBuilder().geneDown(GENE).build())
                .build();

    }
}

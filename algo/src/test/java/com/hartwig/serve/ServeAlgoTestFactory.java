package com.hartwig.serve;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.characteristic.CharacteristicTestFactory;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.immuno.ImmunoTestFactory;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;

import org.jetbrains.annotations.NotNull;

public final class ServeAlgoTestFactory {

    private ServeAlgoTestFactory() {
    }

    @NotNull
    public static ExtractionResult createResultForSource(@NotNull Knowledgebase source) {
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(source.refGenomeVersion())
                .addKnownHotspots(HotspotTestFactory.createTestKnownHotspotForSource(source))
                .addKnownCodons(RangeTestFactory.createTestKnownCodonForSource(source))
                .addKnownExons(RangeTestFactory.createTestKnownExonForSource(source))
                .addKnownCopyNumbers(GeneTestFactory.createTestKnownCopyNumberForSource(source))
                .addKnownFusions(FusionTestFactory.createTestKnownFusionForSource(source))
                .addActionableHotspots(HotspotTestFactory.createTestActionableHotspotForSource(source))
                .addActionableCodons(RangeTestFactory.createTestActionableRangeForSource(source))
                .addActionableExons(RangeTestFactory.createTestActionableRangeForSource(source))
                .addActionableGenes(GeneTestFactory.createTestActionableGeneForSource(source))
                .addActionableFusions(FusionTestFactory.createTestActionableFusionForSource(source))
                .addActionableCharacteristics(CharacteristicTestFactory.createTestActionableCharacteristicForSource(source))
                .addActionableHLA(ImmunoTestFactory.createTestActionableHLAForSource(source))
                .build();
    }
}
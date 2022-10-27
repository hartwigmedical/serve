package com.hartwig.serve.refgenome;

import static org.junit.Assert.assertTrue;

import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.range.ImmutableCodonAnnotation;
import com.hartwig.serve.datamodel.range.ImmutableExonAnnotation;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ConversionFilterTest {

    @Test
    public void canFilterGenes() {
        ConversionFilter filter = new ConversionFilter();

        ExtractionResult resultToFilter =
                createExtractionResultForGene(ConversionFilterFactory.GENES_TO_EXCLUDE_FOR_CONVERSION.iterator().next());
        ExtractionResult filtered = filter.filter(resultToFilter);
        assertTrue(filtered.knownHotspots().isEmpty());
        assertTrue(filtered.knownCodons().isEmpty());
        assertTrue(filtered.knownExons().isEmpty());
        assertTrue(filtered.knownCopyNumbers().isEmpty());
        assertTrue(filtered.knownFusionPairs().isEmpty());
        assertTrue(filtered.actionableGenes().isEmpty());
        assertTrue(filtered.actionableFusions().isEmpty());

        filter.reportUnusedFilterEntries();
    }

    @NotNull
    private static ExtractionResult createExtractionResultForGene(@NotNull String gene) {
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(RefGenomeVersion.V38)
                .addKnownHotspots(HotspotTestFactory.knownHotspotBuilder().gene(gene).build())
                .addKnownCodons(ImmutableKnownCodon.builder()
                        .from(RangeTestFactory.createTestKnownCodon())
                        .annotation(ImmutableCodonAnnotation.builder()
                                .from(RangeTestFactory.createTestCodonAnnotation())
                                .gene(gene)
                                .build())
                        .build())
                .addKnownExons(ImmutableKnownExon.builder()
                        .from(RangeTestFactory.createTestKnownExon())
                        .annotation(ImmutableExonAnnotation.builder().from(RangeTestFactory.createTestExonAnnotation()).gene(gene).build())
                        .build())
                .addKnownCopyNumbers(GeneTestFactory.knownCopyNumberBuilder().gene(gene).build())
                .addKnownFusionPairs(FusionTestFactory.knownFusionBuilder().geneUp(gene).build())
                .addKnownFusionPairs(FusionTestFactory.knownFusionBuilder().geneDown(gene).build())
                .addActionableGenes(ImmutableActionableGene.builder().from(GeneTestFactory.createTestActionableGene()).gene(gene).build())
                .addActionableFusions(FusionTestFactory.actionableFusionBuilder().geneUp(gene).build())
                .addActionableFusions(FusionTestFactory.actionableFusionBuilder().geneDown(gene).build())
                .build();
    }
}
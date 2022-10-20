package com.hartwig.serve.refgenome;

import static org.junit.Assert.assertTrue;

import com.hartwig.serve.ServeAlgoTestFactory;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableKnownCodon;
import com.hartwig.serve.extraction.copynumber.ImmutableKnownCopyNumber;
import com.hartwig.serve.extraction.exon.ImmutableExonAnnotation;
import com.hartwig.serve.extraction.exon.ImmutableKnownExon;
import com.hartwig.serve.extraction.fusion.ImmutableKnownFusionPair;
import com.hartwig.serve.extraction.hotspot.ImmutableKnownHotspot;

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
                .addKnownHotspots(ImmutableKnownHotspot.builder().from(ServeAlgoTestFactory.createTestKnownHotspot()).gene(gene).build())
                .addKnownCodons(ImmutableKnownCodon.builder()
                        .from(ServeAlgoTestFactory.createTestKnownCodon())
                        .annotation(ImmutableCodonAnnotation.builder()
                                .from(ServeAlgoTestFactory.createTestCodonAnnotation())
                                .gene(gene)
                                .build())
                        .build())
                .addKnownExons(ImmutableKnownExon.builder()
                        .from(ServeAlgoTestFactory.createTestKnownExon())
                        .annotation(ImmutableExonAnnotation.builder()
                                .from(ServeAlgoTestFactory.createTestExonAnnotation())
                                .gene(gene)
                                .build())
                        .build())
                .addKnownCopyNumbers(ImmutableKnownCopyNumber.builder()
                        .from(ServeAlgoTestFactory.createTestKnownCopyNumber())
                        .gene(gene)
                        .build())
                .addKnownFusionPairs(ImmutableKnownFusionPair.builder()
                        .from(ServeAlgoTestFactory.createTestKnownFusionPair())
                        .geneUp(gene)
                        .build())
                .addKnownFusionPairs(ImmutableKnownFusionPair.builder()
                        .from(ServeAlgoTestFactory.createTestKnownFusionPair())
                        .geneDown(gene)
                        .build())
                .addActionableGenes(ImmutableActionableGene.builder().from(ServeAlgoTestFactory.createTestActionableGene()).gene(gene).build())
                .addActionableFusions(ImmutableActionableFusion.builder()
                        .from(ServeAlgoTestFactory.createTestActionableFusion())
                        .geneUp(gene)
                        .build())
                .addActionableFusions(ImmutableActionableFusion.builder()
                        .from(ServeAlgoTestFactory.createTestActionableFusion())
                        .geneDown(gene)
                        .build())
                .build();
    }
}
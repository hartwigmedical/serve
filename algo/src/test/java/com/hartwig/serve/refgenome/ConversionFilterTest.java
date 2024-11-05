package com.hartwig.serve.refgenome;

import static org.junit.Assert.assertTrue;

import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.ClinicalTrialTestFactory;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.EfficacyEvidenceTestFactory;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.MolecularCriteriumTestFactory;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
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
        assertTrue(filtered.knownEvents().hotspots().isEmpty());
        assertTrue(filtered.knownEvents().codons().isEmpty());
        assertTrue(filtered.knownEvents().exons().isEmpty());
        assertTrue(filtered.knownEvents().genes().isEmpty());
        assertTrue(filtered.knownEvents().copyNumbers().isEmpty());
        assertTrue(filtered.knownEvents().fusions().isEmpty());
        assertTrue(filtered.efficacyEvidences().isEmpty());
        assertTrue(filtered.clinicalTrials().isEmpty());

        filter.reportUnusedFilterEntries();
    }

    @NotNull
    private static ExtractionResult createExtractionResultForGene(@NotNull String gene) {
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(RefGenome.V38)
                .knownEvents(createExhaustiveKnownEvents(gene))
                .addEfficacyEvidences(evidenceForHotspot(HotspotTestFactory.actionableHotspotBuilder().gene(gene).build()))
                .addEfficacyEvidences(evidenceForGene(GeneTestFactory.actionableGeneBuilder().gene(gene).build()))
                .addEfficacyEvidences(evidenceForFusion(FusionTestFactory.actionableFusionBuilder().geneUp(gene).build()))
                .addEfficacyEvidences(evidenceForFusion(FusionTestFactory.actionableFusionBuilder().geneDown(gene).build()))
                .addClinicalTrials(trialForHotspot(HotspotTestFactory.actionableHotspotBuilder().gene(gene).build()))
                .addClinicalTrials(trialForGene(GeneTestFactory.actionableGeneBuilder().gene(gene).build()))
                .addClinicalTrials(trialForFusion(FusionTestFactory.actionableFusionBuilder().geneUp(gene).build()))
                .addClinicalTrials(trialForFusion(FusionTestFactory.actionableFusionBuilder().geneDown(gene).build()))
                .build();
    }

    @NotNull
    private static KnownEvents createExhaustiveKnownEvents(@NotNull String gene) {
        return ImmutableKnownEvents.builder()
                .addHotspots(HotspotTestFactory.knownHotspotBuilder().gene(gene).build())
                .addCodons(RangeTestFactory.knownCodonBuilder().gene(gene).build())
                .addExons(RangeTestFactory.knownExonBuilder().gene(gene).build())
                .addGenes(GeneTestFactory.knownGeneBuilder().gene(gene).build())
                .addCopyNumbers(GeneTestFactory.knownCopyNumberBuilder().gene(gene).build())
                .addFusions(FusionTestFactory.knownFusionBuilder().geneUp(gene).build())
                .addFusions(FusionTestFactory.knownFusionBuilder().geneDown(gene).build())
                .build();
    }

    @NotNull
    private static EfficacyEvidence evidenceForHotspot(@NotNull ActionableHotspot hotspot) {
        return EfficacyEvidenceTestFactory.createWithMolecularCriterium(MolecularCriteriumTestFactory.createWithActionableHotspot(hotspot));
    }

    @NotNull
    private static EfficacyEvidence evidenceForGene(@NotNull ActionableGene gene) {
        return EfficacyEvidenceTestFactory.createWithMolecularCriterium(MolecularCriteriumTestFactory.createWithActionableGene(gene));
    }

    @NotNull
    private static EfficacyEvidence evidenceForFusion(@NotNull ActionableFusion fusion) {
        return EfficacyEvidenceTestFactory.createWithMolecularCriterium(MolecularCriteriumTestFactory.createWithActionableFusion(fusion));
    }

    @NotNull
    private static ClinicalTrial trialForHotspot(@NotNull ActionableHotspot hotspot) {
        return ClinicalTrialTestFactory.createWithMolecularCriterium(MolecularCriteriumTestFactory.createWithActionableHotspot(hotspot));
    }

    @NotNull
    private static ClinicalTrial trialForGene(@NotNull ActionableGene gene) {
        return ClinicalTrialTestFactory.createWithMolecularCriterium(MolecularCriteriumTestFactory.createWithActionableGene(gene));
    }

    @NotNull
    private static ClinicalTrial trialForFusion(@NotNull ActionableFusion fusion) {
        return ClinicalTrialTestFactory.createWithMolecularCriterium(MolecularCriteriumTestFactory.createWithActionableFusion(fusion));
    }
}
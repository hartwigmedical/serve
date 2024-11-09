package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import com.hartwig.serve.ckb.datamodel.variant.Gene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableGene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableVariant;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbVariantAnnotatorTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2022, 5, 20);
    private static final Gene ONCO_GENE = geneWith("oncogene");

    private static final Gene TSG_GENE = geneWith("tumor suppressor");
    private static final Gene BOTH_GENE = geneWith("both");
    private static final Gene UNKNOWN_GENE = geneWith("na");
    private static final String GENE_NAME = "gene";

    @Test
    public void shouldAnnotateHotspotUsingVariant() {
        KnownHotspot hotspot = HotspotTestFactory.knownHotspotBuilder().gene(GENE_NAME).build();
        KnownHotspot annotated = CkbVariantAnnotator.annotateHotspot(hotspot, variantWith(ONCO_GENE, "loss of function"));
        assertEquals(GeneRole.ONCO, annotated.geneRole());
        assertEquals(ProteinEffect.LOSS_OF_FUNCTION, annotated.proteinEffect());
        assertEquals(true, annotated.associatedWithDrugResistance());
    }

    @Test
    public void shouldAnnotateCodonUsingVariant() {
        KnownCodon codon = RangeTestFactory.knownCodonBuilder().gene(GENE_NAME).build();
        KnownCodon annotated = CkbVariantAnnotator.annotateCodon(codon, variantWith(TSG_GENE, "gain of function"));
        assertEquals(GeneRole.TSG, annotated.geneRole());
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, annotated.proteinEffect());
        assertEquals(true, annotated.associatedWithDrugResistance());
    }

    @Test
    public void shouldAnnotateExonUsingVariant() {
        KnownExon exon = RangeTestFactory.knownExonBuilder().gene(GENE_NAME).build();
        KnownExon annotated = CkbVariantAnnotator.annotateExon(exon, variantWith(BOTH_GENE, "no effect - predicted"));
        assertEquals(GeneRole.BOTH, annotated.geneRole());
        assertEquals(ProteinEffect.NO_EFFECT_PREDICTED, annotated.proteinEffect());
        assertEquals(true, annotated.associatedWithDrugResistance());
    }

    @Test
    public void shouldAnnotateCopyNumberUsingVariant() {
        KnownCopyNumber copyNumber = GeneTestFactory.knownCopyNumberBuilder().gene(GENE_NAME).build();
        KnownCopyNumber annotated = CkbVariantAnnotator.annotateCopyNumber(copyNumber, variantWith(UNKNOWN_GENE, "no effect"));
        assertEquals(GeneRole.UNKNOWN, annotated.geneRole());
        assertEquals(ProteinEffect.NO_EFFECT, annotated.proteinEffect());
        assertEquals(true, annotated.associatedWithDrugResistance());
    }

    @Test
    public void shouldAnnotateFusionUsingVariant() {
        KnownFusion exon = FusionTestFactory.knownFusionBuilder().build();
        KnownFusion annotated = CkbVariantAnnotator.annotateFusion(exon, variantWith(BOTH_GENE, "unknown"));
        assertEquals(ProteinEffect.UNKNOWN, annotated.proteinEffect());
        assertEquals(true, annotated.associatedWithDrugResistance());
    }

    @Test
    public void shouldAnnotateKnownGenesWithRoleFromVariant() {
        assertGeneRoleResolved(ONCO_GENE, GeneRole.ONCO);
        assertGeneRoleResolved(TSG_GENE, GeneRole.TSG);
        assertGeneRoleResolved(BOTH_GENE, GeneRole.BOTH);
        assertGeneRoleResolved(UNKNOWN_GENE, GeneRole.UNKNOWN);
    }

    @NotNull
    private static ImmutableGene geneWith(String role) {
        return ImmutableGene.builder().id(0).createDate(TEST_DATE).updateDate(TEST_DATE).geneSymbol(GENE_NAME).geneRole(role).build();
    }

    private static void assertGeneRoleResolved(Gene gene, GeneRole expectedRole) {
        Variant variant = variantWith(gene, null);
        assertEquals(expectedRole, CkbVariantAnnotator.resolveGeneRole(variant));
    }

    @NotNull
    private static ImmutableVariant variantWith(@NotNull Gene gene, @Nullable String proteinEffect) {
        return ImmutableVariant.builder()
                .id(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .fullName("variant")
                .variant("variant")
                .isHotspot(false)
                .gene(gene).proteinEffect(proteinEffect).associatedWithDrugResistance("Y")
                .build();
    }
}
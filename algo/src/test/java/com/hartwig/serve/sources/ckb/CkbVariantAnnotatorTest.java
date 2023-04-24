package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import com.hartwig.serve.ckb.datamodel.variant.Gene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableGene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableVariant;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbVariantAnnotatorTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2022, 5, 20);
    private static final Gene ONCO_GENE = geneWith("oncogene");

    private static final Gene TSG_GENE = geneWith("tumor suppressor");
    private static final Gene BOTH_GENE = geneWith("both");
    private static final Gene UNKNOWN_GENE = geneWith("na");
    private static final String GENE_NAME = "gene";

    @Test
    public void shouldAnnotateKnownGenesWithRoleFromVariant() {
        verifyKnownGeneAnnotatedWith(ONCO_GENE, GeneRole.ONCO);
        verifyKnownGeneAnnotatedWith(TSG_GENE, GeneRole.TSG);
        verifyKnownGeneAnnotatedWith(BOTH_GENE, GeneRole.BOTH);
        verifyKnownGeneAnnotatedWith(UNKNOWN_GENE, GeneRole.UNKNOWN);
    }

    @NotNull
    private static ImmutableGene geneWith(String role) {
        return ImmutableGene.builder().id(0).createDate(TEST_DATE).updateDate(TEST_DATE).geneSymbol(GENE_NAME).geneRole(role).build();
    }

    private static void verifyKnownGeneAnnotatedWith(Gene gene, GeneRole expectedRole) {
        ExtractionResult result = CkbVariantAnnotator.annotate(ImmutableExtractionResult.builder()
                .refGenomeVersion(RefGenome.V37)
                .addKnownGenes(ImmutableKnownGene.builder().gene(GENE_NAME).geneRole(GeneRole.UNKNOWN).build())
                .build(), variantWith(gene));
        assertEquals(result.knownGenes().size(), 1);
        KnownGene annotated = result.knownGenes().iterator().next();
        assertEquals(annotated.gene(), GENE_NAME);
        assertEquals(annotated.geneRole(), expectedRole);
    }

    @NotNull
    private static ImmutableVariant variantWith(Gene gene) {
        return ImmutableVariant.builder()
                .id(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .fullName("variant")
                .variant("variant")
                .isHotspot(false)
                .gene(gene)
                .build();
    }

}
package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.extraction.ExtractionResult;

import org.junit.Test;

public class HartwigGeneExtractorTest {

    private static final String GENE = "gene";

    @Test
    public void shouldExtractGenesFromEntries() {
        HartwigGeneExtractor victim = new HartwigGeneExtractor(Knowledgebase.HARTWIG_GENE_CURATED);
        ExtractionResult result = victim.extract(List.of(ImmutableHartwigGeneEntry.builder().gene(GENE).geneRole("onco").build()));
        assertEquals(result.knownGenes().size(), 1);
        KnownGene knownGene = result.knownGenes().iterator().next();
        assertEquals(GENE, knownGene.gene());
        assertEquals(GeneRole.ONCO, knownGene.geneRole());
        assertEquals(1, knownGene.sources().size());
        assertEquals(Knowledgebase.HARTWIG_GENE_CURATED, knownGene.sources().iterator().next());
    }
}
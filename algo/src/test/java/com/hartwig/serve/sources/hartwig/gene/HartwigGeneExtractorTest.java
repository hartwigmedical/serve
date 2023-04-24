package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.*;

import java.util.List;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.extraction.ExtractionResult;

import org.junit.Test;

public class HartwigGeneExtractorTest {

    private static final String GENE = "gene";

    @Test
    public void shouldExtractGenesFromEntries(){
        HartwigGeneExtractor victim = new HartwigGeneExtractor(Knowledgebase.HARTWIG_GENE_CURATED);
        ExtractionResult result = victim.extract(List.of(ImmutableHartwigGeneEntry.builder().gene(GENE).build()));
        assertEquals(result.knownGenes().size(), 1);
        KnownGene knownGene = result.knownGenes().iterator().next();
        assertEquals(knownGene.gene(), GENE);
        assertEquals(knownGene.geneRole(), GeneRole.UNKNOWN);
        assertEquals(knownGene.sources().size(), 1);
        assertEquals(knownGene.sources().iterator().next(), Knowledgebase.HARTWIG_GENE_CURATED);
    }

}
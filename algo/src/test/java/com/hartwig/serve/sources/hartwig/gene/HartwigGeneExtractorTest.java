package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.gene.KnownGene;
import com.hartwig.serve.extraction.ExtractionResult;

import org.junit.Test;

public class HartwigGeneExtractorTest {

    private static final String GENE = "gene";
    private static final GeneRole GENE_ROLE = GeneRole.ONCO;

    @Test
    public void shouldExtractGenesFromEntries() {
        HartwigGeneExtractor extractor = new HartwigGeneExtractor(Knowledgebase.HARTWIG_GENE_CURATED);
        ExtractionResult result = extractor.extract(List.of(ImmutableHartwigGeneEntry.builder().gene(GENE).geneRole(GENE_ROLE).build()));
        assertEquals(result.knownEvents().genes().size(), 1);

        KnownGene knownGene = result.knownEvents().genes().iterator().next();
        assertEquals(GENE, knownGene.gene());
        assertEquals(GeneRole.ONCO, knownGene.geneRole());
        assertEquals(1, knownGene.sources().size());
        assertEquals(Knowledgebase.HARTWIG_GENE_CURATED, knownGene.sources().iterator().next());
    }
}
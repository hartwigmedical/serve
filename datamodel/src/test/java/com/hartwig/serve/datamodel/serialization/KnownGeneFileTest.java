package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.junit.Test;

public class KnownGeneFileTest {

    private static final String KNOWN_GENE_TSV = Resources.getResource("known/KnownGenes.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownGene> genes = KnownGenesFile.read(KNOWN_GENE_TSV);

        assertEquals(genes.size(), 1);
        KnownGene knownGene = genes.get(0);
        assertEquals(knownGene.gene(), "NRAS");
        assertEquals(knownGene.geneRole(), GeneRole.ONCO);
        assertEquals(knownGene.sources(), Set.of(Knowledgebase.VICC_CIVIC));

        Map<String, Integer> fields = SerializationUtil.createFields(KnownGenesFile.header(), KnownGenesFile.FIELD_DELIMITER);
        List<KnownGene> regeneratedGenes = KnownGenesFile.fromLines(KnownGenesFile.toLines(genes), fields);

        assertEquals(genes, regeneratedGenes);
    }
}
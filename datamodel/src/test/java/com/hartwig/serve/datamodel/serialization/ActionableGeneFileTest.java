package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableGeneFileTest {

    private static final String ACTIONABLE_GENE_TSV = Resources.getResource("actionable/ActionableGenes.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableGene> genes = ActionableGeneFile.read(ACTIONABLE_GENE_TSV);

        assertActionableGenes(genes);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableGeneFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableGene> regeneratedGenes = ActionableGeneFile.fromLines(ActionableGeneFile.toLines(genes), fields);

        assertEquals(genes, regeneratedGenes);
    }

    private static void assertActionableGenes(@NotNull List<ActionableGene> genes) {
        assertEquals(7, genes.size());

        // TODO Implement (see ActionableFusionFileTest)
    }
}
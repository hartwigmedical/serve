package com.hartwig.serve.datamodel.gene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class ActionableGeneFileTest {

    private static final String ACTIONABLE_GENE_TSV = Resources.getResource("serve/ActionableGenes.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableGene> actionableGenes = ActionableGeneFile.read(ACTIONABLE_GENE_TSV);

        assertEquals(7, actionableGenes.size());

        List<String> lines = ActionableGeneFile.toLines(actionableGenes);
        List<ActionableGene> regeneratedGenes = ActionableGeneFile.fromLines(lines);
        List<String> regeneratedLines = ActionableGeneFile.toLines(regeneratedGenes);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}
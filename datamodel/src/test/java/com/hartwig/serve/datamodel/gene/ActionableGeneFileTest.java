package com.hartwig.serve.datamodel.gene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.common.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.DatamodelTestFactory;

import org.junit.Test;

public class ActionableGeneFileTest {

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String actionableGeneTsv =
                ActionableGeneFile.actionableGeneTsvPath(DatamodelTestFactory.TEST_SERVE_DIR, RefGenomeVersion.V37);
        List<ActionableGene> actionableGenes = ActionableGeneFile.read(actionableGeneTsv);

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
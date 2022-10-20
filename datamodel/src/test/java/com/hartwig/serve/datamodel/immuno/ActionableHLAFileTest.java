package com.hartwig.serve.datamodel.immuno;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.common.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.DatamodelTestFactory;

import org.junit.Test;

public class ActionableHLAFileTest {

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String actionableHlATsv = ActionableHLAFile.actionableHLATsvPath(DatamodelTestFactory.TEST_SERVE_DIR, RefGenomeVersion.V37);
        List<ActionableHLA> actionableHLAs = ActionableHLAFile.read(actionableHlATsv);

        assertEquals(1, actionableHLAs.size());

        List<String> lines = ActionableHLAFile.toLines(actionableHLAs);
        List<ActionableHLA> regeneratedHLAs = ActionableHLAFile.fromLines(lines);
        List<String> regeneratedLines = ActionableHLAFile.toLines(regeneratedHLAs);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}
package com.hartwig.serve.datamodel.immuno;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class ActionableHLAFileTest {

    private static final String ACTIONABLE_HLA_TSV = Resources.getResource("serve/ActionableHLA.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableHLA> actionableHLAs = ActionableHLAFile.read(ACTIONABLE_HLA_TSV);

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
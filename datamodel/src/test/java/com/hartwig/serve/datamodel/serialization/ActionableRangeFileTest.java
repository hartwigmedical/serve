package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.range.ActionableRange;

import org.junit.Test;

public class ActionableRangeFileTest {

    private static final String ACTIONABLE_RANGE_TSV = Resources.getResource("actionability/ActionableRanges.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableRange> actionableRanges = ActionableRangeFile.read(ACTIONABLE_RANGE_TSV);

        assertEquals(2, actionableRanges.size());

        List<String> lines = ActionableRangeFile.toLines(actionableRanges);
        List<ActionableRange> regeneratedRanges = ActionableRangeFile.fromLines(lines);
        List<String> regeneratedLines = ActionableRangeFile.toLines(regeneratedRanges);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}
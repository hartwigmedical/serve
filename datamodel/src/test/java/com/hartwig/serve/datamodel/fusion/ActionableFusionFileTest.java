package com.hartwig.serve.datamodel.fusion;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class ActionableFusionFileTest {

    private static final String ACTIONABLE_FUSION_TSV = Resources.getResource("actionability/ActionableFusions.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableFusion> actionableFusions = ActionableFusionFile.read(ACTIONABLE_FUSION_TSV);

        assertEquals(3, actionableFusions.size());

        List<String> lines = ActionableFusionFile.toLines(actionableFusions);
        List<ActionableFusion> regeneratedFusions = ActionableFusionFile.fromLines(lines);
        List<String> regeneratedLines = ActionableFusionFile.toLines(regeneratedFusions);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}
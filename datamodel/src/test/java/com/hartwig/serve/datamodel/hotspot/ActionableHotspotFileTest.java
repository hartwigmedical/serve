package com.hartwig.serve.datamodel.hotspot;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class ActionableHotspotFileTest {

    private static final String ACTIONABLE_HOTSPOT_TSV = Resources.getResource("serve/ActionableHotspots.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableHotspot> actionableHotspots = ActionableHotspotFile.read(ACTIONABLE_HOTSPOT_TSV);

        assertEquals(2, actionableHotspots.size());

        List<String> lines = ActionableHotspotFile.toLines(actionableHotspots);
        List<ActionableHotspot> regeneratedHotspots = ActionableHotspotFile.fromLines(lines);
        List<String> regeneratedLines = ActionableHotspotFile.toLines(regeneratedHotspots);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}
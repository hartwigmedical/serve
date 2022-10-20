package com.hartwig.serve.datamodel.hotspot;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.common.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.DatamodelTestFactory;

import org.junit.Test;

public class ActionableHotspotFileTest {

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String actionableHotspotTsv =
                ActionableHotspotFile.actionableHotspotTsvPath(DatamodelTestFactory.TEST_SERVE_DIR, RefGenomeVersion.V37);
        List<ActionableHotspot> actionableHotspots = ActionableHotspotFile.read(actionableHotspotTsv);

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
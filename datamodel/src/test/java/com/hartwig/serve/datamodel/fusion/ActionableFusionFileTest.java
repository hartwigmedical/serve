package com.hartwig.serve.datamodel.fusion;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;

import org.junit.Test;

public class ActionableFusionFileTest {

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String actionableFusionTsv =
                ActionableFusionFile.actionableFusionTsvPath(DatamodelTestFactory.TEST_SERVE_DIR, RefGenomeVersion.V37);
        List<ActionableFusion> actionableFusions = ActionableFusionFile.read(actionableFusionTsv);

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
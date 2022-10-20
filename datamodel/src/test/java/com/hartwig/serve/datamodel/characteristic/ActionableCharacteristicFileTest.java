package com.hartwig.serve.datamodel.characteristic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;

import org.junit.Test;

public class ActionableCharacteristicFileTest {

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String actionableCharacteristicTsv =
                ActionableCharacteristicFile.actionableCharacteristicTsvPath(DatamodelTestFactory.TEST_SERVE_DIR,
                        RefGenomeVersion.V37);
        List<ActionableCharacteristic> actionableCharacteristics = ActionableCharacteristicFile.read(actionableCharacteristicTsv);

        assertEquals(2, actionableCharacteristics.size());

        List<String> lines = ActionableCharacteristicFile.toLines(actionableCharacteristics);
        List<ActionableCharacteristic> regeneratedCharacteristics = ActionableCharacteristicFile.fromLines(lines);
        List<String> regeneratedLines = ActionableCharacteristicFile.toLines(regeneratedCharacteristics);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}

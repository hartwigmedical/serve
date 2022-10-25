package com.hartwig.serve.datamodel.characteristic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class ActionableCharacteristicFileTest {

    private static final String ACTIONABLE_CHARACTERISTIC_TSV = Resources.getResource("actionability/ActionableCharacteristics.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableCharacteristic> actionableCharacteristics = ActionableCharacteristicFile.read(ACTIONABLE_CHARACTERISTIC_TSV);

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

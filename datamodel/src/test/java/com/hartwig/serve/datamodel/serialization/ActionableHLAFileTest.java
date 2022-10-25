package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableHLAFileTest {

    private static final String ACTIONABLE_HLA_TSV = Resources.getResource("actionability/ActionableHLA.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableHLA> hlas = ActionableHLAFile.read(ACTIONABLE_HLA_TSV);

        assertActionableHLAs(hlas);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableHLAFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableHLA> regeneratedHLAs = ActionableHLAFile.fromLines(ActionableHLAFile.toLines(hlas), fields);

        assertEquals(hlas, regeneratedHLAs);
    }

    private static void assertActionableHLAs(@NotNull List<ActionableHLA> hlas) {
        assertEquals(1, hlas.size());

        // TODO Implement (see ActionableFusionFileTest)
    }
}
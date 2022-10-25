package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableHotspotFileTest {

    private static final String ACTIONABLE_HOTSPOT_TSV = Resources.getResource("actionability/ActionableHotspots.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableHotspot> hotspots = ActionableHotspotFile.read(ACTIONABLE_HOTSPOT_TSV);

        assertActionableHotspots(hotspots);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableHotspotFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableHotspot> regeneratedHotspots = ActionableHotspotFile.fromLines(ActionableHotspotFile.toLines(hotspots), fields);

        assertEquals(hotspots, regeneratedHotspots);
    }

    private static void assertActionableHotspots(@NotNull List<ActionableHotspot> hotspots) {
        assertEquals(2, hotspots.size());

        // TODO Implement, see ActionableFusionFileTest
    }
}
package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownHotspotFileTest {

    private static final String KNOWN_HOTSPOT_TSV = Resources.getResource("known/KnownHotspots.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownHotspot> hotspots = KnownHotspotFile.read(KNOWN_HOTSPOT_TSV);

        assertKnownCodons(hotspots);

        Map<String, Integer> fields = SerializationUtil.createFields(KnownHotspotFile.header(), KnownCodonFile.FIELD_DELIMITER);
        List<KnownHotspot> regeneratedHotspots = KnownHotspotFile.fromLines(KnownHotspotFile.toLines(hotspots), fields);

        assertEquals(hotspots, regeneratedHotspots);
    }

    private static void assertKnownCodons(@NotNull List<KnownHotspot> hotspots) {
        assertEquals(2, hotspots.size());

        // TODO Implement (See ActionableFusionFileTest)
    }

}
package com.hartwig.serve.extraction.hotspot;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;

import org.junit.Test;

public class KnownHotspotVCFTest {

    private static final String KNOWN_HOTSPOT_VCF = Resources.getResource("known_hotspots/known_hotspots_example.vcf").getPath();

    @Test
    public void canReadKnownHotspotVCF() throws IOException {
        List<KnownHotspot> hotspots = KnownHotspotVCF.read(KNOWN_HOTSPOT_VCF);

        assertEquals(2, hotspots.size());

        // TODO Implement completely (see ActionableFusionFileTest)
    }
}
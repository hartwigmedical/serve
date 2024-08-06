package com.hartwig.serve.sources.ckb.region;

import static com.hartwig.serve.sources.ckb.CkbTestFactory.createLocation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbRegionFileTest {

    @Test
    public void shouldLoadFileWithAndWithoutStates() throws Exception {
        Set<CkbRegion> regions = CkbRegionFile.read(Resources.getResource("ckb_region/ckb_regions.tsv").getPath());
        CkbRegion netherlands = regions.stream().filter(r -> r.country().equals("netherlands")).findFirst().orElseThrow();
        CkbRegion us = regions.stream().filter(r -> r.country().equals("united states")).findFirst().orElseThrow();

        assertTrue(netherlands.includes(createLocation("Netherlands", null, "Rotterdam", "Noord Holland")));
        assertTrue(netherlands.includes(createLocation("Netherlands", null, "Rotterdam", null)));

        assertTrue(us.includes(createLocation("United States", null, "New York", "Maine")));
        assertFalse(us.includes(createLocation("United States", null, "New York", "California")));
        assertFalse(us.includes(createLocation("United States", null, "New York", null)));

    }

}
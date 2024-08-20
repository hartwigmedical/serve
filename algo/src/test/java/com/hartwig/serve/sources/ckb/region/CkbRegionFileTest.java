package com.hartwig.serve.sources.ckb.region;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.io.Resources;
import com.hartwig.serve.sources.ckb.CkbTestFactory;

import org.junit.Test;

public class CkbRegionFileTest {

    @Test
    public void shouldLoadFileWithAndWithoutStates() throws Exception {
        Set<CkbRegion> regions = CkbRegionFile.read(Resources.getResource("ckb_region/ckb_regions.tsv").getPath());
        CkbRegion netherlands = regions.stream().filter(r -> r.country().equals("netherlands")).findFirst().orElseThrow();
        CkbRegion us = regions.stream().filter(r -> r.country().equals("united states")).findFirst().orElseThrow();

        assertTrue(netherlands.includes(CkbTestFactory.createLocation("Netherlands", null, "Rotterdam", "Noord Holland", null)));
        assertTrue(netherlands.includes(CkbTestFactory.createLocation("Netherlands", null, "Rotterdam", null, null)));

        assertTrue(us.includes(CkbTestFactory.createLocation("United States", null, "Augusta", "Maine", null)));
        assertFalse(us.includes(CkbTestFactory.createLocation("United States", null, "LA", "California", null)));
        assertFalse(us.includes(CkbTestFactory.createLocation("United States", null, "New York", null, null)));
    }
}
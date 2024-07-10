package com.hartwig.serve.sources.ckb.region;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.io.Resources;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbRegionFileTest {

    @Test
    public void shouldLoadFileWithAndWithoutStates() throws Exception {
        Set<CkbRegion> regions = CkbRegionFile.read(Resources.getResource("ckb_region/ckb_regions.tsv").getPath());
        CkbRegion netherlands = regions.stream().filter(r -> r.country().equals("netherlands")).findFirst().orElseThrow();
        CkbRegion us = regions.stream().filter(r -> r.country().equals("united states")).findFirst().orElseThrow();

        assertTrue(netherlands.includes(location("Netherlands", "Nord Holland")));
        assertTrue(netherlands.includes(location("Netherlands", null)));

        assertTrue(us.includes(location("United States", "Maine")));
        assertFalse(us.includes(location("United States", "California")));
        assertFalse(us.includes(location("United States", null)));

    }

    @NotNull
    private static ImmutableLocation location(@NotNull String country, @Nullable String state) {
        return ImmutableLocation.builder().country(country).state(state).city("city").nctId("nct").build();
    }
}
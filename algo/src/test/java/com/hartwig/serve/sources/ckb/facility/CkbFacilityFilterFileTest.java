package com.hartwig.serve.sources.ckb.facility;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityFilterFileTest {

    private static final String TEST_CKB_FACILITY_FILTER_FILE = Resources.getResource("ckb_facility/ckb_facility_filter.tsv").getPath();

    @Test
    public void canReadCkbFacilityFilterTsv() throws IOException {
        List<CkbFacilityFilterEntry> facilityFilterEntries = CkbFacilityFilterFile.read(TEST_CKB_FACILITY_FILTER_FILE);
        assertEquals(1, facilityFilterEntries.size());
    }
}
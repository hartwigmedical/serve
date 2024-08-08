package com.hartwig.serve.sources.ckb.facility;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityNameFileTest {

    private static final String TEST_CKB_FACILITY_NAME_FILE = Resources.getResource("ckb_facility/ckb_facility_name.tsv").getPath();

    @Test
    public void canReadCkbFacilityNameTsv() throws IOException {
        List<CkbFacilityNameEntry> facilityNameEntries = CkbFacilityNameFile.read(TEST_CKB_FACILITY_NAME_FILE);
        assertEquals(1, facilityNameEntries.size());
    }
}
package com.hartwig.serve.sources.ckb.facility;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityCityFileTest {

    private static final String TEST_CKB_FACILITY_CITY_FILE = Resources.getResource("ckb_facility/ckb_facility_city.tsv").getPath();

    @Test
    public void canReadCkbFacilityCityTsv() throws IOException {
        List<CkbFacilityCityEntry> facilityCityEntries = CkbFacilityCityFile.read(TEST_CKB_FACILITY_CITY_FILE);
        assertEquals(1, facilityCityEntries.size());
    }
}
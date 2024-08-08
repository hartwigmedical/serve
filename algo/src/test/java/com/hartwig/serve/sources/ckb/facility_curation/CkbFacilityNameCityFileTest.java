package com.hartwig.serve.sources.ckb.facility_curation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityNameCityFileTest {

    private static final String TEST_CKB_FACILITY_CURATION_CITY_FILE =
            Resources.getResource("ckb_facility/ckb_facility_city.tsv").getPath();

    @Test
    public void canReadCkbFacilityCityTsv() throws IOException {
        List<CkbFacilityCurationCityEntry> facilityCurationCityEntries =
                CkbFacilityCurationCityFile.read(TEST_CKB_FACILITY_CURATION_CITY_FILE);
        assertEquals(1, facilityCurationCityEntries.size());
    }
}
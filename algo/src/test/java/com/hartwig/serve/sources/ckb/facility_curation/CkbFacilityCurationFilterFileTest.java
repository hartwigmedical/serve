package com.hartwig.serve.sources.ckb.facility_curation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityCurationFilterFileTest {

    private static final String TEST_CKB_FACILITY_CURATION_FILTER_FILE =
            Resources.getResource("ckb_facility_curation/ckb_facility_filter.tsv").getPath();

    @Test
    public void canReadCkbFacilityFilterTsv() throws IOException {
        List<CkbFacilityCurationFilterEntry> facilityCurationFilterEntries =
                CkbFacilityCurationFilterFile.read(TEST_CKB_FACILITY_CURATION_FILTER_FILE);
        //assertEquals(1, facilityCurationFilterEntries.size());
    }
}
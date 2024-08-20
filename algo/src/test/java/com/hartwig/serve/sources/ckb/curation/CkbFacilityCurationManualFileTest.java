package com.hartwig.serve.sources.ckb.curation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityCurationManualFileTest {

    private static final String TEST_CKB_FACILITY_CURATION_MANUAL_FILE =
            Resources.getResource("ckb_curation/ckb_facility_manual.tsv").getPath();

    @Test
    public void canReadCkbFacilityManualTsv() throws IOException {
        List<CkbFacilityCurationManualEntry> facilityCurationManualEntries =
                CkbFacilityCurationManualFile.read(TEST_CKB_FACILITY_CURATION_MANUAL_FILE);
        assertEquals(1, facilityCurationManualEntries.size());
    }
}
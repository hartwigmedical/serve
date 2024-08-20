package com.hartwig.serve.sources.ckb.curation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityCurationNameFileTest {

    private static final String TEST_CKB_FACILITY_CURATION_NAME_FILE =
            Resources.getResource("ckb_curation/ckb_facility_name.tsv").getPath();

    @Test
    public void canReadCkbFacilityNameTsv() throws IOException {
        List<CkbFacilityCurationNameEntry> facilityCurationNameEntries =
                CkbFacilityCurationNameFile.read(TEST_CKB_FACILITY_CURATION_NAME_FILE);
        assertEquals(1, facilityCurationNameEntries.size());
    }
}
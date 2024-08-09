package com.hartwig.serve.sources.ckb.facility_curation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityCurationZipFileTest {

    private static final String TEST_CKB_FACILITY_CURATION_ZIP_FILE =
            Resources.getResource("ckb_facility_curation/ckb_facility_zip.tsv").getPath();

    @Test
    public void canReadCkbFacilityZipTsv() throws IOException {
        List<CkbFacilityCurationZipEntry> facilityCurationZipEntries = CkbFacilityCurationZipFile.read(TEST_CKB_FACILITY_CURATION_ZIP_FILE);
        assertEquals(1, facilityCurationZipEntries.size());
    }
}

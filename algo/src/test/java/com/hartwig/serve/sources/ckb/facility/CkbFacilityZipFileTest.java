package com.hartwig.serve.sources.ckb.facility;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbFacilityZipFileTest {

    private static final String TEST_CKB_FACILITY_ZIP_FILE = Resources.getResource("ckb_facility/ckb_facility_zip.tsv").getPath();

    @Test
    public void canReadCkbFacilityZipTsv() throws IOException {
        List<CkbFacilityZipEntry> facilityZipEntries = CkbFacilityZipFile.read(TEST_CKB_FACILITY_ZIP_FILE);
        assertEquals(1, facilityZipEntries.size());
    }
}

package com.hartwig.serve.sources.ckb.facility_curation;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.junit.Test;

public class TestTest {

    private static final String TEST_CKB_FACILITY_TEST_FILE = Resources.getResource("ckb_facility_curation/test.tsv").getPath();

    private static final String TEST_CKB_FACILITY_CURATION_NAME_FILE =
            Resources.getResource("ckb_facility_curation/ckb_facility_name.tsv").getPath();

    private static final String TEST_CKB_FACILITY_CURATION_FILTER_FILE =
            Resources.getResource("ckb_facility_curation/ckb_facility_filter.tsv").getPath();

    private static final String TEST_CKB_FACILITY_CURATION_ZIP_FILE =
            Resources.getResource("ckb_facility_curation/ckb_facility_zip.tsv").getPath();

    @Test
    public void canReadCkbFacilityCityTsv() throws IOException {
        List<Location> locations = TestFile.read(TEST_CKB_FACILITY_TEST_FILE);

        List<CkbFacilityCurationNameEntry> facilityCurationNameEntries =
                CkbFacilityCurationNameFile.read(TEST_CKB_FACILITY_CURATION_NAME_FILE);

        List<CkbFacilityCurationFilterEntry> facilityCurationFilterEntries =
                CkbFacilityCurationFilterFile.read(TEST_CKB_FACILITY_CURATION_FILTER_FILE);

        List<CkbFacilityCurationZipEntry> facilityCurationZipEntries = CkbFacilityCurationZipFile.read(TEST_CKB_FACILITY_CURATION_ZIP_FILE);

        CkbFacilityCurationModel ckbFacilityCurationModel =
                new CkbFacilityCurationModel(facilityCurationNameEntries, facilityCurationZipEntries, facilityCurationFilterEntries);

        for (Location location : locations) {
            System.out.println(
                    location.facility() + ";" + location.city() + ";" + location.zip() + ";" + ckbFacilityCurationModel.curateFacilityName(
                            location));
        }
    }
}

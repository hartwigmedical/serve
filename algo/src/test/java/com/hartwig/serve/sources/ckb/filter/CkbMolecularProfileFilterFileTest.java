package com.hartwig.serve.sources.ckb.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbMolecularProfileFilterFileTest {

    private static final String TEST_CKB_MOLECULAR_PROFILE_FILTER_FILE =
            Resources.getResource("ckb_filter/ckb_molecular_profile_filter.tsv").getPath();

    @Test
    public void canReadCkbMolecularProfileFilterTsv() throws IOException {
        List<CkbMolecularProfileFilterEntry> filterEntries = CkbMolecularProfileFilterFile.read(TEST_CKB_MOLECULAR_PROFILE_FILTER_FILE);
        assertEquals(2, filterEntries.size());
    }
}
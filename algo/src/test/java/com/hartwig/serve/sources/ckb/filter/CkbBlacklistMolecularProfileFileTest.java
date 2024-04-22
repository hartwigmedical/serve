package com.hartwig.serve.sources.ckb.filter;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CkbBlacklistMolecularProfileFileTest  {

    private static final String TEST_CKB_FILTER_FILE = Resources.getResource("ckb_filter/ckb_filters.tsv").getPath();

    @Test
    public void canReadCkbFilterTsv() throws IOException {
        List<CkbBlacklistMolecularProfileEntry> filterEntries = CkbBlacklistMolecularProfileFile.read(TEST_CKB_FILTER_FILE);
        assertEquals(2, filterEntries.size());
    }

}
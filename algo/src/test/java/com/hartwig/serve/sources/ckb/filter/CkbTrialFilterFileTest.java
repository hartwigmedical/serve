package com.hartwig.serve.sources.ckb.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbTrialFilterFileTest {

    private static final String TEST_CKB_TRIAL_FILTER_FILE = Resources.getResource("ckb_filter/ckb_trial_filter.tsv").getPath();

    @Test
    public void canReadCkbTrialFilterTsv() throws IOException {
        List<CkbTrialFilterEntry> filterEntries = CkbTrialFilterFile.read(TEST_CKB_TRIAL_FILTER_FILE);
        assertEquals(4, filterEntries.size());
    }
}
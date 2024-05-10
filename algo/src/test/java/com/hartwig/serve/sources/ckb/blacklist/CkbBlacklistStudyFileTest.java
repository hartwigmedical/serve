package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CkbBlacklistStudyFileTest {

    private static final String TEST_CKB_BLACKLIST_STUDY_FILE = Resources.getResource("ckb_blacklist/ckb_blacklist_trial.tsv").getPath();

    @Test
    public void canReadCkbBlacklistStudyTsv() throws IOException {
        List<CkbBlacklistStudyEntry> blacklistStudyEntries = CkbBlacklistStudyFile.read(TEST_CKB_BLACKLIST_STUDY_FILE);
        assertEquals(4, blacklistStudyEntries.size());
    }

}
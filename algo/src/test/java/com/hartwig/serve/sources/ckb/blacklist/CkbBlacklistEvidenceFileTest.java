package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CkbBlacklistEvidenceFileTest {

    private static final String TEST_CKB_BLACKLIST_EVIDENCE_FILE = Resources.getResource("ckb_blacklist/ckb_blacklist_evidence.tsv").getPath();

    @Test
    public void canReadCkbBlacklistEvidenceTsv() throws IOException {
        List<CkbBlacklistEvidenceEntry> blacklistEvidenceEntries = CkbBlacklistEvidenceFile.read(TEST_CKB_BLACKLIST_EVIDENCE_FILE);
        assertEquals(1, blacklistEvidenceEntries.size());
    }
}
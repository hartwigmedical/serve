package com.hartwig.serve.sources.ckb.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbEvidenceFilterFileTest {

    private static final String TEST_CKB_EVIDENCE_FILTER_FILE =
            Resources.getResource("ckb_filter/ckb_evidence_filter.tsv").getPath();

    @Test
    public void canReadCkbEvidenceFilterTsv() throws IOException {
        List<CkbEvidenceFilterEntry> filterEntries = CkbEvidenceFilterFile.read(TEST_CKB_EVIDENCE_FILTER_FILE);
        assertEquals(1, filterEntries.size());
    }
}
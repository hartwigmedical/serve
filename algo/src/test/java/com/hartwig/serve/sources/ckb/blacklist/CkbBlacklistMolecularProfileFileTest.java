package com.hartwig.serve.sources.ckb.blacklist;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CkbBlacklistMolecularProfileFileTest {

    private static final String TEST_CKB_BLACKLIST_MOLECULAR_PROFILE_FILE =
            Resources.getResource("ckb_blacklist/ckb_blacklist_molecular_profile.tsv").getPath();

    @Test
    public void canReadCkbBlacklistMolecularProfileTsv() throws IOException {
        List<CkbBlacklistMolecularProfileEntry> blacklistMolecularProfileEntries =
                CkbBlacklistMolecularProfileFile.read(TEST_CKB_BLACKLIST_MOLECULAR_PROFILE_FILE);
        assertEquals(2, blacklistMolecularProfileEntries.size());
    }

}
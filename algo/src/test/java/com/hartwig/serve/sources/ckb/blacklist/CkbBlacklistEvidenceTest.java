package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.CkbTestFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CkbBlacklistEvidenceTest {

    @NotNull
    public static CkbBlacklistEvidence createCkbBlacklistEvidence() {
        return new CkbBlacklistEvidence(Lists.newArrayList());
    }
}
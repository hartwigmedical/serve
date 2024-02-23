package com.hartwig.serve.sources.ckb.blacklist;


import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;


public class CkbBlacklistStudyTest {


    @NotNull
    public static CkbBlacklistStudy createCkbBlacklistStudy() {
        return new CkbBlacklistStudy(Lists.newArrayList());
    }

}
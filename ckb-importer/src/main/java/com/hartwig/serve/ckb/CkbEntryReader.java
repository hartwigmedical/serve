package com.hartwig.serve.ckb;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.json.CkbJsonDatabase;
import com.hartwig.serve.ckb.json.CkbJsonReader;

import org.jetbrains.annotations.NotNull;

public final class CkbEntryReader {

    private CkbEntryReader() {
    }

    @NotNull
    public static List<CkbEntry> read(@NotNull String ckbDir) throws IOException {
        CkbJsonDatabase ckbJsonDatabase = CkbJsonReader.read(ckbDir);
        return JsonDatabaseToCkbEntryConverter.convert(ckbJsonDatabase);
    }
}

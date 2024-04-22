package com.hartwig.serve.sources.ckb.blacklist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public final class CkbBlacklistMolecularProfileFile {

    private static final String FIELD_DELIMITER = "\t";

    private CkbBlacklistMolecularProfileFile() {
    }

    @NotNull
    public static List<CkbBlacklistMolecularProfileEntry> read(@NotNull String ckbFilterTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFilterTsv).toPath());
        // Skip header
        return fromLines(lines.subList(1, lines.size()));
    }

    @NotNull
    private static List<CkbBlacklistMolecularProfileEntry> fromLines(@NotNull List<String> lines) {
        List<CkbBlacklistMolecularProfileEntry> filterEntries = Lists.newArrayList();
        for (String line : lines) {
            filterEntries.add(fromLine(line));
        }
        return filterEntries;
    }

    @NotNull
    private static CkbBlacklistMolecularProfileEntry fromLine(@NotNull String line) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableCkbBlacklistMolecularProfileEntry.builder()
                .type(CkbBlacklistMolecularProfileType.valueOf(values[0]))
                .value(values[1])
                .build();
    }
}

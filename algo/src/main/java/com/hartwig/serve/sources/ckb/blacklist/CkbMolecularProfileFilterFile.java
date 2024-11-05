package com.hartwig.serve.sources.ckb.blacklist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public final class CkbMolecularProfileFilterFile {

    private static final String FIELD_DELIMITER = "\t";

    private CkbMolecularProfileFilterFile() {
    }

    @NotNull
    public static List<CkbMolecularProfileFilterEntry> read(@NotNull String ckbMolecularProfileFilterTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbMolecularProfileFilterTsv).toPath());

        // Skip header
        return fromLines(lines.subList(1, lines.size()));
    }

    @NotNull
    private static List<CkbMolecularProfileFilterEntry> fromLines(@NotNull List<String> lines) {
        List<CkbMolecularProfileFilterEntry> entries = Lists.newArrayList();
        for (String line : lines) {
            entries.add(fromLine(line));
        }
        return entries;
    }

    @NotNull
    private static CkbMolecularProfileFilterEntry fromLine(@NotNull String line) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableCkbMolecularProfileFilterEntry.builder()
                .type(CkbMolecularProfileFilterType.valueOf(values[0]))
                .value(values[1])
                .build();
    }
}

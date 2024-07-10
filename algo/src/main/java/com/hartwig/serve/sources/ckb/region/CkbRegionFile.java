package com.hartwig.serve.sources.ckb.region;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public class CkbRegionFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static Set<CkbRegion> read(@NotNull String ckbRegionTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbRegionTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static Set<CkbRegion> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        Set<CkbRegion> regions = new HashSet<>();
        for (String line : lines) {
            regions.add(fromLine(line, fields));
        }
        return regions;
    }

    @NotNull
    private static CkbRegion fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        return CkbRegion.of(values[fields.get("country")],
                Arrays.stream(values[fields.get("states")].split(";")).collect(Collectors.toSet()));
    }
}

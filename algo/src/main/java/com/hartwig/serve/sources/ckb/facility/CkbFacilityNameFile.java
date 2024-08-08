package com.hartwig.serve.sources.ckb.facility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class CkbFacilityNameFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityNameEntry> read(@NotNull String ckbFacilityNameTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityNameTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityNameEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityNameEntry> names = Lists.newArrayList();
        for (String line : lines) {
            names.add(fromLine(line, fields));
        }
        return names;
    }

    @NotNull
    private static CkbFacilityNameEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String name = values[fields.get("facility name")];
        String facilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityNameEntry.builder().facilityName(name).curatedFacilityName(facilityName).build();
    }
}


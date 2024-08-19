package com.hartwig.serve.sources.ckb.curation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public final class CkbFacilityCurationNameFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityCurationNameEntry> read(@NotNull String ckbFacilityCurationNameTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityCurationNameTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityCurationNameEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityCurationNameEntry> names = Lists.newArrayList();
        for (String line : lines) {
            names.add(fromLine(line, fields));
        }
        return names;
    }

    @NotNull
    private static CkbFacilityCurationNameEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String name = values[fields.get("facility name")];
        String city = values[fields.get("city")];
        String facilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityCurationNameEntry.builder().facilityName(name).city(city).curatedFacilityName(facilityName).build();
    }
}


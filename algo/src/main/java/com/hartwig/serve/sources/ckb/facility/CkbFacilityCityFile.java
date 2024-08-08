package com.hartwig.serve.sources.ckb.facility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class CkbFacilityCityFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityCityEntry> read(@NotNull String ckbFacilityCityTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityCityTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityCityEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityCityEntry> cities = Lists.newArrayList();
        for (String line : lines) {
            cities.add(fromLine(line, fields));
        }
        return cities;
    }

    @NotNull
    private static CkbFacilityCityEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String city = values[fields.get("city")];
        String facilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityCityEntry.builder().city(city).curatedFacilityName(facilityName).build();
    }
}

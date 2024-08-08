package com.hartwig.serve.sources.ckb.facility_curation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class CkbFacilityCurationCityFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityCurationCityEntry> read(@NotNull String ckbFacilityCityTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityCityTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityCurationCityEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityCurationCityEntry> cities = Lists.newArrayList();
        for (String line : lines) {
            cities.add(fromLine(line, fields));
        }
        return cities;
    }

    @NotNull
    private static CkbFacilityCurationCityEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String city = values[fields.get("city")];
        String facilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityCurationCityEntry.builder().city(city).curatedFacilityName(facilityName).build();
    }
}

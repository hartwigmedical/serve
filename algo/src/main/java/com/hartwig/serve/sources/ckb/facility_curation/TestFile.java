package com.hartwig.serve.sources.ckb.facility_curation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class TestFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<Location> read(@NotNull String ckbFacilityCurationCityTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityCurationCityTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<Location> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<Location> cities = Lists.newArrayList();
        for (String line : lines) {
            cities.add(fromLine(line, fields));
        }
        return cities;
    }

    @NotNull
    private static Location fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String facility = values[fields.get("facility")];
        String city = values[fields.get("city")];
        String zip = values[fields.get("zip")];

        return ImmutableLocation.builder().nctId("").city(city).zip(zip).country("Netherlands").facility(facility).build();
    }
}

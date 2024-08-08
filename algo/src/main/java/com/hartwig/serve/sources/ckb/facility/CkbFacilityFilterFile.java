package com.hartwig.serve.sources.ckb.facility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class CkbFacilityFilterFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityFilterEntry> read(@NotNull String ckbFacilityFilterTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityFilterTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityFilterEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityFilterEntry> filters = Lists.newArrayList();
        for (String line : lines) {
            filters.add(fromLine(line, fields));
        }
        return filters;
    }

    @NotNull
    private static CkbFacilityFilterEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String facilityName = values[fields.get("facility name")];
        String city = values[fields.get("city")];
        String zip = values[fields.get("zip")];
        String curatedFacilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityFilterEntry.builder()
                .facilityName(facilityName)
                .city(city)
                .zip(zip)
                .curatedFacilityName(curatedFacilityName)
                .build();
    }
}

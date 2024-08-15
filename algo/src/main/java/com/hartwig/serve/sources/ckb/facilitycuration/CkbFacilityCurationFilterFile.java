package com.hartwig.serve.sources.ckb.facilitycuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public final class CkbFacilityCurationFilterFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityCurationFilterEntry> read(@NotNull String ckbFacilityCurationFilterTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityCurationFilterTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityCurationFilterEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityCurationFilterEntry> filters = Lists.newArrayList();
        for (String line : lines) {
            filters.add(fromLine(line, fields));
        }
        return filters;
    }

    @NotNull
    private static CkbFacilityCurationFilterEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String facilityName = values[fields.get("facility name")];
        String city = values[fields.get("city")];
        String zip = values[fields.get("zip")];
        String curatedFacilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityCurationFilterEntry.builder()
                .facilityName(facilityName)
                .city(city)
                .zip(zip)
                .curatedFacilityName(curatedFacilityName)
                .build();
    }
}

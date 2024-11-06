package com.hartwig.serve.sources.ckb.curation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.common.serialization.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public final class CkbFacilityCurationManualFile {

    private static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityCurationManualEntry> read(@NotNull String ckbFacilityCurationManualTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbFacilityCurationManualTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityCurationManualEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityCurationManualEntry> manualCurations = Lists.newArrayList();
        for (String line : lines) {
            manualCurations.add(fromLine(line, fields));
        }
        return manualCurations;
    }

    @NotNull
    private static CkbFacilityCurationManualEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String facilityName = values[fields.get("facility name")];
        String city = values[fields.get("city")];
        String zip = values[fields.get("zip")];
        String curatedFacilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityCurationManualEntry.builder()
                .facilityName(facilityName)
                .city(city)
                .zip(zip)
                .curatedFacilityName(curatedFacilityName)
                .build();
    }
}

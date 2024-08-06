package com.hartwig.serve.sources.ckb.facility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class CkbFacilityZipFile {

    public static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static List<CkbFacilityZipEntry> read(@NotNull String ckbRegionTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbRegionTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbFacilityZipEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbFacilityZipEntry> zips = Lists.newArrayList();
        for (String line : lines) {
            zips.add(fromLine(line, fields));
        }
        return zips;
    }

    @NotNull
    private static CkbFacilityZipEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        String city = values[fields.get("city")];
        String zip = values[fields.get("zip")];
        String facilityName = values[fields.get("curated facility name")];

        return ImmutableCkbFacilityZipEntry.builder().city(city).zip(zip).curatedFacilityName(facilityName).build();
    }
}

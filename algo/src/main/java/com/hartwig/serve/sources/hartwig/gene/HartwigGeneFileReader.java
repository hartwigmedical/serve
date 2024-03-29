package com.hartwig.serve.sources.hartwig.gene;

import static java.util.stream.Collectors.toList;

import static com.hartwig.serve.datamodel.serialization.util.SerializationUtil.createFields;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public final class HartwigGeneFileReader {

    private static final String DELIMITER = "\t";

    private HartwigGeneFileReader() {
    }

    @NotNull
    public static List<HartwigGeneEntry> read(@NotNull String fileName) throws IOException {
        return fromLines(Files.readAllLines(new File(fileName).toPath()));
    }

    @NotNull
    private static List<HartwigGeneEntry> fromLines(@NotNull List<String> lines) {
        Map<String, Integer> fields = createFields(lines.get(0), DELIMITER);
        int geneFieldIndex = fields.get("gene");
        return lines.stream().skip(1).map(line -> fromLine(line, geneFieldIndex)).collect(toList());
    }

    @NotNull
    private static HartwigGeneEntry fromLine(@NotNull String line, int geneFieldIndex) {
        String[] values = line.split(DELIMITER);
        return ImmutableHartwigGeneEntry.builder().gene(values[geneFieldIndex]).build();
    }
}

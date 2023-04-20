package com.hartwig.serve.sources.hartwig.hotspot;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class HartwigHotspotFileReader {

    private static final String DELIMITER = "\t";

    private HartwigHotspotFileReader() {
    }

    @NotNull
    public static List<HartwigHotspotEntry> read(@NotNull String fileName) throws IOException {
        return fromLines(Files.readAllLines(new File(fileName).toPath()));
    }

    @NotNull
    private static List<HartwigHotspotEntry> fromLines(@NotNull List<String> lines) {
        return lines.stream().skip(1).map(HartwigHotspotFileReader::fromLine).collect(toList());
    }

    @NotNull
    private static HartwigHotspotEntry fromLine(@NotNull String line) {
        String[] values = line.split(DELIMITER);

        String proteinAnnotation = values.length > 6 ? values[6] : Strings.EMPTY;

        return ImmutableHartwigHotspotEntry.builder()
                .chromosome(values[0])
                .position(Integer.parseInt(values[1]))
                .ref(values[2])
                .alt(values[3])
                .gene(values[4])
                .transcript(values[5])
                .proteinAnnotation(HartwigProteinInterpreter.interpretProtein(proteinAnnotation))
                .build();
    }
}

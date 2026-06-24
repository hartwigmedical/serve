package com.hartwig.serve.sources.hartwig.trial;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.hartwig.serve.datamodel.trial.GenderCriterium;

import org.jetbrains.annotations.NotNull;

public final class HartwigTrialReader {
    
    private static final String DELIMITER = "\t";

    private HartwigTrialReader() {
    }

    @NotNull
    public static List<HartwigTrialEntry> read(@NotNull String fileName) throws IOException {
        return fromLines(Files.readAllLines(new File(fileName).toPath()));
    }

    @NotNull
    private static List<HartwigTrialEntry> fromLines(@NotNull List<String> lines) {
        return lines.stream().skip(1).map(HartwigTrialReader::fromLine).collect(toList());
    }

    @NotNull
    private static HartwigTrialEntry fromLine(@NotNull String line) {
        String[] values = line.split(DELIMITER);

        return ImmutableHartwigTrialEntry.builder()
                .nctId(values[0])
                .title(values[1])
                .acronym(values[2].isEmpty() ? null : values[2])
                .country(values[3])
                .genderCriterium(values[4].isEmpty() ? null : GenderCriterium.valueOf(values[4]))
                .cancerType(values[5])
                .cancerTypeDoid(values[6])
                .actionableGene(values[7])
                .actionableEvent(values[8])
                .url(values[9])
                .build();
    }
}
package com.hartwig.serve.sources.hartwig.trial;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.hartwig.serve.datamodel.trial.GenderCriterium;

import org.jetbrains.annotations.NotNull;

public final class HartwigTrialReader {
    
    private static final String DELIMITER = "\t";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

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
                .date(LocalDate.parse(values[0], DATE_FORMAT))
                .nctId(values[1])
                .title(values[2])
                .acronym(values[3].isEmpty() ? null : values[3])
                .country(values[4])
                .genderCriterium(values[5].isEmpty() ? null : GenderCriterium.valueOf(values[5]))
                .cancerType(values[6])
                .cancerTypeDoid(values[7])
                .actionableGene(values[8])
                .actionableEvent(values[9])
                .url(values[10])
                .build();
    }
}
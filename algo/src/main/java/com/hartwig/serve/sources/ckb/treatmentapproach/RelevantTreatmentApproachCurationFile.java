package com.hartwig.serve.sources.ckb.treatmentapproach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.EvidenceDirection;

import org.jetbrains.annotations.NotNull;

public class RelevantTreatmentApproachCurationFile {

    private static final String FIELD_DELIMITER = "\t";

    @NotNull
    public static Map<RelevantTreatmentApproachCurationEntryKey, RelevantTreatmentApproachCurationEntry> read(
            @NotNull String treatmentApproachCurationFile) throws IOException {
        List<String> lines = Files.readAllLines(new File(treatmentApproachCurationFile).toPath());
        // Skip header
        return fromLines(lines.subList(1, lines.size()));
    }

    @NotNull
    private static Map<RelevantTreatmentApproachCurationEntryKey, RelevantTreatmentApproachCurationEntry> fromLines(
            @NotNull List<String> lines) {
        Map<RelevantTreatmentApproachCurationEntryKey, RelevantTreatmentApproachCurationEntry> mapEntry = Maps.newHashMap();
        for (String line : lines) {
            String[] values = line.split(FIELD_DELIMITER);

            RelevantTreatmentApproachCurationEntryKey entryKey = ImmutableRelevantTreatmentApproachCurationEntryKey.builder()
                    .treatment(values[1])
                    .treatmentApproach(values[2].isEmpty() ? null : values[2])
                    .event(values[3])
                    .direction(EvidenceDirection.valueOf(values[4]))
                    .build();

            mapEntry.put(entryKey,
                    ImmutableRelevantTreatmentApproachCurationEntry.builder()
                            .curationType(RelevantTreatmentApproachCurationType.valueOf(values[0]))
                            .curationKey(entryKey)
                            .curatedTreatmentApproach(values.length == 6 ? values[5] : null)
                            .build());
        }
        return mapEntry;
    }
}
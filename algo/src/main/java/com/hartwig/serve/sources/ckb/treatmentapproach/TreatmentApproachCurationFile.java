package com.hartwig.serve.sources.ckb.treatmentapproach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.EvidenceDirection;

import org.jetbrains.annotations.NotNull;

public final class TreatmentApproachCurationFile {

    private static final String FIELD_DELIMITER = "\t";

    private TreatmentApproachCurationFile() {
    }

    @NotNull
    public static Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> read(@NotNull String treatmentApproachCurationFile)
            throws IOException {
        List<String> lines = Files.readAllLines(new File(treatmentApproachCurationFile).toPath());
        // Skip header
        return fromLines(lines.subList(1, lines.size()));
    }

    @NotNull
    private static Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> fromLines(@NotNull List<String> lines) {
        Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> mapEntry = Maps.newHashMap();
        for (String line : lines) {
            String[] values = line.split(FIELD_DELIMITER);

            TreatmentApproachCurationEntryKey entryKey = ImmutableTreatmentApproachCurationEntryKey.builder()
                    .treatment(values[1])
                    .treatmentApproach(values[2].isEmpty() ? null : values[2])
                    .event(values[3])
                    .direction(EvidenceDirection.valueOf(values[4]))
                    .build();

            mapEntry.put(entryKey,
                    ImmutableTreatmentApproachCurationEntry.builder()
                            .curationType(TreatmentApproachCurationType.valueOf(values[0]))
                            .curationKey(entryKey)
                            .curatedTreatmentApproach(values.length == 6 ? values[5] : null)
                            .build());
        }
        return mapEntry;
    }
}
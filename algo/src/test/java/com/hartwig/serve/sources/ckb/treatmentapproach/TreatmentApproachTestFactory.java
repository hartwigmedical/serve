package com.hartwig.serve.sources.ckb.treatmentapproach;

import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.EvidenceDirection;

import org.jetbrains.annotations.NotNull;

public final class TreatmentApproachTestFactory {

    private TreatmentApproachTestFactory() {
    }

    @NotNull
    public static RelevantTreatmentApproachCurator createEmptyCurator() {
       return new RelevantTreatmentApproachCurator(Maps.newHashMap());
    }

    @NotNull
    public static RelevantTreatmentApproachCurator createTestCurator() {
        Map<RelevantTreatmentApproachCurationEntryKey, RelevantTreatmentApproachCurationEntry> curationEntries = Maps.newHashMap();

        curationEntries.put(createCurationKey("A", "A", "BRAF amplification", EvidenceDirection.RESPONSIVE),
                createCurationEntry(RelevantTreatmentApproachCurationType.TREATMENT_APPROACH_CURATION,
                        "A",
                        "A",
                        "BRAF amplification",
                        EvidenceDirection.RESPONSIVE,
                        "AA"));

        return new RelevantTreatmentApproachCurator(curationEntries);
    }

    @NotNull
    public static RelevantTreatmentApproachCurationEntry createCurationEntry(@NotNull RelevantTreatmentApproachCurationType type,
            @NotNull String treatment, @NotNull String treatmentApproach, @NotNull String event, @NotNull EvidenceDirection direction,
            @NotNull String curation) {
        return ImmutableRelevantTreatmentApproachCurationEntry.builder()
                .curationType(type)
                .curationKey(createCurationKey(treatment, treatmentApproach, event, direction))
                .curatedTreatmentApproach(curation)
                .build();
    }

    @NotNull
    public static RelevantTreatmentApproachCurationEntryKey createCurationKey(@NotNull String treatment, @NotNull String treatmentApproach,
            @NotNull String event, @NotNull EvidenceDirection direction) {
        return ImmutableRelevantTreatmentApproachCurationEntryKey.builder()
                .treatment(treatment)
                .treatmentApproach(treatmentApproach)
                .event(event)
                .direction(direction)
                .build();
    }
}

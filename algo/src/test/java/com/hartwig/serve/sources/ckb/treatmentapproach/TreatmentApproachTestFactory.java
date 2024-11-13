package com.hartwig.serve.sources.ckb.treatmentapproach;

import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.efficacy.EvidenceDirection;

import org.jetbrains.annotations.NotNull;

public final class TreatmentApproachTestFactory {

    private TreatmentApproachTestFactory() {
    }

    @NotNull
    public static TreatmentApproachCurator createEmptyCurator() {
        return new TreatmentApproachCurator(Maps.newHashMap());
    }

    @NotNull
    public static TreatmentApproachCurator createTestCurator() {
        Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> curationEntries = Maps.newHashMap();

        curationEntries.put(createCurationKey("A", "A", "BRAF amplification", EvidenceDirection.RESPONSIVE),
                createCurationEntry(TreatmentApproachCurationType.TREATMENT_APPROACH_CURATION,
                        "A",
                        "A",
                        "BRAF amplification",
                        EvidenceDirection.RESPONSIVE,
                        "AA"));

        return new TreatmentApproachCurator(curationEntries);
    }

    @NotNull
    public static TreatmentApproachCurationEntry createCurationEntry(@NotNull TreatmentApproachCurationType type, @NotNull String treatment,
            @NotNull String treatmentApproach, @NotNull String event, @NotNull EvidenceDirection direction, @NotNull String curation) {
        return ImmutableTreatmentApproachCurationEntry.builder()
                .curationType(type)
                .curationKey(createCurationKey(treatment, treatmentApproach, event, direction))
                .curatedTreatmentApproach(curation)
                .build();
    }

    @NotNull
    public static TreatmentApproachCurationEntryKey createCurationKey(@NotNull String treatment, @NotNull String treatmentApproach,
            @NotNull String event, @NotNull EvidenceDirection direction) {
        return ImmutableTreatmentApproachCurationEntryKey.builder()
                .treatment(treatment)
                .treatmentApproach(treatmentApproach)
                .event(event)
                .direction(direction)
                .build();
    }
}

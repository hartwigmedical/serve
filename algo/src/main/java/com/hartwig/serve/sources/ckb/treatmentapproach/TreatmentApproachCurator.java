package com.hartwig.serve.sources.ckb.treatmentapproach;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class TreatmentApproachCurator {

    private static final Logger LOGGER = LogManager.getLogger(TreatmentApproachCurator.class);

    @NotNull
    private final Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> curations;
    @NotNull
    private final Set<TreatmentApproachCurationEntryKey> usedCurations = Sets.newHashSet();

    public TreatmentApproachCurator(
            @NotNull final Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> curations) {
        this.curations = curations;
    }

    public void reportUnusedCuratedEntries() {
        int unusedCuratedEntryCount = 0;

        for (TreatmentApproachCurationEntryKey entrySet : curations.keySet()) {
            if (!usedCurations.contains(entrySet)) {
                unusedCuratedEntryCount++;
                // TODO Enable warning once treatment approaches are properly curated.
//                LOGGER.warn(" Curation entry '{}' hasn't been used for treatment approach curation", entrySet);
            }
        }

        LOGGER.debug(" Found {} unused curated entries during treatment approach curation", unusedCuratedEntryCount);
    }

    @NotNull
    public String isMatch(@NotNull TreatmentApproachCurationEntryKey key) {
        TreatmentApproachCurationEntry curationEntry = curations.get(key);

        if (curationEntry == null) {
            // TODO Enable or disable once treatment approaches are properly curated.
//            LOGGER.warn("The treatment '{}' with relevant treatment approach '{}' of event '{}' "
//                            + "with direction '{}' isn't curated because missing in curation resource",
//                    key.treatment(),
//                    key.treatmentApproach() == null ? Strings.EMPTY : key.treatmentApproach(),
//                    key.event(),
//                    key.direction());
            return Strings.EMPTY;
        }

        switch (curationEntry.curationType()) {
            case EVENT_TREATMENT_APPROACH_CURATION_IGNORE:
            case DIRECTION_TREATMENT_APPROACH_CURATION_IGNORE: {
                usedCurations.add(key);
                return Strings.EMPTY;
            }
            case TREATMENT_APPROACH_CURATION: {
                usedCurations.add(key);
                return curationEntry.curatedTreatmentApproach() == null ? Strings.EMPTY : curationEntry.curatedTreatmentApproach();
            }
            default: {
                LOGGER.warn("Curation entry found with unrecognized type: {}", curationEntry);
                return Strings.EMPTY;
            }
        }
    }
}
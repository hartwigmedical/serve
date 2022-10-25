package com.hartwig.serve.extraction.characteristic;

import java.util.Set;

import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TumorCharacteristicExtractor {

    private static final Logger LOGGER = LogManager.getLogger(TumorCharacteristicExtractor.class);

    @NotNull
    private final Set<String> microsatelliteUnstableKeyPhrases;
    @NotNull
    private final Set<String> microsatelliteStableKeyPhrases;
    @NotNull
    private final Set<String> highTumorMutationalLoadKeyPhrases;
    @NotNull
    private final Set<String> lowTumorMutationalLoadKeyPhrases;
    @NotNull
    private final Set<String> highTumorMutationalBurdenKeyPhrases;
    @NotNull
    private final Set<String> lowTumorMutationalBurdenKeyPhrases;
    @NotNull
    private final Set<String> hrDeficiencyKeyPhrases;
    @NotNull
    private final Set<String> hpvPositiveEvents;
    @NotNull
    private final Set<String> ebvPositiveEvents;

    public TumorCharacteristicExtractor(@NotNull final Set<String> microsatelliteUnstableKeyPhrases,
            @NotNull final Set<String> microsatelliteStableKeyPhrases, @NotNull final Set<String> highTumorMutationalLoadKeyPhrases,
            @NotNull final Set<String> lowTumorMutationalLoadKeyPhrases, @NotNull final Set<String> highTumorMutationalBurdenKeyPhrases,
            @NotNull final Set<String> lowTumorMutationalBurdenKeyPhrases, @NotNull final Set<String> hrDeficiencyKeyPhrases,
            @NotNull final Set<String> hpvPositiveEvents, @NotNull final Set<String> ebvPositiveEvents) {
        this.microsatelliteUnstableKeyPhrases = microsatelliteUnstableKeyPhrases;
        this.microsatelliteStableKeyPhrases = microsatelliteStableKeyPhrases;
        this.highTumorMutationalLoadKeyPhrases = highTumorMutationalLoadKeyPhrases;
        this.lowTumorMutationalLoadKeyPhrases = lowTumorMutationalLoadKeyPhrases;
        this.highTumorMutationalBurdenKeyPhrases = highTumorMutationalBurdenKeyPhrases;
        this.lowTumorMutationalBurdenKeyPhrases = lowTumorMutationalBurdenKeyPhrases;
        this.hrDeficiencyKeyPhrases = hrDeficiencyKeyPhrases;
        this.hpvPositiveEvents = hpvPositiveEvents;
        this.ebvPositiveEvents = ebvPositiveEvents;
    }

    @Nullable
    public TumorCharacteristic extract(@NotNull EventType type, @NotNull String event) {
        if (type == EventType.CHARACTERISTIC) {
            TumorCharacteristicType characteristicType = determineCharacteristicType(event);
            if (characteristicType == null) {
                LOGGER.warn("Could not extract characteristic annotation from '{}'", event);
                return null;
            }

            TumorCharacteristicCutoffType cutoffType = determineCutoffType(event);
            Double interpretedCutoff = determineCutoff(cutoffType, event);
            return ImmutableTumorCharacteristicImpl.builder()
                    .type(characteristicType)
                    .cutoffType(cutoffType)
                    .cutoff(interpretedCutoff)
                    .build();
        }
        return null;
    }

    @Nullable
    private TumorCharacteristicType determineCharacteristicType(@NotNull String event) {
        if (hasKeyPhraseMatch(event, microsatelliteUnstableKeyPhrases)) {
            return TumorCharacteristicType.MICROSATELLITE_UNSTABLE;
        } else if (hasKeyPhraseMatch(event, microsatelliteStableKeyPhrases)) {
            return TumorCharacteristicType.MICROSATELLITE_STABLE;
        } else if (hasKeyPhraseMatch(event, highTumorMutationalLoadKeyPhrases)) {
            return TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD;
        } else if (hasKeyPhraseMatch(event, lowTumorMutationalLoadKeyPhrases)) {
            return TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_LOAD;
        } else if (hasKeyPhraseMatch(event, highTumorMutationalBurdenKeyPhrases)) {
            return TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN;
        } else if (hasKeyPhraseMatch(event, lowTumorMutationalBurdenKeyPhrases)) {
            return TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN;
        } else if (hasKeyPhraseMatch(event, hrDeficiencyKeyPhrases)) {
            return TumorCharacteristicType.HOMOLOGOUS_RECOMBINATION_DEFICIENT;
        } else if (hpvPositiveEvents.contains(event)) {
            return TumorCharacteristicType.HPV_POSITIVE;
        } else if (ebvPositiveEvents.contains(event)) {
            return TumorCharacteristicType.EBV_POSITIVE;
        }

        return null;
    }

    private static boolean hasKeyPhraseMatch(@NotNull String event, @NotNull Iterable<String> keyPhrases) {
        for (String keyPhrase : keyPhrases) {
            if (event.contains(keyPhrase)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static TumorCharacteristicCutoffType determineCutoffType(@NotNull String event) {
        for (TumorCharacteristicCutoffType cutoffType : TumorCharacteristicCutoffType.values()) {
            if (event.contains(cutoffType.keyPhrase())) {
                return cutoffType;
            }
        }

        return null;
    }

    @Nullable
    private static Double determineCutoff(@Nullable TumorCharacteristicCutoffType cutoffType, @NotNull String event) {
        if (cutoffType == null) {
            return null;
        }

        int start = event.indexOf(cutoffType.keyPhrase()) + cutoffType.keyPhrase().length();
        return Double.parseDouble(event.substring(start).trim());
    }
}
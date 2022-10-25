package com.hartwig.serve.datamodel.characteristic;

import org.jetbrains.annotations.NotNull;

public enum TumorCharacteristicCutoffType {
    EQUAL_OR_LOWER("<="),
    EQUAL_OR_GREATER(">="),
    LOWER("<"),
    GREATER(">");

    @NotNull
    private final String keyPhrase;

    TumorCharacteristicCutoffType(@NotNull final String keyPhrase) {
        this.keyPhrase = keyPhrase;
    }

    @NotNull
    public String keyPhrase() {
        return keyPhrase;
    }
}

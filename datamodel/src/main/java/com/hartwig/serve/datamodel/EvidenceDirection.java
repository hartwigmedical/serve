package com.hartwig.serve.datamodel;

import org.jetbrains.annotations.NotNull;

public enum EvidenceDirection {
    RESPONSIVE(true, false, true),
    PREDICTED_RESPONSIVE(true, false, false),

    RESISTANT(false, true, true),
    PREDICTED_RESISTANT(false, true, false),
    NO_BENEFIT(false, false, true);

    private final boolean isResponsive;
    private final boolean isResistant;
    private final boolean isCertain;

    EvidenceDirection(final boolean isResponsive, final boolean isResistant, final boolean isCertain) {
        this.isResponsive = isResponsive;
        this.isResistant = isResistant;
        this.isCertain = isCertain;
    }

    public boolean isResponsive() {
        return isResponsive;
    }

    public boolean isResistant() {
        return isResistant;
    }

    @SuppressWarnings("unused")
    public boolean isCertain() {
        return isCertain;
    }

    public boolean isHigher(@NotNull EvidenceDirection other) {
        return this.ordinal() < other.ordinal();
    }
}

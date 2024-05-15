package com.hartwig.serve.datamodel;

public enum EvidenceDirection {
    RESPONSIVE(true, false, true),
    PREDICTED_RESPONSIVE(true, false, false),
    NO_BENEFIT(false, false, true),
    RESISTANT(false, true, true),
    PREDICTED_RESISTANT(false, true, false);

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
}

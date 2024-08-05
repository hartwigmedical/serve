package com.hartwig.serve.datamodel;

public enum EvidenceDirection {
    RESPONSIVE(true, false, true, false),
    PREDICTED_RESPONSIVE(true, false, false, false),
    DECREASED_RESPONSE(true, false, true, true),
    NO_BENEFIT(false, false, true, false),
    RESISTANT(false, true, true, false),
    PREDICTED_RESISTANT(false, true, false, false);

    private final boolean isResponsive;
    private final boolean isResistant;
    private final boolean isCertain;
    private final boolean isLessResponsive;

    EvidenceDirection(final boolean isResponsive, final boolean isResistant, final boolean isCertain, final boolean isLessResponsive) {
        this.isResponsive = isResponsive;
        this.isResistant = isResistant;
        this.isCertain = isCertain;
        this.isLessResponsive = isLessResponsive;
    }

    public boolean isResponsive() {
        return isResponsive;
    }

    public boolean isResistant() {
        return isResistant;
    }

    public boolean isLessResponsive() {
        return isLessResponsive;
    }

    @SuppressWarnings("unused")
    public boolean isCertain() {
        return isCertain;
    }
}

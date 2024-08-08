package com.hartwig.serve.datamodel;

public enum EvidenceDirection {
    RESPONSIVE(true, false, false, true),
    PREDICTED_RESPONSIVE(true, false, false, false),
    DECREASED_RESPONSE(false, false, false, true),
    NO_BENEFIT(false, true, false, true),
    RESISTANT(false, true, true, true),
    PREDICTED_RESISTANT(false, true, true, false);

    private final boolean hasPositiveResponse;
    private final boolean hasNoBenefit;
    private final boolean isResistant;
    private final boolean isCertain;

    EvidenceDirection(final boolean hasPositiveResponse, final boolean hasNoBenefit, final boolean isResistant, final boolean isCertain) {
        this.hasPositiveResponse = hasPositiveResponse;
        this.hasNoBenefit = hasNoBenefit;
        this.isResistant = isResistant;
        this.isCertain = isCertain;
    }

    public boolean hasPositiveResponse() {
        return hasPositiveResponse;
    }

    public boolean hasNoBenefit() {
        return hasNoBenefit;
    }

    public boolean isResistant() {
        return isResistant;
    }

    @SuppressWarnings("unused")
    public boolean isCertain() {
        return isCertain;
    }
}

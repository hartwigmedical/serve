package com.hartwig.serve.datamodel.efficacy;

public enum EvidenceDirection {
    RESPONSIVE(true, true, false, true),
    PREDICTED_RESPONSIVE(true, true, false, false),
    DECREASED_RESPONSE(false, true, false, true),
    NO_BENEFIT(false, false, false, true),
    RESISTANT(false, false, true, true),
    PREDICTED_RESISTANT(false, false, true, false);

    private final boolean hasPositiveResponse;
    private final boolean hasBenefit;
    private final boolean isResistant;
    private final boolean isCertain;

    EvidenceDirection(final boolean hasPositiveResponse, final boolean hasBenefit, final boolean isResistant, final boolean isCertain) {
        this.hasPositiveResponse = hasPositiveResponse;
        this.hasBenefit = hasBenefit;
        this.isResistant = isResistant;
        this.isCertain = isCertain;
    }

    public boolean hasPositiveResponse() {
        return hasPositiveResponse;
    }

    public boolean hasBenefit() {
        return hasBenefit;
    }

    public boolean isResistant() {
        return isResistant;
    }

    public boolean isCertain() {
        return isCertain;
    }
}

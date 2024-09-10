package com.hartwig.serve.datamodel;

public enum EvidenceLevelDetails {
    PRECLINICAL("Preclinical"),
    CASE_REPORTS_SERIES("Case report series"),
    CLINICAL_STUDY("Clinical study"),
    FDA_APPROVED("FDA approved"),
    GUIDELINE("Guideline"),
    FDA_CONTRAINDICATED("FDA contraindicated"),
    UNKNOWN("Unknown");

    public String description;

    EvidenceLevelDetails(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public EvidenceLevelDetails evidenceLevelDetailsCreator(String description) {
        this.description = description;
        return this;
    }
}

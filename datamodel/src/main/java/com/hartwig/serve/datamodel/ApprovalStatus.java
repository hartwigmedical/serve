package com.hartwig.serve.datamodel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ApprovalStatus {
    PRECLINICAL("Preclinical"),
    CASE_REPORTS_SERIES("Case Reports/Case Series"),
    PRECLINICAL_PATIENT_CELL_CULTURE("Preclinical - Patient cell culture"),
    PRECLINICAL_PDX_CELL_CULTURE("Preclinical - Pdx & cell culture"),
    PRECLINICAL_PDX("Preclinical - Pdx"),
    PRECLINICAL_CELL_CULTURE("Preclinical - Cell culture"),
    CLINICAL_STUDY("Clinical Study"),
    PRECLINICAL_CELL_LINE_XENOGRAFT("Preclinical - Cell line xenograft"),
    PRECLINICAL_BIOCHEMICAL("Preclinical - Biochemical"),
    PHASE_III("Phase III"),
    PHASE_II("Phase II"),
    PHASE_I("Phase I"),
    FDA_APPROVED_HAS_COMPANION_DIAGNOSTIC("FDA approved - Has Companion Diagnostic"),
    CLINICAL_STUDY_COHORT("Clinical Study - Cohort"),
    FDA_APPROVED("FDA approved"),
    GUIDELINE("Guideline"),
    FDA_APPROVED_ON_COMPANION_DIAGNOSTIC("FDA approved - On Companion Diagnostic"),
    PHASE_IB_II("Phase Ib/II"),
    PHASE_0("Phase 0"),
    CLINICAL_STUDY_META_ANALYSIS("Clinical Study - Meta-analysis"),
    FDA_CONTRAINDICATED("FDA contraindicated");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public static ApprovalStatus fromString(@NotNull String description) {
        for (ApprovalStatus status : ApprovalStatus.values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        return null;
    }
}

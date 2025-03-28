package com.hartwig.serve.sources.ckb.filter;

public enum CkbEvidenceFilterType {
    ALL_EVIDENCE_BASED_ON_GENE,
    ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
    EVIDENCE_BASED_ON_THERAPY,
    EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
    EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
    EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT
}
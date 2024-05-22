package com.hartwig.serve.sources.ckb.blacklist;

public enum CkbBlacklistStudyType {
    ALL_STUDIES_BASED_ON_GENE,
    ALL_STUDIES_BASED_ON_GENE_AND_EVENT,
    STUDY_WHOLE,
    STUDY_BASED_ON_THERAPY,
    STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
    STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
    STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT
}

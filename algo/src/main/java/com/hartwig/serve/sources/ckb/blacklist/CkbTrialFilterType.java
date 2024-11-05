package com.hartwig.serve.sources.ckb.blacklist;

public enum CkbTrialFilterType {
    ALL_TRIALS_BASED_ON_GENE,
    ALL_TRIALS_BASED_ON_GENE_AND_EVENT,
    COMPLETE_TRIAL,
    TRIAL_BASED_ON_THERAPY,
    TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE,
    TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
    TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT
}

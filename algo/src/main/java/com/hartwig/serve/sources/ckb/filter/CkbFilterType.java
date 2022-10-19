package com.hartwig.serve.sources.ckb.filter;

public enum CkbFilterType {
    ALLOW_GENE_IN_FUSIONS_EXCLUSIVELY,
    FILTER_EVENT_WITH_KEYWORD,
    FILTER_EXACT_VARIANT_FULLNAME,
    FILTER_ALL_EVIDENCE_ON_GENE,
    FILTER_EVIDENCE_FOR_EXONS_ON_GENE,
    FILTER_SECONDARY_GENE_WHEN_FUSION_LEG
}

package com.hartwig.serve.sources.ckb.blacklist;

import java.util.Set;

import com.google.common.collect.Sets;

public abstract class BlacklistConditions {

    //Sets for CKB_EVIDENCE
    public static Set<CkbBlacklistEvidenceType> evidenceContainsTherapy = Sets.newHashSet(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY,
            CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public static Set<CkbBlacklistEvidenceType> evidenceContainsCancerType = Sets.newHashSet(CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public static Set<CkbBlacklistEvidenceType> evidenceContainsGene = Sets.newHashSet(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
            CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE,
            CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT);
    public static Set<CkbBlacklistEvidenceType> evidenceContainsEvent = Sets.newHashSet(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);

    //Sets for CKB_TRIAL
    public static Set<CkbBlacklistStudyType> studyContainsTherapy = Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY,
            CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
            CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public static Set<CkbBlacklistStudyType> studyContainsCancerType = Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
            CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public static Set<CkbBlacklistStudyType> studyContainsGene = Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE);
    public static Set<CkbBlacklistStudyType> studyContainsEvent = Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
            CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE_AND_EVENT);
}

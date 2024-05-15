package com.hartwig.serve.sources.ckb.blacklist;

import java.util.Set;

import com.google.common.collect.Sets;

public final class BlacklistConstants {

    private BlacklistConstants() {
    }

    //Sets for CKB_EVIDENCE
    public final static Set<CkbBlacklistEvidenceType> EVIDENCE_BLACKLIST_TYPES_CONTAINING_THERAPY =
            Sets.newHashSet(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY,
                    CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
                    CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public final static Set<CkbBlacklistEvidenceType> EVIDENCE_BLACKLIST_TYPES_CONTAINING_CANCER_TYPE = Sets.newHashSet(
            CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public final static Set<CkbBlacklistEvidenceType> EVIDENCE_BLACKLIST_TYPES_CONTAINING_GENE =
            Sets.newHashSet(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                    CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE,
                    CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT);
    public final static Set<CkbBlacklistEvidenceType> EVIDENCE_BLACKLIST_TYPES_CONTAINING_EVENT =
            Sets.newHashSet(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
                    CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);

    //Sets for CKB_TRIAL
    public final static Set<CkbBlacklistStudyType> STUDY_BLACKLIST_TYPES_CONTAINING_THERAPY =
            Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY,
                    CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
                    CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public final static Set<CkbBlacklistStudyType> STUDY_BLACKLIST_TYPES_CONTAINING_CANCER_TYPE =
            Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
                    CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);
    public final static Set<CkbBlacklistStudyType> STUDY_BLACKLIST_TYPES_CONTAINING_GENE =
            Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE);
    public final static Set<CkbBlacklistStudyType> STUDY_BLACKLIST_TYPES_CONTAINING_EVENT =
            Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                    CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE_AND_EVENT);

}
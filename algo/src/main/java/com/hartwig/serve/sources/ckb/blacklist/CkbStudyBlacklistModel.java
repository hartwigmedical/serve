package com.hartwig.serve.sources.ckb.blacklist;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbStudyBlacklistModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbStudyBlacklistModel.class);

    @NotNull
    private final List<CkbBlacklistStudyEntry> BLACKLIST_STUDIES_ENTRIES;
    @NotNull
    private final Set<CkbBlacklistStudyEntry> USED_BLACKLIST_STUDIES_ENTRIES = Sets.newHashSet();

    public CkbStudyBlacklistModel(@NotNull final List<CkbBlacklistStudyEntry> blacklistStudiesList) {
        this.BLACKLIST_STUDIES_ENTRIES = blacklistStudiesList;
    }

    public boolean isBlacklistStudy(@NotNull String nctId, @NotNull String therapyName, @NotNull String cancerType, @NotNull String sourceGene,
                                    @NotNull String event) {
        for (CkbBlacklistStudyEntry blacklistStudyEntry : BLACKLIST_STUDIES_ENTRIES) {
            boolean match = isMatch(nctId, therapyName, cancerType, sourceGene, event, blacklistStudyEntry);
            if (match) {
                USED_BLACKLIST_STUDIES_ENTRIES.add(blacklistStudyEntry);
                return true;
            }
        }
        return false;
    }

    public void reportUnusedBlacklistEntries() {
        int unusedBlacklistEntryCount = 0;
        for (CkbBlacklistStudyEntry entry : BLACKLIST_STUDIES_ENTRIES) {
            if (!USED_BLACKLIST_STUDIES_ENTRIES.contains(entry)) {
                unusedBlacklistEntryCount++;
                LOGGER.warn(" Blacklist study entry '{}' hasn't been used for CKB filtering", entry);
            }
        }
        LOGGER.debug(" Found {} unused blacklist study entries during CKB filtering", unusedBlacklistEntryCount);
    }

    public boolean isMatch(@NotNull String nctId, @NotNull String therapyName, @NotNull String cancerType, @NotNull String sourceGene,
                           @NotNull String event, @NotNull CkbBlacklistStudyEntry blacklistStudyEntry) {
        switch (blacklistStudyEntry.type()) {
            case STUDY_WHOLE: {
                return blacklistStudyEntry.nctId().equals(nctId);
            }
            case STUDY_BASED_ON_THERAPY: {
                String blacklistEvidenceTherapy = blacklistStudyEntry.therapy();
                assert blacklistEvidenceTherapy != null;
                return blacklistStudyEntry.nctId().equals(nctId)
                        && blacklistEvidenceTherapy.equals(therapyName);
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE: {
                String blacklistEvidenceTherapy = blacklistStudyEntry.therapy();
                String blacklistEvidenceCancerType = blacklistStudyEntry.cancerType();
                assert blacklistEvidenceTherapy != null;
                assert blacklistEvidenceCancerType != null;
                return blacklistStudyEntry.nctId().equals(nctId)
                        && blacklistEvidenceTherapy.equals(therapyName)
                        && blacklistEvidenceCancerType.equals(cancerType);
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE: {
                String blacklistEvidenceTherapy = blacklistStudyEntry.therapy();
                String blacklistEvidenceCancerType = blacklistStudyEntry.cancerType();
                String blacklistEvidenceGene= blacklistStudyEntry.gene();
                assert blacklistEvidenceGene != null;
                assert blacklistEvidenceTherapy != null;
                assert blacklistEvidenceCancerType != null;
                return blacklistStudyEntry.nctId().equals(nctId)
                        && blacklistEvidenceTherapy.equals(therapyName)
                        && blacklistEvidenceCancerType.equals(cancerType)
                        && blacklistEvidenceGene.equals(sourceGene);
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT: {
                String blacklistEvidenceTherapy = blacklistStudyEntry.therapy();
                String blacklistEvidenceCancerType = blacklistStudyEntry.cancerType();
                String blacklistEvidenceGene= blacklistStudyEntry.gene();
                String blacklistEvidenceEvent= blacklistStudyEntry.event();
                assert blacklistEvidenceGene != null;
                assert blacklistEvidenceEvent != null;
                assert blacklistEvidenceTherapy != null;
                assert blacklistEvidenceCancerType != null;

                return blacklistStudyEntry.nctId().equals(nctId)
                        && blacklistEvidenceTherapy.equals(therapyName)
                        && blacklistEvidenceCancerType.equals(cancerType)
                        && blacklistEvidenceGene.equals(sourceGene)
                        && blacklistEvidenceEvent.equals(event);
            }
            case ALL_STUDIES_BASED_ON_GENE: {
                String blacklistEvidenceGene= blacklistStudyEntry.gene();
                assert blacklistEvidenceGene != null;
                return blacklistEvidenceGene.equals(sourceGene);
            }
            case ALL_STUDIES_BASED_ON_GENE_AND_EVENT: {
                String blacklistEvidenceGene= blacklistStudyEntry.gene();
                String blacklistEvidenceEvent= blacklistStudyEntry.event();
                assert blacklistEvidenceEvent != null;
                assert blacklistEvidenceGene != null;
                return blacklistEvidenceGene.equals(sourceGene)
                        && blacklistEvidenceEvent.equals(event);
            }
            default: {
                LOGGER.warn("Blacklist study entry found with unrecognized type: {}", blacklistStudyEntry.type());
                return false;
            }
        }
    }
}
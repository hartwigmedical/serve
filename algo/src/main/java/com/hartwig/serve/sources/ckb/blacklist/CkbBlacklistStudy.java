package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class CkbBlacklistStudy {

    private static final Logger LOGGER = LogManager.getLogger(CkbBlacklistStudy.class);

    @NotNull
    private final List<CkbBlacklistStudyEntry> blacklistStudiesList;
    @NotNull
    private final Set<CkbBlacklistStudyEntry> usedBlacklists = Sets.newHashSet();

    public CkbBlacklistStudy(@NotNull final List<CkbBlacklistStudyEntry> blacklistStudiesList) {
        this.blacklistStudiesList = blacklistStudiesList;
    }

    public boolean isBlacklistStudy(@NotNull String studyName, @NotNull String therapyName, @NotNull String cancerType, @NotNull String sourceGene,
                                    @NotNull String event) {

        for (CkbBlacklistStudyEntry blacklistStudyEntry : blacklistStudiesList) {
            boolean match = isMatch(studyName, therapyName, cancerType, sourceGene, event, blacklistStudyEntry);
            if (match) {
                usedBlacklists.add(blacklistStudyEntry);
                return false;
            }
        }
        return false;
    }

    public boolean isMatch(@NotNull String studyName, @NotNull String therapyName, @NotNull String cancerType, @NotNull String sourceGene,
                           @NotNull String event, @NotNull CkbBlacklistStudyEntry blacklistStudyEntry) {

        switch (blacklistStudyEntry.ckbBlacklistReason()) {
            case STUDY_WHOLE: {
                return blacklistStudyEntry.nctId().equals(studyName);
            }
            case STUDY_BASED_ON_THERAPY: {
                return blacklistStudyEntry.nctId().equals(studyName)
                        && blacklistStudyEntry.therapy().equals(therapyName);
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE: {
                return blacklistStudyEntry.nctId().equals(studyName)
                        && blacklistStudyEntry.therapy().equals(therapyName)
                        && blacklistStudyEntry.cancerType().equals(cancerType);
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE: {
                return blacklistStudyEntry.nctId().equals(studyName)
                        && blacklistStudyEntry.therapy().equals(therapyName)
                        && blacklistStudyEntry.cancerType().equals(cancerType)
                        && blacklistStudyEntry.gene().equals(sourceGene);
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT: {
                return blacklistStudyEntry.nctId().equals(studyName)
                        && blacklistStudyEntry.therapy().equals(therapyName)
                        && blacklistStudyEntry.cancerType().equals(cancerType)
                        && blacklistStudyEntry.gene().equals(sourceGene)
                        && blacklistStudyEntry.event().equals(event);
            }
            case ALL_STUDIES_BASED_ON_GENE: {
                return blacklistStudyEntry.nctId().equals(studyName)
                        && blacklistStudyEntry.gene().equals(sourceGene);
            }
            case ALL_STUDIES_BASED_ON_GENE_AND_EVENT: {
                return blacklistStudyEntry.nctId().equals(studyName)
                        && blacklistStudyEntry.gene().equals(sourceGene)
                        && blacklistStudyEntry.event().equals(event);
            }
            default: {
                LOGGER.warn("Blacklist entry found with unrecognized type: {}", blacklistStudyEntry.ckbBlacklistReason());
                return false;
            }
        }
    }

    public void reportUnusedBlacklistEntries() {
        int unusedBlacklistEntryCount = 0;
        for (CkbBlacklistStudyEntry entry : blacklistStudiesList) {
            if (!usedBlacklists.contains(entry)) {
                unusedBlacklistEntryCount++;
                LOGGER.warn(" Blacklist entry '{}' hasn't been used for CKB filtering", entry);
            }
        }

        LOGGER.debug(" Found {} unused blacklist entries during CKB filtering", unusedBlacklistEntryCount);
    }
}
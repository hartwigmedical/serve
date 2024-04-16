package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.EvidenceLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class CkbBlacklistEvidence {

    private static final Logger LOGGER = LogManager.getLogger(CkbBlacklistEvidence.class);

    @NotNull
    private final List<CkbBlacklistEvidenceEntry> blacklistEvidenceEntryList;
    @NotNull
    private final Set<CkbBlacklistEvidenceEntry> usedBlackEvidencelists = Sets.newHashSet();

    public CkbBlacklistEvidence(@NotNull final List<CkbBlacklistEvidenceEntry> blacklistEvidenceEntryList) {
        this.blacklistEvidenceEntryList = blacklistEvidenceEntryList;
    }

    public boolean isBlacklistEvidence(@NotNull String therapyName, @NotNull String cancerType, @NotNull EvidenceLevel level, @NotNull String sourceGene,
                                       @NotNull String event) {

        for (CkbBlacklistEvidenceEntry blacklistEvidenceEntry : blacklistEvidenceEntryList) {
            boolean match = isMatch(therapyName, cancerType, level, sourceGene, event, blacklistEvidenceEntry);
            if (match) {
                usedBlackEvidencelists.add(blacklistEvidenceEntry);
                return true;
            }
        }
        return false;
    }

    public void reportUnusedBlacklistEntries() {
        int unusedBlacklistEntryCount = 0;
        for (CkbBlacklistEvidenceEntry entry : blacklistEvidenceEntryList) {
            if (!blacklistEvidenceEntryList.contains(entry)) {
                unusedBlacklistEntryCount++;
                LOGGER.warn(" Blacklist entry '{}' hasn't been used for CKB filtering", entry);
            }
        }

        LOGGER.debug(" Found {} unused blacklist entries during CKB filtering", unusedBlacklistEntryCount);
    }

    public boolean isMatch(@NotNull String therapyName, @NotNull String cancerType, @NotNull EvidenceLevel level,  @NotNull String sourceGene,
                           @NotNull String event, @NotNull CkbBlacklistEvidenceEntry blacklistEvidenceEntry) {
        switch (blacklistEvidenceEntry.ckbBlacklistEvidenceReason()) {
            case ALL_EVIDENCE_BASED_ON_GENE: {
                String blacklistEvidenceGene = blacklistEvidenceEntry.gene();
                assert blacklistEvidenceGene != null;
                if (blacklistEvidenceEntry.level() == null) {
                    return blacklistEvidenceGene.equals(sourceGene);
                } else {
                    return blacklistEvidenceGene.equals(sourceGene)
                            && blacklistEvidenceEntry.level() == level;
                }
            }

            case ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT: {
                String blacklistEvidenceGene = blacklistEvidenceEntry.gene();
                String blacklistEvidenceEvent = blacklistEvidenceEntry.event();
                assert blacklistEvidenceGene != null;
                assert blacklistEvidenceEvent != null;
                if (blacklistEvidenceEntry.level() == null) {
                    return blacklistEvidenceGene.equals(sourceGene)
                            && blacklistEvidenceEvent.equals(event);
                } else {
                    return blacklistEvidenceGene.equals(sourceGene)
                            && blacklistEvidenceEvent.equals(event)
                            && blacklistEvidenceEntry.level() == level;
                }
            }

            case EVIDENCE_BASED_ON_THERAPY: {
                String blacklistEvidenceTherapy = blacklistEvidenceEntry.therapy();
                assert blacklistEvidenceTherapy != null;
                if (blacklistEvidenceEntry.level() == null) {
                    return blacklistEvidenceTherapy.equals(therapyName);
                } else {
                    return blacklistEvidenceTherapy.equals(therapyName)
                            && blacklistEvidenceEntry.level() == level;
                }
            }

            case EVIDENCE_ON_THERAPY_AND_CANCER_TYPE: {
                String blacklistEvidenceTherapy = blacklistEvidenceEntry.therapy();
                String blacklistEvidenceCancerType = blacklistEvidenceEntry.cancerType();
                assert blacklistEvidenceTherapy != null;
                assert blacklistEvidenceCancerType != null;
                if (blacklistEvidenceEntry.level() == null) {
                    return blacklistEvidenceTherapy.equals(therapyName)
                            && blacklistEvidenceCancerType.equals(cancerType);
                } else {
                    return blacklistEvidenceTherapy.equals(therapyName)
                            && blacklistEvidenceCancerType.equals(cancerType)
                            && blacklistEvidenceEntry.level() == level;
                }
            }

            case EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE: {
                String blacklistEvidenceTherapy = blacklistEvidenceEntry.therapy();
                String blacklistEvidenceCancerType = blacklistEvidenceEntry.cancerType();
                String blacklistEvidenceGene = blacklistEvidenceEntry.gene();
                assert blacklistEvidenceTherapy != null;
                assert blacklistEvidenceCancerType != null;
                assert blacklistEvidenceGene != null;
                if (blacklistEvidenceEntry.level() == null) {
                    return blacklistEvidenceTherapy.equals(therapyName)
                            && blacklistEvidenceCancerType.equals(cancerType)
                            && blacklistEvidenceGene.equals(sourceGene);
                } else {
                    return blacklistEvidenceTherapy.equals(therapyName)
                            && blacklistEvidenceCancerType.equals(cancerType)
                            && blacklistEvidenceGene.equals(sourceGene)
                            && blacklistEvidenceEntry.level() == level;
                }
            }

            case EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT: {
                String blacklistEvidenceTherapy = blacklistEvidenceEntry.therapy();
                String blacklistEvidenceCancerType = blacklistEvidenceEntry.cancerType();
                String blacklistEvidenceGene = blacklistEvidenceEntry.gene();
                String blacklistEvidenceEvent = blacklistEvidenceEntry.event();
                assert blacklistEvidenceGene != null;
                assert blacklistEvidenceTherapy != null;
                assert blacklistEvidenceCancerType != null;
                assert blacklistEvidenceEvent != null;
                if (blacklistEvidenceEntry.level() == null) {
                    return blacklistEvidenceTherapy.equals(therapyName)
                            && blacklistEvidenceCancerType.equals(cancerType)
                            && blacklistEvidenceGene.equals(sourceGene)
                            && blacklistEvidenceEvent.equals(event);
                } else {
                    return blacklistEvidenceTherapy.equals(therapyName)
                            && blacklistEvidenceCancerType.equals(cancerType)
                            && blacklistEvidenceGene.equals(sourceGene)
                            && blacklistEvidenceEvent.equals(event)
                            && blacklistEvidenceEntry.level() == level;
                }
            }

            default: {
                LOGGER.warn("Blacklist entry found with unrecognized type: {}", blacklistEvidenceEntry.ckbBlacklistEvidenceReason());
                return false;
            }
        }
    }
}
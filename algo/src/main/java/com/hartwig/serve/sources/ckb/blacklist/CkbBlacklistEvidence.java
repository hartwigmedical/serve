package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.evidence.Evidence;
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

    @NotNull
    public List<CkbEntry> run(@NotNull List<CkbEntry> ckbEntries) {
        List<CkbEntry> filteredCkbEntries = Lists.newArrayList();
        List<Evidence> filteredCkbEvidenceEntries = Lists.newArrayList();

        for (CkbEntry entry : ckbEntries) {
            String molecularProfile = entry.profileName();

            for (Evidence evidence : entry.evidences()) {
                String therapy = evidence.therapy().therapyName();
                String indication = evidence.indication().name();

                if (include(molecularProfile, therapy, indication)) {
                    filteredCkbEvidenceEntries.add(evidence);
                } else {
                    LOGGER.debug("Blacklisting evidence on therapy '{}', cancerType '{}', molecular profile '{}'", therapy, indication, molecularProfile);
                }
                if (!filteredCkbEvidenceEntries.isEmpty()) {
                    filteredCkbEntries.add(ImmutableCkbEntry.builder().from(entry).evidences(filteredCkbEvidenceEntries).build());
                }
            }
        }
        return filteredCkbEntries;
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

    private boolean include(@NotNull String molecularProfile, @NotNull String therapy, @NotNull String indication) {
        for (CkbBlacklistEvidenceEntry blacklistEvidenceEntry : blacklistEvidenceEntryList) {
            boolean filterMatches = isMatch(blacklistEvidenceEntry, molecularProfile, therapy, indication);
            if (filterMatches) {
                usedBlackEvidencelists.add(blacklistEvidenceEntry);
                return false;
            }
        }
        return true;
    }

    private boolean isMatch(@NotNull CkbBlacklistEvidenceEntry blacklistEvidenceEntry, @NotNull String molecularProfile, @NotNull String therapy, @NotNull String indication) {
        switch (blacklistEvidenceEntry.ckbBlacklistEvidenceReason()) {
            case EVIDENCE_THERAPY: {
                return blacklistEvidenceEntry.therapy().equals(therapy);
            }

            case EVIDENCE_CANCER_TYPE: {
                return blacklistEvidenceEntry.therapy().equals(therapy) &&
                        blacklistEvidenceEntry.cancerType().equals(indication);
            }

            case EVIDENCE_MOLECULAR_PROFILE: {
                LOGGER.info(molecularProfile);
                LOGGER.info(blacklistEvidenceEntry.molecularProfile());
                return blacklistEvidenceEntry.therapy().equals(therapy) &&
                        blacklistEvidenceEntry.cancerType().equals(indication) &&
                        blacklistEvidenceEntry.molecularProfile().equals(molecularProfile);
            }

            case ALL_MOLECULAR_PROFILE: {
                return blacklistEvidenceEntry.molecularProfile().equals(molecularProfile);
            }

            default: {
                LOGGER.warn("Blacklist entry found with unrecognized type: {}", blacklistEvidenceEntry.ckbBlacklistEvidenceReason());
                return false;
            }
        }
    }
}
package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
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

    @NotNull
    public List<CkbEntry> run(@NotNull CkbEntry ckbEntries) {
        List<CkbEntry> filteredCkbEntries = Lists.newArrayList();
        List<ClinicalTrial> filteredCkbStudiesEntries = Lists.newArrayList();

        for (ClinicalTrial clinicalTrial : ckbEntries.clinicalTrials()) {
            if (include(clinicalTrial, ckbEntries.profileName())) {
                filteredCkbStudiesEntries.add(clinicalTrial);
            } else {
                LOGGER.debug("Blacklisting study '{}'", clinicalTrial.nctId());
            }
            if (!filteredCkbStudiesEntries.isEmpty()) {
                filteredCkbEntries.add(ImmutableCkbEntry.builder().from(ckbEntries).clinicalTrials(filteredCkbStudiesEntries).build());
            }
        }


        return filteredCkbEntries;
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

    private boolean include(@NotNull ClinicalTrial clinicalTrial, @NotNull String profileName) {
        for (CkbBlacklistStudyEntry blacklistStudyEntry : blacklistStudiesList) {
            boolean filterMatches = isMatch(blacklistStudyEntry, clinicalTrial, profileName);
            if (filterMatches) {
                usedBlacklists.add(blacklistStudyEntry);
                return false;
            }
        }
        return true;
    }

    private boolean isMatch(@NotNull CkbBlacklistStudyEntry blacklistStudyEntry, @NotNull ClinicalTrial clinicalTrial, @NotNull String profileName) {
        switch (blacklistStudyEntry.ckbBlacklistReason()) {
            case STUDY_WHOLE: {
                return blacklistStudyEntry.nctId().equals(clinicalTrial.nctId());
            }
            case STUDY_BASED_ON_THERAPY: {
                for (Therapy therapy : clinicalTrial.therapies()) {
                    boolean therapyMatch = false;
                    if (blacklistStudyEntry.therapy().equals(therapy.therapyName())) {
                        therapyMatch = true;
                    }
                    return blacklistStudyEntry.nctId().equals(clinicalTrial.nctId()) && therapyMatch;
                }
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE: {
                boolean therapyMatch = false;
                boolean indicationMatch = false;

                for (Therapy therapy : clinicalTrial.therapies()) {
                    if (blacklistStudyEntry.therapy().equals(therapy.therapyName())) {
                        therapyMatch = true;
                        for (Indication indication : clinicalTrial.indications()) {
                            if (blacklistStudyEntry.cancerType().equals(indication.name())) {
                                indicationMatch = true;
                            }
                        }
                    }
                }
                return blacklistStudyEntry.nctId().equals(clinicalTrial.nctId()) && therapyMatch && indicationMatch;
            }
            case STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_MOLECULAR_PROFILE: {
                boolean therapyMatch = false;
                boolean indicationMatch = false;

                for (Therapy therapy : clinicalTrial.therapies()) {
                    if (blacklistStudyEntry.therapy().equals(therapy.therapyName())) {
                        therapyMatch = true;
                        for (Indication indication : clinicalTrial.indications()) {
                            if (blacklistStudyEntry.cancerType().equals(indication.name())) {
                                indicationMatch = true;
                            }
                        }
                    }
                }
                return blacklistStudyEntry.nctId().equals(clinicalTrial.nctId()) && therapyMatch && indicationMatch && blacklistStudyEntry.molecularProfile().equals(profileName);
            }
            case ALL_STUDIES_BASED_ON_MOLECULAR_PROFILE: {
                return blacklistStudyEntry.molecularProfile().equals(profileName);
            }
            default: {
                LOGGER.warn("Blacklist entry found with unrecognized type: {}", blacklistStudyEntry.ckbBlacklistReason());
                return false;
            }
        }
    }
}
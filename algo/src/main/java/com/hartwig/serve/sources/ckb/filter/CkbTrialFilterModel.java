package com.hartwig.serve.sources.ckb.filter;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbTrialFilterModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbTrialFilterModel.class);

    @NotNull
    private final List<CkbTrialFilterEntry> filterEntries;
    @NotNull
    private final Set<CkbTrialFilterEntry> usedFilterEntries = Sets.newHashSet();

    public CkbTrialFilterModel(@NotNull final List<CkbTrialFilterEntry> filterEntries) {
        this.filterEntries = filterEntries;
    }

    public boolean shouldFilterTrial(@NotNull String nctId, @NotNull String therapyName, @NotNull String cancerType,
            @NotNull String sourceGene, @NotNull String event) {
        for (CkbTrialFilterEntry filterEntry : filterEntries) {
            if (isMatch(nctId, therapyName, cancerType, sourceGene, event, filterEntry)) {
                usedFilterEntries.add(filterEntry);
                return true;
            }
        }
        return false;
    }

    public void reportUnusedFilterEntries() {
        int unusedFilterEntryCount = 0;
        for (CkbTrialFilterEntry entry : filterEntries) {
            if (!usedFilterEntries.contains(entry)) {
                unusedFilterEntryCount++;
                LOGGER.warn(" Trial filter entry '{}' hasn't been used for CKB trial filtering", entry);
            }
        }
        LOGGER.debug(" Found {} unused trial filter entries during CKB trial filtering", unusedFilterEntryCount);
    }

    public boolean isMatch(@NotNull String nctId, @NotNull String therapyName, @NotNull String cancerType, @NotNull String sourceGene,
            @NotNull String event, @NotNull CkbTrialFilterEntry filterEntry) {
        switch (filterEntry.type()) {
            case COMPLETE_TRIAL: {
                return filterEntry.nctId().equals(nctId);
            }
            case TRIAL_BASED_ON_THERAPY: {
                String filterTherapy = filterEntry.therapy();
                assert filterTherapy != null;
                return filterEntry.nctId().equals(nctId) && filterTherapy.equals(therapyName);
            }
            case TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE: {
                String filterTherapy = filterEntry.therapy();
                String filterCancerType = filterEntry.cancerType();
                assert filterTherapy != null;
                assert filterCancerType != null;
                return filterEntry.nctId().equals(nctId) && filterTherapy.equals(therapyName) && filterCancerType.equals(cancerType);
            }
            case TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE: {
                String filterTherapy = filterEntry.therapy();
                String filterCancerType = filterEntry.cancerType();
                String filterGene = filterEntry.gene();
                assert filterGene != null;
                assert filterTherapy != null;
                assert filterCancerType != null;
                return filterEntry.nctId().equals(nctId) && filterTherapy.equals(therapyName) && filterCancerType.equals(cancerType)
                        && filterGene.equals(sourceGene);
            }
            case TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT: {
                String filterTherapy = filterEntry.therapy();
                String filterCancerType = filterEntry.cancerType();
                String filterGene = filterEntry.gene();
                String filterEvent = filterEntry.event();
                assert filterGene != null;
                assert filterEvent != null;
                assert filterTherapy != null;
                assert filterCancerType != null;

                return filterEntry.nctId().equals(nctId) && filterTherapy.equals(therapyName) && filterCancerType.equals(cancerType)
                        && filterGene.equals(sourceGene) && filterEvent.equals(event);
            }
            case ALL_TRIALS_BASED_ON_GENE: {
                String filterGene = filterEntry.gene();
                assert filterGene != null;
                return filterGene.equals(sourceGene);
            }
            case ALL_TRIALS_BASED_ON_GENE_AND_EVENT: {
                String filterGene = filterEntry.gene();
                String filterEvent = filterEntry.event();
                assert filterEvent != null;
                assert filterGene != null;
                return filterGene.equals(sourceGene) && filterEvent.equals(event);
            }
            default: {
                LOGGER.warn("Trial filter entry found with unrecognized type: {}", filterEntry.type());
                return false;
            }
        }
    }
}
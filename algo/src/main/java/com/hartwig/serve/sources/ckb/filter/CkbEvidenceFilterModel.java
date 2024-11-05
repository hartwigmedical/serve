package com.hartwig.serve.sources.ckb.filter;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbEvidenceFilterModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbEvidenceFilterModel.class);

    @NotNull
    private final List<CkbEvidenceFilterEntry> filterEntries;
    @NotNull
    private final Set<CkbEvidenceFilterEntry> usedFilterEntries = Sets.newHashSet();

    public CkbEvidenceFilterModel(@NotNull final List<CkbEvidenceFilterEntry> filterEntries) {
        this.filterEntries = filterEntries;
    }

    public boolean shouldFilterEvidence(@NotNull String therapyName, @NotNull String cancerType, @NotNull EvidenceLevel level,
            @NotNull String sourceGene, @NotNull String event) {
        for (CkbEvidenceFilterEntry filterEntry : filterEntries) {
            boolean match = isMatch(therapyName, cancerType, level, sourceGene, event, filterEntry);
            if (match) {
                usedFilterEntries.add(filterEntry);
                return true;
            }
        }
        return false;
    }

    public void reportUnusedFilterEntries() {
        int unusedFilterEntryCount = 0;
        for (CkbEvidenceFilterEntry entry : filterEntries) {
            if (!usedFilterEntries.contains(entry)) {
                unusedFilterEntryCount++;
                LOGGER.warn(" Evidence filter entry '{}' hasn't been used for CKB evidence filtering", entry);
            }
        }

        LOGGER.debug(" Found {} unused evidence filter entries during CKB evidence filtering", unusedFilterEntryCount);
    }

    @VisibleForTesting
    public boolean isMatch(@NotNull String therapyName, @NotNull String cancerType, @NotNull EvidenceLevel level,
            @NotNull String sourceGene, @NotNull String event, @NotNull CkbEvidenceFilterEntry filterEntry) {
        boolean hasValidLevel = filterEntry.level() == null || filterEntry.level() == level;

        switch (filterEntry.type()) {
            case ALL_EVIDENCE_BASED_ON_GENE: {
                String filterGene = filterEntry.gene();
                assert filterGene != null;
                return hasValidLevel && filterGene.equals(sourceGene);
            }

            case ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT: {
                String filterGene = filterEntry.gene();
                String filterEvent = filterEntry.event();
                assert filterGene != null;
                assert filterEvent != null;
                return hasValidLevel && filterGene.equals(sourceGene) && filterEvent.equals(event);
            }

            case EVIDENCE_BASED_ON_THERAPY: {
                String filterTherapy = filterEntry.therapy();
                assert filterTherapy != null;
                return hasValidLevel && filterTherapy.equals(therapyName);
            }

            case EVIDENCE_ON_THERAPY_AND_CANCER_TYPE: {
                String filterTherapy = filterEntry.therapy();
                String filterCancerType = filterEntry.cancerType();
                assert filterTherapy != null;
                assert filterCancerType != null;
                return hasValidLevel && filterTherapy.equals(therapyName) && filterCancerType.equals(cancerType);
            }

            case EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE: {
                String filterTherapy = filterEntry.therapy();
                String filterCancerType = filterEntry.cancerType();
                String filterGene = filterEntry.gene();
                assert filterTherapy != null;
                assert filterCancerType != null;
                assert filterGene != null;
                return hasValidLevel && filterTherapy.equals(therapyName) && filterCancerType.equals(cancerType) && filterGene.equals(
                        sourceGene);
            }

            case EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT: {
                String filterTherapy = filterEntry.therapy();
                String filterCancerType = filterEntry.cancerType();
                String filterGene = filterEntry.gene();
                String filterEvent = filterEntry.event();
                assert filterGene != null;
                assert filterTherapy != null;
                assert filterCancerType != null;
                assert filterEvent != null;
                return hasValidLevel && filterTherapy.equals(therapyName) && filterCancerType.equals(cancerType) && filterGene.equals(
                        sourceGene) && filterEvent.equals(event);
            }

            default: {
                LOGGER.warn("Evidence filter entry found with unrecognized type: {}", filterEntry.type());
                return false;
            }
        }
    }
}
package com.hartwig.serve.sources.ckb.blacklist;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbMolecularProfileBlacklistModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbMolecularProfileBlacklistModel.class);

    @NotNull
    private final List<CkbBlacklistMolecularProfileEntry> blacklistEntries;
    @NotNull
    private final Set<CkbBlacklistMolecularProfileEntry> usedBlacklistEntries = Sets.newHashSet();

    public CkbMolecularProfileBlacklistModel(@NotNull final List<CkbBlacklistMolecularProfileEntry> blacklistEntries) {
        this.blacklistEntries = blacklistEntries;
    }

    @NotNull
    public List<CkbEntry> run(@NotNull List<CkbEntry> ckbEntries) {
        List<CkbEntry> filteredCkbEntries = Lists.newArrayList();
        for (CkbEntry entry : ckbEntries) {
            if (entry.variants().size() > 1) {
                // Do not filter variants when in combination event, since this might make them a non-combined event.
                filteredCkbEntries.add(entry);
            } else if (entry.variants().isEmpty()) {
                // Always filter entries with no variants. Should never happen in practice!
                LOGGER.warn("Filtering '{}' because no variants have been defined for this entry!", entry);
            } else {
                List<Variant> filteredVariants = Lists.newArrayList();

                Variant variant = entry.variants().get(0);
                if (include(entry.type(), variant)) {
                    filteredVariants.add(variant);
                } else {
                    LOGGER.debug("Filtering variant '{}' on '{}'", variant.variant(), variant.gene().geneSymbol());
                }

                if (!filteredVariants.isEmpty()) {
                    filteredCkbEntries.add(ImmutableCkbEntry.builder().from(entry).variants(filteredVariants).build());
                }
            }
        }

        return filteredCkbEntries;
    }

    public void reportUnusedBlacklistEntries() {
        int unusedBlacklistEntryCount = 0;
        for (CkbBlacklistMolecularProfileEntry entry : blacklistEntries) {
            if (!usedBlacklistEntries.contains(entry)) {
                unusedBlacklistEntryCount++;
                LOGGER.warn(" Blacklist molecular profile entry '{}' hasn't been used for CKB blacklisting", entry);
            }
        }

        LOGGER.debug(" Found {} unused blacklisting entries during CKB blacklisting", unusedBlacklistEntryCount);
    }

    private boolean include(@NotNull EventType type, @NotNull Variant variant) {
        String gene = CkbEventAndGeneExtractor.extractGene(variant);
        String event = CkbEventAndGeneExtractor.extractEvent(variant);

        for (CkbBlacklistMolecularProfileEntry blacklistEntry : blacklistEntries) {
            boolean blacklistMatches = isMatch(blacklistEntry, type, gene, event, variant.fullName());
            if (blacklistMatches) {
                usedBlacklistEntries.add(blacklistEntry);
                return false;
            }
        }

        return true;
    }

    private boolean isMatch(@NotNull CkbBlacklistMolecularProfileEntry blacklistEntry, @NotNull EventType type, @NotNull String gene,
            @NotNull String event, @NotNull String fullName) {
        switch (blacklistEntry.type()) {
            case FILTER_EVENT_WITH_KEYWORD: {
                return event.contains(blacklistEntry.value());
            }
            case FILTER_ALL_EVIDENCE_ON_GENE: {
                return gene.equals(blacklistEntry.value());
            }
            case FILTER_EVIDENCE_FOR_EXONS_ON_GENE: {
                return gene.equals(blacklistEntry.value()) && type == EventType.EXON;
            }
            case ALLOW_GENE_IN_FUSIONS_EXCLUSIVELY: {
                return gene.equals(blacklistEntry.value()) && type != EventType.FUSION_PAIR && type != EventType.PROMISCUOUS_FUSION;
            }
            case FILTER_SECONDARY_GENE_WHEN_FUSION_LEG: {
                return type == EventType.FUSION_PAIR && !gene.equals(blacklistEntry.value()) && event.contains(blacklistEntry.value());
            }
            case FILTER_EXACT_VARIANT_FULLNAME: {
                return fullName.equals(blacklistEntry.value());
            }
            default: {
                LOGGER.warn("Blacklist entry found with unrecognized type: {}", blacklistEntry);
                return false;
            }
        }
    }
}

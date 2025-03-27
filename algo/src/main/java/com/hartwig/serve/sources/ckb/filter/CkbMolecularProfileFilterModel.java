package com.hartwig.serve.sources.ckb.filter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

public class CkbMolecularProfileFilterModel {

    private static final Logger LOGGER = LogManager.getLogger(CkbMolecularProfileFilterModel.class);

    @NotNull
    private final List<CkbMolecularProfileFilterEntry> filterEntries;
    @NotNull
    private final Set<CkbMolecularProfileFilterEntry> usedFilterEntries = Sets.newHashSet();

    public CkbMolecularProfileFilterModel(@NotNull final List<CkbMolecularProfileFilterEntry> filterEntries) {
        this.filterEntries = filterEntries;
    }

    @NotNull
    public List<CkbEntry> run(@NotNull List<CkbEntry> ckbEntries) {
        List<CkbEntry> cleanedCkbEntries = Lists.newArrayList();
        for (CkbEntry entry : ckbEntries) {
            if (entry.variants().isEmpty()) {
                // Always filter entries with no variants. Should never happen in practice!
                LOGGER.warn("Filtering '{}' because no variants have been defined for this entry!", entry);
            } else {
                Map<Boolean, List<Variant>> shouldIncludeVariants = entry.variants().stream()
                        .collect(Collectors.partitioningBy(variant -> include(entry.type(), variant)));

                if (shouldIncludeVariants.get(false).isEmpty()) {
                    cleanedCkbEntries.add(ImmutableCkbEntry.builder().from(entry).variants(shouldIncludeVariants.get(true)).build());
                } else {
                    LOGGER.debug("Filtering entry '{}' because some variants have been filtered out", entry);
                    shouldIncludeVariants.get(false).forEach(rejectedVariant -> LOGGER.debug("Filtered variant '{}'", rejectedVariant));
                }
            }
        }

        return cleanedCkbEntries;
    }

    public void reportUnusedFilterEntries() {
        int unusedFilterEntryCount = 0;
        for (CkbMolecularProfileFilterEntry entry : filterEntries) {
            if (!usedFilterEntries.contains(entry)) {
                unusedFilterEntryCount++;
                LOGGER.warn(" Molecular profile filter entry '{}' hasn't been used for CKB filtering", entry);
            }
        }

        LOGGER.debug(" Found {} unused filtering entries during CKB molecular profile filtering", unusedFilterEntryCount);
    }

    private boolean include(@NotNull EventType type, @NotNull Variant variant) {
        String gene = CkbEventAndGeneExtractor.extractGene(variant);
        String event = CkbEventAndGeneExtractor.extractEvent(variant);

        for (CkbMolecularProfileFilterEntry filterEntry : filterEntries) {
            if (isMatch(filterEntry, type, gene, event, variant.fullName())) {
                usedFilterEntries.add(filterEntry);
                return false;
            }
        }

        return true;
    }

    private boolean isMatch(@NotNull CkbMolecularProfileFilterEntry filterEntry, @NotNull EventType type, @NotNull String gene,
            @NotNull String event, @NotNull String fullName) {
        switch (filterEntry.type()) {
            case FILTER_EVENT_WITH_KEYWORD: {
                return event.contains(filterEntry.value());
            }
            case FILTER_ALL_EVIDENCE_ON_GENE: {
                return gene.equals(filterEntry.value());
            }
            case FILTER_EVIDENCE_FOR_EXONS_ON_GENE: {
                return gene.equals(filterEntry.value()) && type == EventType.EXON;
            }
            case ALLOW_GENE_IN_FUSIONS_EXCLUSIVELY: {
                return gene.equals(filterEntry.value()) && type != EventType.FUSION_PAIR && type != EventType.PROMISCUOUS_FUSION;
            }
            case FILTER_SECONDARY_GENE_WHEN_FUSION_LEG: {
                return type == EventType.FUSION_PAIR && !gene.equals(filterEntry.value()) && event.contains(filterEntry.value());
            }
            case FILTER_EXACT_VARIANT_FULLNAME: {
                return fullName.equals(filterEntry.value());
            }
            default: {
                LOGGER.warn("Molecular profile filter entry found with unrecognized type: {}", filterEntry);
                return false;
            }
        }
    }
}

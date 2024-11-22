package com.hartwig.serve.sources.docm.curation;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.extraction.util.KeyFormatter;
import com.hartwig.serve.sources.docm.DocmEntry;
import com.hartwig.serve.sources.docm.ImmutableDocmEntry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class DocmCurator {

    private static final Logger LOGGER = LogManager.getLogger(DocmCurator.class);

    @NotNull
    private final Set<CurationKey> evaluatedCurationKeys = Sets.newHashSet();

    @NotNull
    public List<DocmEntry> curate(@NotNull List<DocmEntry> entries) {
        List<DocmEntry> curatedEntries = Lists.newArrayList();
        for (DocmEntry entry : entries) {
            CurationKey key = new CurationKey(entry.gene(), entry.transcript(), entry.proteinAnnotation());
            evaluatedCurationKeys.add(key);
            if (CurationFactory.ENTRY_FILTERS.contains(key)) {
                LOGGER.debug("Removing DocmEntry '{}' because of filtering curation.",
                        KeyFormatter.toProteinKey(entry.gene(), entry.transcript(), entry.proteinAnnotation()));
            } else if (CurationFactory.GENE_MAPPINGS.containsKey(entry.gene())) {
                String mappedGene = CurationFactory.GENE_MAPPINGS.get(entry.gene());
                LOGGER.debug("Mapping gene '{}' to '{}'", entry.gene(), mappedGene);
                curatedEntries.add(ImmutableDocmEntry.builder().from(entry).gene(mappedGene).build());
            } else {
                curatedEntries.add(entry);
            }
        }
        return curatedEntries;
    }

    public void reportUnusedFilterEntries() {
        int unusedKeys = 0;
        for (CurationKey key : CurationFactory.ENTRY_FILTERS) {
            if (!evaluatedCurationKeys.contains(key)) {
                unusedKeys++;
                LOGGER.warn("Key '{}' hasn't been used during DoCM filtering", key);
            }
        }

        LOGGER.debug("Found {} unused DoCM filter entries. {} keys have been requested against {} filter entries",
                unusedKeys,
                evaluatedCurationKeys.size(),
                CurationFactory.ENTRY_FILTERS.size());
    }
}

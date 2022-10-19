package com.hartwig.serve.sources.ckb.curation;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableGene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableVariant;
import com.hartwig.serve.ckb.datamodel.variant.Variant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbCurator {

    private static final Logger LOGGER = LogManager.getLogger(CkbCurator.class);

    @NotNull
    private final Set<CurationEntry> evaluatedCurationEntries = Sets.newHashSet();

    public CkbCurator() {
    }

    @NotNull
    public List<CkbEntry> run(@NotNull List<CkbEntry> ckbEntries) {
        List<CkbEntry> curatedCkbEntries = Lists.newArrayList();

        for (CkbEntry ckbEntry : ckbEntries) {
            List<Variant> curatedVariants = Lists.newArrayList();
            for (Variant variant : ckbEntry.variants()) {
                curatedVariants.add(curate(variant));
            }
            curatedCkbEntries.add(ImmutableCkbEntry.builder().from(ckbEntry).variants(curatedVariants).build());
        }

        return curatedCkbEntries;
    }

    public void reportUnusedCurationEntries() {
        int unusedEntryCount = 0;
        for (CurationEntry entry : CurationFactory.VARIANT_MAPPINGS.keySet()) {
            if (!evaluatedCurationEntries.contains(entry)) {
                unusedEntryCount++;
                LOGGER.warn(" Entry '{}' hasn't been used during CKB curation", entry);
            }
        }

        LOGGER.debug(" Found {} unused CKB curation entries. {} keys have been requested against {} curation entries",
                unusedEntryCount,
                evaluatedCurationEntries.size(),
                CurationFactory.VARIANT_MAPPINGS.size());
    }

    @NotNull
    private Variant curate(@NotNull Variant variant) {
        String geneSymbol = variant.gene() != null ? variant.gene().geneSymbol() : null;
        if (geneSymbol == null) {
            LOGGER.debug("No gene symbol known, skipping curation on '{}'", variant);
            return variant;
        }

        CurationEntry entry = new CurationEntry(geneSymbol, variant.variant());
        evaluatedCurationEntries.add(entry);

        Variant curatedVariant = variant;
        if (CurationFactory.VARIANT_MAPPINGS.containsKey(entry)) {
            String mappedVariant = CurationFactory.VARIANT_MAPPINGS.get(entry).variant();
            String mappedGeneSymbol = CurationFactory.VARIANT_MAPPINGS.get(entry).geneSymbol();

            LOGGER.debug("Mapping variant '{}' on '{}' to '{}' on '{}'",
                    entry.variant(),
                    entry.geneSymbol(),
                    mappedVariant,
                    mappedGeneSymbol);

            curatedVariant = ImmutableVariant.builder()
                    .from(curatedVariant)
                    .gene(ImmutableGene.builder().from(variant.gene()).geneSymbol(mappedGeneSymbol).build())
                    .variant(mappedVariant)
                    .build();
        }

        return curatedVariant;
    }
}

package com.hartwig.serve.sources.hartwig.gene;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.util.ProgressTracker;

import org.jetbrains.annotations.NotNull;

public class HartwigGeneExtractor {

    @NotNull
    private final Knowledgebase source;

    public HartwigGeneExtractor(@NotNull final Knowledgebase source) {
        this.source = source;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<HartwigGeneEntry> entries) {
        Set<KnownGene> knownGenes = Sets.newHashSet();
        ProgressTracker tracker = new ProgressTracker("Hartwig genes", entries.size());
        for (HartwigGeneEntry entry : entries) {
            knownGenes.add(ImmutableKnownGene.builder().gene(entry.gene()).geneRole(GeneRole.UNKNOWN).addSources(source).build());
            tracker.update();
        }

        return ImmutableExtractionResult.builder().refGenomeVersion(source.refGenomeVersion()).knownGenes(knownGenes).build();
    }
}

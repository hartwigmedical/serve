package com.hartwig.serve.extraction.gene;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

import java.util.Set;

import com.hartwig.serve.datamodel.molecular.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.molecular.gene.KnownGene;

import org.jetbrains.annotations.NotNull;

public final class GeneConsolidation {

    private GeneConsolidation() {
    }

    @NotNull
    public static Set<KnownGene> consolidate(@NotNull Iterable<KnownGene> genes) {
        return stream(genes.spliterator(), false).collect(groupingBy(gene -> ImmutableKnownGene.copyOf(gene).withSources(emptySet())))
                .entrySet()
                .stream()
                .map(entry -> ImmutableKnownGene.copyOf(entry.getKey())
                        .withSources(entry.getValue().stream().flatMap(gene -> gene.sources().stream()).collect(toList())))
                .collect(toSet());
    }
}
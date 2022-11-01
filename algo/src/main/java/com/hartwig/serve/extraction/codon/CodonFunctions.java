package com.hartwig.serve.extraction.codon;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.KnownCodon;

import org.jetbrains.annotations.NotNull;

public final class CodonFunctions {

    private CodonFunctions() {
    }

    @NotNull
    public static Set<KnownCodon> consolidate(@NotNull Iterable<KnownCodon> codons) {
        Map<KnownCodon, Set<Knowledgebase>> sourcesPerAnnotation = Maps.newHashMap();
        for (KnownCodon codon : codons) {
            KnownCodon key = createKey(codon);
            Set<Knowledgebase> sources = sourcesPerAnnotation.get(key);
            if (sources == null) {
                sources = Sets.newHashSet();
            }
            sources.addAll(codon.sources());
            sourcesPerAnnotation.put(key, sources);
        }

        Set<KnownCodon> consolidated = Sets.newHashSet();
        for (Map.Entry<KnownCodon, Set<Knowledgebase>> entry : sourcesPerAnnotation.entrySet()) {
            consolidated.add(ImmutableKnownCodon.builder().from(entry.getKey()).sources(entry.getValue()).build());
        }
        return consolidated;
    }

    @NotNull
    private static KnownCodon createKey(@NotNull KnownCodon codon) {
        return ImmutableKnownCodon.builder().from(codon).sources(Sets.newHashSet()).build();
    }
}

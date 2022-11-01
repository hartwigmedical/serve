package com.hartwig.serve.extraction.exon;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownExon;

import org.jetbrains.annotations.NotNull;

public final class ExonFunctions {

    private ExonFunctions() {
    }

    @NotNull
    public static Set<KnownExon> consolidate(@NotNull Iterable<KnownExon> exons) {
        Map<KnownExon, Set<Knowledgebase>> sourcesPerAnnotation = Maps.newHashMap();
        for (KnownExon exon : exons) {
            KnownExon key = createKey(exon);
            Set<Knowledgebase> sources = sourcesPerAnnotation.get(key);
            if (sources == null) {
                sources = Sets.newHashSet();
            }
            sources.addAll(exon.sources());
            sourcesPerAnnotation.put(key, sources);
        }

        Set<KnownExon> consolidated = Sets.newHashSet();
        for (Map.Entry<KnownExon, Set<Knowledgebase>> entry : sourcesPerAnnotation.entrySet()) {
            consolidated.add(ImmutableKnownExon.builder().from(entry.getKey()).sources(entry.getValue()).build());
        }
        return consolidated;
    }

    @NotNull
    private static KnownExon createKey(@NotNull KnownExon exon) {
        return ImmutableKnownExon.builder().from(exon).sources(Sets.newHashSet()).build();
    }
}

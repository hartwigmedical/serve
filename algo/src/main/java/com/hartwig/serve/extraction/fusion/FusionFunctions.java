package com.hartwig.serve.extraction.fusion;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;

import org.jetbrains.annotations.NotNull;

public final class FusionFunctions {

    private FusionFunctions() {
    }

    @NotNull
    public static Set<KnownFusion> consolidate(@NotNull Iterable<KnownFusion> fusions) {
        Map<KnownFusion, Set<Knowledgebase>> sourcesPerFusion = Maps.newHashMap();
        for (KnownFusion fusion : fusions) {
            KnownFusion key = stripSources(fusion);
            Set<Knowledgebase> sources = sourcesPerFusion.get(key);
            if (sources == null) {
                sources = Sets.newHashSet();
            }
            sources.addAll(fusion.sources());
            sourcesPerFusion.put(key, sources);
        }

        Set<KnownFusion> consolidated = Sets.newHashSet();
        for (Map.Entry<KnownFusion, Set<Knowledgebase>> entry : sourcesPerFusion.entrySet()) {
            consolidated.add(ImmutableKnownFusion.builder().from(entry.getKey()).sources(entry.getValue()).build());
        }
        return consolidated;
    }

    @NotNull
    private static KnownFusion stripSources(@NotNull KnownFusion fusion) {
        return ImmutableKnownFusion.builder().from(fusion).sources(Sets.newHashSet()).build();
    }
}

package com.hartwig.serve.actionability;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.range.ActionableExon;
import com.hartwig.serve.datamodel.range.ImmutableActionableExon;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ActionableExonUrlConsolidator implements UrlConsolidator<ActionableExon> {

    @NotNull
    @Override
    public ActionableExon stripUrls(@NotNull final ActionableExon instance) {
        return ImmutableActionableExon.builder().from(instance).evidenceUrls(Sets.newHashSet()).build();
    }

    @NotNull
    @Override
    public ActionableExon buildWithUrls(@NotNull final ActionableExon instance, @NotNull final Set<String> urls) {
        return ImmutableActionableExon.builder().from(instance).evidenceUrls(urls).build();
    }
}

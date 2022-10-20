package com.hartwig.serve.actionability;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.actionability.range.ActionableRange;
import com.hartwig.serve.datamodel.actionability.range.ImmutableActionableRange;

import org.jetbrains.annotations.NotNull;

public class ActionableRangeUrlConsolidator implements UrlConsolidator<ActionableRange> {

    @NotNull
    @Override
    public ActionableRange stripUrls(@NotNull final ActionableRange instance) {
        return ImmutableActionableRange.builder().from(instance).evidenceUrls(Sets.newHashSet()).build();
    }

    @NotNull
    @Override
    public ActionableRange buildWithUrls(@NotNull final ActionableRange instance, @NotNull final Set<String> urls) {
        return ImmutableActionableRange.builder().from(instance).evidenceUrls(urls).build();
    }
}

package com.hartwig.serve.actionability;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ActionableRangeUrlConsolidator implements UrlConsolidator<ActionableRange> {

    @NotNull
    @Override
    public ActionableRange stripUrls(@NotNull final ActionableRange instance) {
        return buildWithUrls(instance, Sets.newHashSet());
    }

    @NotNull
    @Override
    public ActionableRange buildWithUrls(@NotNull final ActionableRange instance, @NotNull final Set<String> urls) {
        return ImmutableActionableRange.builder().from(instance).evidenceUrls(urls).build();
    }
}

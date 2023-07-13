package com.hartwig.serve.actionability;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.range.ActionableCodon;
import com.hartwig.serve.datamodel.range.ImmutableActionableCodon;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ActionableCodonUrlConsolidator implements UrlConsolidator<ActionableCodon>{

    @NotNull
    @Override
    public ActionableCodon stripUrls(@NotNull final ActionableCodon instance) {
        return ImmutableActionableCodon.builder().from(instance).evidenceUrls(Sets.newHashSet()).build();
    }

    @NotNull
    @Override
    public ActionableCodon buildWithUrls(@NotNull final ActionableCodon instance, @NotNull final Set<String> urls) {
        return ImmutableActionableCodon.builder().from(instance).evidenceUrls(urls).build();
    }
}

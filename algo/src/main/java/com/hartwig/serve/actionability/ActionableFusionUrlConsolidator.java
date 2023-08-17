package com.hartwig.serve.actionability;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;

import org.jetbrains.annotations.NotNull;

public class ActionableFusionUrlConsolidator implements UrlConsolidator<ActionableFusion> {

    @NotNull
    @Override
    public ActionableFusion stripUrls(@NotNull final ActionableFusion instance) {
        return buildWithUrls(instance, Sets.newHashSet());
    }

    @NotNull
    @Override
    public ActionableFusion buildWithUrls(@NotNull final ActionableFusion instance, @NotNull final Set<String> urls) {
        return ImmutableActionableFusion.builder().from(instance).evidenceUrls(urls).build();
    }
}

package com.hartwig.serve.actionability;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;

import org.jetbrains.annotations.NotNull;

public class ActionableHotspotUrlConsolidator implements UrlConsolidator<ActionableHotspot> {

    @NotNull
    @Override
    public ActionableHotspot stripUrls(@NotNull final ActionableHotspot instance) {
        return ImmutableActionableHotspot.builder().from(instance).evidenceUrls(Sets.newHashSet()).build();
    }

    @NotNull
    @Override
    public ActionableHotspot buildWithUrls(@NotNull final ActionableHotspot instance, @NotNull final Set<String> urls) {
        return ImmutableActionableHotspot.builder().from(instance).evidenceUrls(urls).build();
    }
}

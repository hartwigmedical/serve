package com.hartwig.serve.actionability;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;

import org.jetbrains.annotations.NotNull;

public class ActionableHLAUrlConsolidator implements UrlConsolidator<ActionableHLA> {

    @NotNull
    @Override
    public ActionableHLA stripUrls(@NotNull final ActionableHLA instance) {
        return ImmutableActionableHLA.builder().from(instance).evidenceUrls(Sets.newHashSet()).build();
    }

    @NotNull
    @Override
    public ActionableHLA buildWithUrls(@NotNull final ActionableHLA instance, @NotNull final Set<String> urls) {
        return ImmutableActionableHLA.builder().from(instance).evidenceUrls(urls).build();
    }
}
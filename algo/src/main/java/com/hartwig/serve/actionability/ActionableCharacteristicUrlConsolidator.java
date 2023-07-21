package com.hartwig.serve.actionability;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;

import org.jetbrains.annotations.NotNull;

public class ActionableCharacteristicUrlConsolidator implements UrlConsolidator<ActionableCharacteristic> {

    @NotNull
    @Override
    public ActionableCharacteristic stripUrls(@NotNull final ActionableCharacteristic instance) {
        return buildWithUrls(instance, Sets.newHashSet());
    }

    @NotNull
    @Override
    public ActionableCharacteristic buildWithUrls(@NotNull final ActionableCharacteristic instance, @NotNull final Set<String> urls) {
        return ImmutableActionableCharacteristic.builder().from(instance).evidenceUrls(urls).build();
    }
}

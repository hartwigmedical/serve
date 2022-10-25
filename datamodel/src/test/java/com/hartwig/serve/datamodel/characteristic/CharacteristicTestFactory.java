package com.hartwig.serve.datamodel.characteristic;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;

public final class CharacteristicTestFactory {

    private CharacteristicTestFactory() {
    }

    @NotNull
    public static ActionableCharacteristic createTestActionableCharacteristicForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableCharacteristic.builder().from(createTestActionableCharacteristic()).source(source).build();
    }

    @NotNull
    public static ActionableCharacteristic createTestActionableCharacteristic() {
        return ImmutableActionableCharacteristic.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE)
                .build();
    }
}

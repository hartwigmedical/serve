package com.hartwig.serve.datamodel.molecular.characteristic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ActionableCharacteristicComparatorTest {

    @Test
    public void canSortActionableCharacteristics() {
        ActionableCharacteristic characteristic1 = CharacteristicTestFactory.actionableCharacteristicBuilder()
                .type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD)
                .sourceEvent("event1")
                .build();

        ActionableCharacteristic characteristic2 = CharacteristicTestFactory.actionableCharacteristicBuilder()
                .type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD)
                .sourceEvent("event2")
                .build();

        ActionableCharacteristic characteristic3 = CharacteristicTestFactory.actionableCharacteristicBuilder()
                .type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE)
                .sourceEvent("event3")
                .build();

        List<ActionableCharacteristic> characteristics = new ArrayList<>(List.of(characteristic3, characteristic1, characteristic2));
        characteristics.sort(new ActionableCharacteristicComparator());

        assertEquals(characteristic1, characteristics.get(0));
        assertEquals(characteristic2, characteristics.get(1));
        assertEquals(characteristic3, characteristics.get(2));
    }
}
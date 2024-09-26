package com.hartwig.serve.datamodel.characteristic;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.junit.Test;

public class ActionableCharacteristicComparatorTest {

    @Test
    public void canSortActionableCharacteristics() {
        ActionableCharacteristic characteristic1 = CharacteristicTestFactory.actionableCharacteristicBuilder()
                .type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD)
                .evidenceLevel(EvidenceLevel.A)
                .build();

        ActionableCharacteristic characteristic2 = CharacteristicTestFactory.actionableCharacteristicBuilder()
                .type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD)
                .evidenceLevel(EvidenceLevel.B)
                .build();

        ActionableCharacteristic characteristic3 = CharacteristicTestFactory.actionableCharacteristicBuilder()
                .type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE)
                .evidenceLevel(EvidenceLevel.A)
                .build();

        List<ActionableCharacteristic> characteristics = Lists.newArrayList(characteristic3, characteristic1, characteristic2);
        characteristics.sort(new ActionableCharacteristicComparator());

        assertEquals(characteristic1, characteristics.get(0));
        assertEquals(characteristic2, characteristics.get(1));
        assertEquals(characteristic3, characteristics.get(2));
    }
}
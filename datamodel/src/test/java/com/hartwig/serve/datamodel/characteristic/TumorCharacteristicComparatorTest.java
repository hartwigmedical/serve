package com.hartwig.serve.datamodel.characteristic;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class TumorCharacteristicComparatorTest {

    @Test
    public void canSortTumorCharacteristics() {
        TumorCharacteristic characteristic1 =
                CharacteristicTestFactory.createTumorCharacteristic(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN,
                        TumorCharacteristicCutoffType.LOWER,
                        200D);
        TumorCharacteristic characteristic2 =
                CharacteristicTestFactory.createTumorCharacteristic(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN,
                        TumorCharacteristicCutoffType.GREATER,
                        400D);
        TumorCharacteristic characteristic3 =
                CharacteristicTestFactory.createTumorCharacteristic(TumorCharacteristicType.MICROSATELLITE_UNSTABLE,
                        TumorCharacteristicCutoffType.GREATER,
                        4D);
        TumorCharacteristic characteristic4 =
                CharacteristicTestFactory.createTumorCharacteristic(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, null, null);

        List<TumorCharacteristic> characteristics = Lists.newArrayList(characteristic1, characteristic3, characteristic4, characteristic2);
        characteristics.sort(new TumorCharacteristicComparator());

        assertEquals(characteristic1, characteristics.get(0));
        assertEquals(characteristic2, characteristics.get(1));
        assertEquals(characteristic3, characteristics.get(2));
        assertEquals(characteristic4, characteristics.get(3));
    }
}
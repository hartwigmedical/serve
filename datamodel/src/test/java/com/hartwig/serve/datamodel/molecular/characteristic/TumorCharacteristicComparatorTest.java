package com.hartwig.serve.datamodel.molecular.characteristic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class TumorCharacteristicComparatorTest {

    @Test
    public void canSortTumorCharacteristics() {
        TumorCharacteristic characteristic1 =
                create(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN, TumorCharacteristicCutoffType.LOWER, 200D);
        TumorCharacteristic characteristic2 =
                create(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN, TumorCharacteristicCutoffType.GREATER, 400D);
        TumorCharacteristic characteristic3 =
                create(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, TumorCharacteristicCutoffType.GREATER, 4D);
        TumorCharacteristic characteristic4 = create(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, null, null);

        List<TumorCharacteristic> characteristics = new ArrayList<>(
            List.of(characteristic1, characteristic3, characteristic4, characteristic2)
        );
        characteristics.sort(new TumorCharacteristicComparator());

        assertEquals(characteristic1, characteristics.get(0));
        assertEquals(characteristic2, characteristics.get(1));
        assertEquals(characteristic3, characteristics.get(2));
        assertEquals(characteristic4, characteristics.get(3));
    }

    @NotNull
    private static TumorCharacteristic create(@NotNull TumorCharacteristicType type, @Nullable TumorCharacteristicCutoffType cutoffType,
            @Nullable Double cutoff) {
        return CharacteristicTestFactory.createTumorCharacteristic(type, cutoffType, cutoff);
    }
}
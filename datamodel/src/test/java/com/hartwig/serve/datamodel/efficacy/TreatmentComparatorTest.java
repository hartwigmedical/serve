package com.hartwig.serve.datamodel.efficacy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TreatmentComparatorTest {

    @Test
    public void canSortTreatments() {
        Treatment treatment1 = ImmutableTreatment.builder().name("treatment 1").build();
        Treatment treatment2 = ImmutableTreatment.builder().name("treatment 2").build();

        List<Treatment> treatments = new ArrayList<>(List.of(treatment2, treatment1));
        treatments.sort(new TreatmentComparator());

        assertEquals(treatment1, treatments.get(0));
        assertEquals(treatment2, treatments.get(1));
    }
}
package com.hartwig.serve.datamodel.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IndicationComparatorTest {

    @Test
    public void canSortIndications() {
        Indication indication1 = DatamodelTestFactory.createTestIndication("cancer 1", "sub type 1");
        Indication indication2 = DatamodelTestFactory.createTestIndication("cancer 1", "sub type 2");
        Indication indication3 = DatamodelTestFactory.createTestIndication("cancer 2", "sub type 1");

        List<Indication> indications = new ArrayList<>(List.of(indication3, indication1, indication2));
        indications.sort(new IndicationComparator());

        assertEquals(indication1, indications.get(0));
        assertEquals(indication2, indications.get(1));
        assertEquals(indication3, indications.get(2));
    }
}
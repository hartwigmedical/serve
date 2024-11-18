package com.hartwig.serve.datamodel.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class CancerTypeComparatorTest {

    @Test
    public void canSortCancerTypes() {
        CancerType cancerType1 = DatamodelTestFactory.cancerTypeBuilder().name("cancer 1").doid("doid 1").build();
        CancerType cancerType2 = DatamodelTestFactory.cancerTypeBuilder().name("cancer 1").doid("doid 2").build();
        CancerType cancerType3 = DatamodelTestFactory.cancerTypeBuilder().name("cancer 2").doid("doid 1").build();

        List<CancerType> cancerTypes = Lists.newArrayList(cancerType2, cancerType1, cancerType3);
        cancerTypes.sort(new CancerTypeComparator());

        assertEquals(cancerType1, cancerTypes.get(0));
        assertEquals(cancerType2, cancerTypes.get(1));
        assertEquals(cancerType3, cancerTypes.get(2));
    }
}
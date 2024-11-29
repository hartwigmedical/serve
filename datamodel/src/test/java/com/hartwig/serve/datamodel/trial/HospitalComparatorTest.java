package com.hartwig.serve.datamodel.trial;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class HospitalComparatorTest {

    @Test
    public void canSortHospitals() {
        Hospital hospital1 = TrialTestFactory.hospitalBuilder().name("hospital 1").isChildrensHospital(true).build();
        Hospital hospital2 = TrialTestFactory.hospitalBuilder().name("hospital 1").isChildrensHospital(null).build();
        Hospital hospital3 = TrialTestFactory.hospitalBuilder().name("hospital 2").isChildrensHospital(true).build();

        List<Hospital> hospitals = new ArrayList<>(List.of(hospital3, hospital1, hospital2));
        hospitals.sort(new HospitalComparator());

        assertEquals(hospital1, hospitals.get(0));
        assertEquals(hospital2, hospitals.get(1));
        assertEquals(hospital3, hospitals.get(2));
    }
}
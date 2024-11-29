package com.hartwig.serve.datamodel.trial;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CountryComparatorTest {

    @Test
    public void canCompareCountries() {
        Country country1 = TrialTestFactory.createTestCountry("country 1", "city 1");
        Country country2 = TrialTestFactory.createTestCountry("country 1", "city 2");
        Country country3 = TrialTestFactory.createTestCountry("country 2", "city 1");

        List<Country> countries = new ArrayList<>(List.of(country3, country1, country2));
        countries.sort(new CountryComparator());

        assertEquals(country1, countries.get(0));
        assertEquals(country2, countries.get(1));
        assertEquals(country3, countries.get(2));
    }
}
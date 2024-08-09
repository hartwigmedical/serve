package com.hartwig.serve.sources.ckb.facility_curation;

import static org.junit.Assert.assertEquals;

import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbFacilityCurationModelTest {

    private static final CkbFacilityCurationModel facilityModel = CkbFacilityModelTestFactory.createProperFacilityModel();

    @Test
    public void canCurateFacilityNameBasedOnName() {
        Location location1 = createLocation("Rotterdam", "Erasmus MC", "3062 PA");
        Location location2 = createLocation("Rotterdam", "Erasmus ziekenhuis in Rotterdam", "3062 PA");

        assertEquals(facilityModel.curateFacilityName(location1), "EMC");
        assertEquals(facilityModel.curateFacilityName(location2), "EMC");
    }

    @Test
    public void canCurateFacilityNameBasedOnZip() {
        Location locationWithZip1 = createLocation("Groningen", "Research site", "9713 GZ");
        Location locationWithZip2 = createLocation("Groningen", "Research site", "9713");
        Location locationWithoutZip = createLocation("Almere", "Flevo location 2", null);

        assertEquals(facilityModel.curateFacilityName(locationWithZip1), "UMCG");
        assertEquals(facilityModel.curateFacilityName(locationWithZip2), "UMCG");
        assertEquals(facilityModel.curateFacilityName(locationWithoutZip), "Flevoziekenhuis");
    }

    @Test
    public void canCurateFacilityNameBasedOnFilter() {
        Location location = createLocation("Amsterdam", "Amsterdam UMC", null);

        assertEquals(facilityModel.curateFacilityName(location), "Amsterdam UMC");
    }

    @NotNull
    private static Location createLocation(@NotNull String city, @NotNull String facility, @Nullable String zip) {
        return ImmutableLocation.builder().nctId("").city(city).country("Netherlands").facility(facility).zip(zip).build();
    }
}

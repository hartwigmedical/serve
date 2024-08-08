package com.hartwig.serve.sources.ckb.facility;

import static org.junit.Assert.assertEquals;

import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbFacilityModelTest {

    private static final CkbFacilityModel facilityModel = CkbFacilityModelTestFactory.createProperFacilityModel();

    @Test
    public void canCurateFacilityNameBasedOnCity() {
        Location location = createLocation("Almere", "Flevo location 2", null);

        assertEquals(facilityModel.curateFacilityName(location), "Flevoziekenhuis");
    }

    @Test
    public void canCurateFacilityNameBasedOnName() {
        Location location = createLocation("Rotterdam", "Erasmus MC", "3062 PA");

        assertEquals(facilityModel.curateFacilityName(location), "EMC");
    }

    @Test
    public void canCurateFacilityNameBasedOnZip() {
        Location location = createLocation("Groningen", "Research site", "9713 GZ");

        assertEquals(facilityModel.curateFacilityName(location), "UMCG");
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

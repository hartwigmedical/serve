package com.hartwig.serve.sources.ckb.curation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.sources.ckb.CkbTestFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbCuratorTest {
    CkbCurator curator = CkbCuratorTestFactory.createProperModel();

    @Test
    public void canCurateVariants() {

        CkbVariantCurationEntry firstCurationKey = CkbVariantCurationFactory.VARIANT_MAPPINGS.keySet().iterator().next();
        CkbEntry entry = CkbTestFactory.createEntryWithGeneAndVariant(firstCurationKey.geneSymbol(), firstCurationKey.variant());

        List<CkbEntry> entries = curator.run(Lists.newArrayList(entry));

        Variant firstVariant = entries.get(0).variants().get(0);

        CkbVariantCurationEntry firstCuratedValue = CkbVariantCurationFactory.VARIANT_MAPPINGS.get(firstCurationKey);
        assertEquals(firstCuratedValue.geneSymbol(), firstVariant.gene().geneSymbol());
        assertEquals(firstCuratedValue.variant(), firstVariant.variant());

        curator.reportUnusedCurationEntries();
    }

    @Test
    public void canCurateFacilityNameBasedOnName() {
        Location location1 = createLocation("Rotterdam", "Erasmus MC", "3062 PA");
        Location location2 = createLocation("Rotterdam", "Erasmus ziekenhuis in Rotterdam", "3062 PA");
        Location curatedLocation = createLocation("Rotterdam", "EMC", "3062 PA");

        assertEquals(curator.curateFacilityName(location1), curatedLocation);
        assertEquals(curator.curateFacilityName(location2), curatedLocation);
    }

    @Test
    public void canCurateFacilityNameBasedOnZip() {
        Location locationWithZip1 = createLocation("Groningen", "Research site", "9713 GZ");
        Location locationWithZip2 = createLocation("Groningen", "Research site", "9713");
        Location locationWithoutZip = createLocation("Almere", "Flevo location 2", null);
        Location curatedLocationWithZip1 = createLocation("Groningen", "UMCG", "9713 GZ");
        Location curatedLocationWithZip2 = createLocation("Groningen", "UMCG", "9713");
        Location curatedLocationWithoutZip = createLocation("Almere", "Flevoziekenhuis", null);

        assertEquals(curator.curateFacilityName(locationWithZip1), curatedLocationWithZip1);
        assertEquals(curator.curateFacilityName(locationWithZip2), curatedLocationWithZip2);
        assertEquals(curator.curateFacilityName(locationWithoutZip), curatedLocationWithoutZip);
    }

    @Test
    public void canCurateFacilityNameBasedOnFilter() {
        Location location = createLocation("Amsterdam", "Amsterdam UMC", null);

        assertEquals(curator.curateFacilityName(location), location);
    }

    @Test
    public void canFindEqualStringsOrNull() {
        String string1 = null;
        String string2 = "";
        assertTrue(curator.equalStringsOrNull(string1, string2));

        String string3 = "same string";
        String string4 = "same string";
        assertTrue(curator.equalStringsOrNull(string3, string4));

        String string5 = null;
        String string6 = "string";
        assertFalse(curator.equalStringsOrNull(string5, string6));

        String string7 = "string";
        String string8 = "another string";
        assertFalse(curator.equalStringsOrNull(string7, string8));
    }

    @Test
    public void canFindStandAloneWordInString() {
        String string1 = "amc";

        String string2 = "amsterdam";
        assertFalse(curator.containsWord(string1, string2));

        String string4 = "amc amsterdam";
        assertTrue(curator.containsWord(string1, string4));

        String string6 = "amcterdam";
        assertFalse(curator.containsWord(string1, string6));
    }

    @NotNull
    private static Location createLocation(@NotNull String city, @NotNull String facility, @Nullable String zip) {
        return ImmutableLocation.builder().nctId("").city(city).country("Netherlands").facility(facility).zip(zip).build();
    }
}
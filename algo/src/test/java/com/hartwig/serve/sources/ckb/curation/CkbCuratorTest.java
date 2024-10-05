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

    private final CkbCurator curator = CkbCuratorTestFactory.createProperModel();

    @Test
    public void canCurateVariants() {
        CkbVariantCurationEntry firstCurationKey = CkbVariantCurationFactory.VARIANT_MAPPINGS.keySet().iterator().next();
        CkbEntry entry = CkbTestFactory.createEntryWithGeneAndVariant(firstCurationKey.geneSymbol(), firstCurationKey.variant());

        List<CkbEntry> entries = curator.run(Lists.newArrayList(entry));

        Variant firstVariant = entries.get(0).variants().get(0);

        CkbVariantCurationEntry firstCuratedValue = CkbVariantCurationFactory.VARIANT_MAPPINGS.get(firstCurationKey);
        assertEquals(firstCuratedValue.geneSymbol(), firstVariant.gene().geneSymbol());
        assertEquals(firstCuratedValue.variant(), firstVariant.variant());

        curator.reportUnusedVariantCurationEntries();
    }

    @Test
    public void canCurateFacilityNameBasedOnName() {
        Location location1 = createLocation("Rotterdam", "Erasmus MC", "3062 PA");
        Location location2 = createLocation("Rotterdam", "Erasmus ziekenhuis in Rotterdam", "3062 PA");
        Location expectedLocation = createLocation("Rotterdam", "EMC", "3062 PA");

        assertEquals(expectedLocation, curator.curateFacilityName(location1));
        assertEquals(expectedLocation, curator.curateFacilityName(location2));
    }

    @Test
    public void canCurateFacilityWithMissingCity() {
        Location missingCity = createLocation(null, "facility", null);
        assertEquals(missingCity, curator.curateFacilityName(missingCity));
    }

    @Test
    public void canCurateFacilityNameBasedOnZip() {
        Location locationWithZip1 = createLocation("Groningen", "Research site", "9713 GZ");
        Location locationWithZip2 = createLocation("Groningen", "Research site", "9713");
        Location locationWithoutZip = createLocation("Almere", "Flevo location 2", null);
        Location expectedLocationWithZip1 = createLocation("Groningen", "UMCG", "9713 GZ");
        Location expectedLocationWithZip2 = createLocation("Groningen", "UMCG", "9713");
        Location expectedLocationWithoutZip = createLocation("Almere", "Flevoziekenhuis", null);

        assertEquals(expectedLocationWithZip1, curator.curateFacilityName(locationWithZip1));
        assertEquals(expectedLocationWithZip2, curator.curateFacilityName(locationWithZip2));
        assertEquals(expectedLocationWithoutZip, curator.curateFacilityName(locationWithoutZip));
    }

    @Test
    public void canUseManualCuration() {
        Location location = createLocation("Amsterdam", "Amsterdam UMC", null);

        assertEquals(location, curator.curateFacilityName(location));
    }

    @Test
    public void canEvaluateEqualStringsOrNull() {
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
    public void canEvaluateStandAloneWordInString() {
        String string1 = "amc";

        String string2 = "amsterdam";
        assertFalse(curator.containsWord(string1, string2));

        String string4 = "amc amsterdam";
        assertTrue(curator.containsWord(string1, string4));

        String string6 = "amcterdam";
        assertFalse(curator.containsWord(string1, string6));
    }

    @NotNull
    private static Location createLocation(@Nullable String city, @NotNull String facility, @Nullable String zip) {
        return ImmutableLocation.builder().nctId("").city(city).country("Netherlands").facility(facility).zip(zip).build();
    }
}
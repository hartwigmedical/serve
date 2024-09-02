package com.hartwig.serve.datamodel.serialization.util;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ApprovalStatus;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.Country;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCountry;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.Treatment;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class ActionableFileUtilTest {

    @Test
    public void canConvertActionableTreatment() {
        ActionableEvent event = DatamodelTestFactory.createActionableEvent(Knowledgebase.VICC_CGI,
                LocalDate.EPOCH,
                "source event",
                Sets.newHashSet(),
                DatamodelTestFactory.interventionBuilder(false, true, "treatment1", null),
                DatamodelTestFactory.cancerTypeBuilder().name("applicable name").doid("applicable doid").build(),
                Sets.newHashSet(DatamodelTestFactory.cancerTypeBuilder().name("blacklist name").doid("blacklist doid").build()),
                Strings.EMPTY,
                EvidenceLevel.C,
                ApprovalStatus.GUIDELINE,
                EvidenceDirection.RESISTANT,
                Sets.newHashSet("url1", "url2")
        );

        String line = ActionableFileUtil.toLine(event);
        Map<String, Integer> fields = SerializationUtil.createFields(ActionableFileUtil.header(), ActionableFileUtil.FIELD_DELIMITER);
        ActionableEvent coveredEvent = ActionableFileUtil.fromLine(line.split(ActionableFileUtil.FIELD_DELIMITER), fields);

        Treatment treatmentCovered = DatamodelTestFactory.extractTreatment(coveredEvent);
        Treatment treatmentEvent = DatamodelTestFactory.extractTreatment(event);

        assertEquals(event.source(), coveredEvent.source());
        assertEquals(event.sourceEvent(), coveredEvent.sourceEvent());
        assertEquals(event.sourceUrls(), coveredEvent.sourceUrls());
        assertEquals(treatmentEvent, treatmentCovered);
        assertEquals(event.applicableCancerType(), coveredEvent.applicableCancerType());
        assertEquals(event.blacklistCancerTypes(), coveredEvent.blacklistCancerTypes());
        assertEquals(event.level(), coveredEvent.level());
        assertEquals(event.approvalStatus(), coveredEvent.approvalStatus());
        assertEquals(event.direction(), coveredEvent.direction());
        assertEquals(event.evidenceUrls(), coveredEvent.evidenceUrls());
    }

    @Test
    public void canConvertActionableTrial() {
        Country country = ImmutableCountry.builder()
                .countryName("Netherlands")
                .hospitalsPerCity(Map.of("Rotterdam", Set.of("EMC", "Ikazia"), "Nijmegen", Set.of("Radboud UMC", "CWZ")))
                .build();
        ActionableEvent event = DatamodelTestFactory.createActionableEvent(Knowledgebase.VICC_CGI,
                LocalDate.EPOCH,
                "source event",
                Sets.newHashSet(),
                DatamodelTestFactory.interventionBuilder(true, false, "treatment1", country),
                DatamodelTestFactory.cancerTypeBuilder().name("applicable name").doid("applicable doid").build(),
                Sets.newHashSet(DatamodelTestFactory.cancerTypeBuilder().name("blacklist name").doid("blacklist doid").build()),
                Strings.EMPTY,
                EvidenceLevel.C,
                ApprovalStatus.UNKNOWN,
                EvidenceDirection.RESISTANT,
                Sets.newHashSet("url1", "url2")
        );

        String line = ActionableFileUtil.toLine(event);
        Map<String, Integer> fields = SerializationUtil.createFields(ActionableFileUtil.header(), ActionableFileUtil.FIELD_DELIMITER);
        ActionableEvent coveredEvent = ActionableFileUtil.fromLine(line.split(ActionableFileUtil.FIELD_DELIMITER), fields);

        ClinicalTrial clinicalTrialCovered = DatamodelTestFactory.extractClinicalTrial(coveredEvent);
        ClinicalTrial clinicalTrialEvent = DatamodelTestFactory.extractClinicalTrial(event);

        assertEquals(event.source(), coveredEvent.source());
        assertEquals(event.sourceEvent(), coveredEvent.sourceEvent());
        assertEquals(event.sourceUrls(), coveredEvent.sourceUrls());
        assertEquals(clinicalTrialEvent, clinicalTrialCovered);
        assertEquals(event.applicableCancerType(), coveredEvent.applicableCancerType());
        assertEquals(event.blacklistCancerTypes(), coveredEvent.blacklistCancerTypes());
        assertEquals(event.level(), coveredEvent.level());
        assertEquals(event.approvalStatus(), coveredEvent.approvalStatus());
        assertEquals(event.direction(), coveredEvent.direction());
        assertEquals(event.evidenceUrls(), coveredEvent.evidenceUrls());
    }

    @Test
    public void canConvertCountriesToHospitals() {
        Set<Country> countries = Sets.newHashSet();
        countries.add(createCountry("Netherlands", "Groningen", "UMCG"));
        countries.add(createCountry("Belgium", "Brussel", "UZ Brussel"));
        assertEquals("Groningen(UMCG),Brussel(UZ Brussel)", ActionableFileUtil.countriesToHospitalsField(countries));
    }

    @Test
    public void canConvertCountriesToCountryNameAndCities() {
        Set<Country> countries = Sets.newHashSet();
        countries.add(createCountry("Netherlands", "Groningen", "UMCG"));
        countries.add(createCountry("Belgium", "Brussel", ""));
        assertEquals("Netherlands(Groningen),Belgium(Brussel)", ActionableFileUtil.countriesToCountryNameAndCitiesField(countries));
    }

    @Test
    public void canConvertSingleCancerTypeToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(createCancerType("Hematologic cancer", "2531"));
        assertEquals("Hematologic cancer;2531", ActionableFileUtil.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canConvertTwoCancerTypesToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(createCancerType("Hematologic cancer", "2531"));
        cancerTypes.add(createCancerType("Skin Melanoma", "8923"));

        assertEquals("Hematologic cancer;2531,Skin Melanoma;8923", ActionableFileUtil.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canConvertMultipleCancerTypesToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(createCancerType("Hematologic cancer", "2531"));
        cancerTypes.add(createCancerType("Skin Melanoma", "8923"));
        cancerTypes.add(createCancerType("Bladder Cancer", "11054"));
        cancerTypes.add(createCancerType("Colorectal Cancer", "1520"));

        assertEquals("Hematologic cancer;2531,Colorectal Cancer;1520,Skin Melanoma;8923,Bladder Cancer;11054",
                ActionableFileUtil.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canResolveCountries() {
        String countryNameAndCity = "Belgium(Brussel),Netherlands(Groningen)";
        String hospital = "Brussel(UZ Brussel),Groningen(UMCG)";

        Set<Country> countries = ActionableFileUtil.twoFieldsToCountries(countryNameAndCity, hospital);
        assertEquals(2, countries.size());
        Iterator<Country> iterator = countries.iterator();

        Country country1 = iterator.next();
        assertEquals("Netherlands", country1.countryName());
        assertEquals(Map.of("Groningen", Set.of("UMCG")), country1.hospitalsPerCity());

        Country country2 = iterator.next();
        assertEquals("Belgium", country2.countryName());
        assertEquals(Map.of("Brussel", Set.of("UZ Brussel")), country2.hospitalsPerCity());
    }

    @Test
    public void canResolveSingleCancerType() {
        String combinedNameAndDoid = "Hematologic cancer;2531";
        Set<CancerType> cancerTypes = ActionableFileUtil.fieldToCancerTypes(combinedNameAndDoid);

        assertEquals(1, cancerTypes.size());
        CancerType tumorLocationBlacklisting = cancerTypes.iterator().next();
        assertEquals("Hematologic cancer", tumorLocationBlacklisting.name());
        assertEquals("2531", tumorLocationBlacklisting.doid());
    }

    @Test
    public void canResolveTwoCancerTypes() {
        String combinedNamesAndDoids = "Hematologic cancer;2531,Skin Melanoma;8923";
        Set<CancerType> cancerTypes = ActionableFileUtil.fieldToCancerTypes(combinedNamesAndDoids);

        assertEquals(2, cancerTypes.size());
        CancerType cancerType1 = findCancerTypeByName(cancerTypes, "Hematologic cancer");
        assertEquals("2531", cancerType1.doid());

        CancerType cancerType2 = findCancerTypeByName(cancerTypes, "Skin Melanoma");
        assertEquals("8923", cancerType2.doid());
    }

    @Test
    public void canResolveMultipleCancerTypes() {
        String combinedNamesAndDoids = "Hematologic cancer;2531,Skin Melanoma;8923,Bladder Cancer;11054,Colorectal Cancer;1520";
        Set<CancerType> cancerTypes = ActionableFileUtil.fieldToCancerTypes(combinedNamesAndDoids);

        assertEquals(4, cancerTypes.size());
        CancerType cancerType1 = findCancerTypeByName(cancerTypes, "Hematologic cancer");
        assertEquals("2531", cancerType1.doid());

        CancerType cancerType2 = findCancerTypeByName(cancerTypes, "Colorectal Cancer");
        assertEquals("1520", cancerType2.doid());

        CancerType cancerType3 = findCancerTypeByName(cancerTypes, "Skin Melanoma");
        assertEquals("8923", cancerType3.doid());

        CancerType cancerType4 = findCancerTypeByName(cancerTypes, "Bladder Cancer");
        assertEquals("11054", cancerType4.doid());
    }

    @NotNull
    private static CancerType findCancerTypeByName(@NotNull Iterable<CancerType> cancerTypes, @NotNull String nameToFind) {
        for (CancerType cancerType : cancerTypes) {
            if (cancerType.name().equals(nameToFind)) {
                return cancerType;
            }
        }

        throw new IllegalStateException("Could not find cancerType with name: " + nameToFind);
    }

    @NotNull
    private static CancerType createCancerType(@NotNull String name, @NotNull String doid) {
        return DatamodelTestFactory.cancerTypeBuilder().name(name).doid(doid).build();
    }

    @NotNull
    private static Country createCountry(@NotNull String country, @NotNull String city, @Nullable String hospital) {
        return ImmutableCountry.builder()
                .countryName(country)
                .hospitalsPerCity(Map.of(city, Set.of(hospital)))
                .build();
    }
}
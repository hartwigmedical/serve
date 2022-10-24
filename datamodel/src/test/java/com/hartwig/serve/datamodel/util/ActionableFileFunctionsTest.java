package com.hartwig.serve.datamodel.util;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.DatamodelTestBuilders;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableFileFunctionsTest {

    @Test
    public void canConvertActionableEvents() {
        ActionableEvent event = DatamodelTestFactory.createEvent(Knowledgebase.VICC_CGI,
                "source event",
                Sets.newHashSet(),
                DatamodelTestBuilders.treatmentBuilder()
                        .treament("treatment")
                        .addSourceRelevantTreatmentApproaches("drug classes")
                        .addRelevantTreatmentApproaches("drug classes")
                        .build(),
                DatamodelTestBuilders.cancerTypeBuilder().name("applicable name").doid("applicable doid").build(),
                Sets.newHashSet(DatamodelTestBuilders.cancerTypeBuilder().name("blacklist name").doid("blacklist doid").build()),
                EvidenceLevel.C,
                EvidenceDirection.RESISTANT,
                Sets.newHashSet("url1", "url2"));

        String line = ActionableFileFunctions.toLine(event);
        ActionableEvent convertedEvent = ActionableFileFunctions.fromLine(line.split(ActionableFileFunctions.FIELD_DELIMITER), 0);

        assertEquals(Knowledgebase.VICC_CGI, convertedEvent.source());
        assertEquals("treatment", convertedEvent.treatment().treament());
        assertEquals("applicable name", convertedEvent.applicableCancerType().name());
        assertEquals("applicable doid", convertedEvent.applicableCancerType().doid());
        assertEquals(EvidenceLevel.C, convertedEvent.level());
        assertEquals(EvidenceDirection.RESISTANT, convertedEvent.direction());
        assertEquals(Sets.newHashSet("url1", "url2"), convertedEvent.evidenceUrls());
    }

    @Test
    public void canConvertSingleCancerTypeToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(create("Hematologic cancer", "2531"));
        assertEquals("Hematologic cancer;2531", ActionableFileFunctions.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canConvertTwoCancerTypesToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(create("Hematologic cancer", "2531"));
        cancerTypes.add(create("Skin Melanoma", "8923"));

        assertEquals("Hematologic cancer;2531,Skin Melanoma;8923", ActionableFileFunctions.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canConvertMultipleCancerTypesToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(create("Hematologic cancer", "2531"));
        cancerTypes.add(create("Skin Melanoma", "8923"));
        cancerTypes.add(create("Bladder Cancer", "11054"));
        cancerTypes.add(create("Colorectal Cancer", "1520"));

        assertEquals("Hematologic cancer;2531,Colorectal Cancer;1520,Skin Melanoma;8923,Bladder Cancer;11054",
                ActionableFileFunctions.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canResolveSingleCancerType() {
        String combinedNameAndDoid = "Hematologic cancer;2531";
        Set<CancerType> cancerTypes = ActionableFileFunctions.fieldToCancerTypes(combinedNameAndDoid);

        assertEquals(1, cancerTypes.size());
        CancerType tumorLocationBlacklisting = cancerTypes.iterator().next();
        assertEquals("Hematologic cancer", tumorLocationBlacklisting.name());
        assertEquals("2531", tumorLocationBlacklisting.doid());
    }

    @Test
    public void canResolveTwoCancerTypes() {
        String combinedNamesAndDoids = "Hematologic cancer;2531,Skin Melanoma;8923";
        Set<CancerType> cancerTypes = ActionableFileFunctions.fieldToCancerTypes(combinedNamesAndDoids);

        assertEquals(2, cancerTypes.size());
        CancerType cancerType1 = findByName(cancerTypes, "Hematologic cancer");
        assertEquals("2531", cancerType1.doid());

        CancerType cancerType2 = findByName(cancerTypes, "Skin Melanoma");
        assertEquals("8923", cancerType2.doid());
    }

    @Test
    public void canResolveMultipleCancerTypes() {
        String combinedNamesAndDoids = "Hematologic cancer;2531,Skin Melanoma;8923,Bladder Cancer;11054,Colorectal Cancer;1520";
        Set<CancerType> cancerTypes = ActionableFileFunctions.fieldToCancerTypes(combinedNamesAndDoids);

        assertEquals(4, cancerTypes.size());
        CancerType cancerType1 = findByName(cancerTypes, "Hematologic cancer");
        assertEquals("2531", cancerType1.doid());

        CancerType cancerType2 = findByName(cancerTypes, "Colorectal Cancer");
        assertEquals("1520", cancerType2.doid());

        CancerType cancerType3 = findByName(cancerTypes, "Skin Melanoma");
        assertEquals("8923", cancerType3.doid());

        CancerType cancerType4 = findByName(cancerTypes, "Bladder Cancer");
        assertEquals("11054", cancerType4.doid());
    }

    @NotNull
    private static CancerType findByName(@NotNull Iterable<CancerType> cancerTypes, @NotNull String nameToFind) {
        for (CancerType cancerType : cancerTypes) {
            if (cancerType.name().equals(nameToFind)) {
                return cancerType;
            }
        }

        throw new IllegalStateException("Could not find cancerType with name: " + nameToFind);
    }

    @NotNull
    private static CancerType create(@NotNull String name, @NotNull String doid) {
        return DatamodelTestBuilders.cancerTypeBuilder().name(name).doid(doid).build();
    }
}
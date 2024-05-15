package com.hartwig.serve.datamodel.serialization.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.Treatment;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableFileUtilTest {

    @Test
    public void canConvertActionableEvents() {
        ActionableEvent event = DatamodelTestFactory.createActionableEvent(Knowledgebase.VICC_CGI,
                "source event",
                Sets.newHashSet(),
                DatamodelTestFactory.interventionBuilder(false, true),
                DatamodelTestFactory.cancerTypeBuilder().name("applicable name").doid("applicable doid").build(),
                Sets.newHashSet(DatamodelTestFactory.cancerTypeBuilder().name("blacklist name").doid("blacklist doid").build()),
                EvidenceLevel.C,
                EvidenceDirection.RESISTANT,
                Sets.newHashSet("url1", "url2"));

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
        assertEquals(event.direction(), coveredEvent.direction());
        assertEquals(event.evidenceUrls(), coveredEvent.evidenceUrls());
    }

    @Test
    public void canConvertSingleCancerTypeToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(create("Hematologic cancer", "2531"));
        assertEquals("Hematologic cancer;2531", ActionableFileUtil.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canConvertTwoCancerTypesToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(create("Hematologic cancer", "2531"));
        cancerTypes.add(create("Skin Melanoma", "8923"));

        assertEquals("Hematologic cancer;2531,Skin Melanoma;8923", ActionableFileUtil.cancerTypesToField(cancerTypes));
    }

    @Test
    public void canConvertMultipleCancerTypesToField() {
        Set<CancerType> cancerTypes = Sets.newHashSet();
        cancerTypes.add(create("Hematologic cancer", "2531"));
        cancerTypes.add(create("Skin Melanoma", "8923"));
        cancerTypes.add(create("Bladder Cancer", "11054"));
        cancerTypes.add(create("Colorectal Cancer", "1520"));

        assertEquals("Hematologic cancer;2531,Colorectal Cancer;1520,Skin Melanoma;8923,Bladder Cancer;11054",
                ActionableFileUtil.cancerTypesToField(cancerTypes));
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
        CancerType cancerType1 = findByName(cancerTypes, "Hematologic cancer");
        assertEquals("2531", cancerType1.doid());

        CancerType cancerType2 = findByName(cancerTypes, "Skin Melanoma");
        assertEquals("8923", cancerType2.doid());
    }

    @Test
    public void canResolveMultipleCancerTypes() {
        String combinedNamesAndDoids = "Hematologic cancer;2531,Skin Melanoma;8923,Bladder Cancer;11054,Colorectal Cancer;1520";
        Set<CancerType> cancerTypes = ActionableFileUtil.fieldToCancerTypes(combinedNamesAndDoids);

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
        return DatamodelTestFactory.cancerTypeBuilder().name(name).doid(doid).build();
    }
}
package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.Treatment;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableCharacteristicFileTest {

    private static final String ACTIONABLE_CHARACTERISTIC_TSV =
            Resources.getResource("actionable/ActionableCharacteristics.37.tsv").getPath();

    private static final double EPSILON = 1.0E-10;

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableCharacteristic> characteristics = ActionableCharacteristicFile.read(ACTIONABLE_CHARACTERISTIC_TSV);

        assertCharacteristics(characteristics);

        Map<String, Integer> fields =
                SerializationUtil.createFields(ActionableCharacteristicFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableCharacteristic> regeneratedCharacteristics =
                ActionableCharacteristicFile.fromLines(ActionableCharacteristicFile.toLines(characteristics), fields);

        assertEquals(characteristics, regeneratedCharacteristics);
    }

    private static void assertCharacteristics(@NotNull List<ActionableCharacteristic> characteristics) {
        assertEquals(2, characteristics.size());

        ActionableCharacteristic characteristic1 = findBySource(characteristics, Knowledgebase.CKB_EVIDENCE);
        Treatment treatment1 = DatamodelTestFactory.extractTreatment(characteristic1);
        assertEquals(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, characteristic1.type());
        assertEquals(TumorCharacteristicCutoffType.EQUAL_OR_GREATER, characteristic1.cutoffType());
        assertEquals(4, characteristic1.cutoff(), EPSILON);
        assertEquals("msi", characteristic1.sourceEvent());
        assertTrue(characteristic1.sourceUrls().contains("http"));
        assertEquals("Nivolumab", treatment1.name());
        assertTrue(treatment1.treatmentApproachesDrugClass().isEmpty());
        assertTrue(treatment1.treatmentApproachesTherapy().isEmpty());
        assertEquals("All cancer types", characteristic1.applicableCancerType().name());
        assertEquals("X", characteristic1.applicableCancerType().doid());
        assertTrue(characteristic1.blacklistCancerTypes()
                .contains(DatamodelTestFactory.cancerTypeBuilder().name("Hematologic cancer").doid("2531").build()));
        assertEquals(EvidenceLevel.A, characteristic1.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristic1.direction());
        assertEquals(2, characteristic1.evidenceUrls().size());
        assertEquals(LocalDate.of(2021, 2, 3), characteristic1.date());
        assertEquals("efficacy evidence", characteristic1.description());

        ActionableCharacteristic characteristic2 = findBySource(characteristics, Knowledgebase.CKB_TRIAL);
        ClinicalTrial trial = DatamodelTestFactory.extractClinicalTrial(characteristic2);
        assertEquals(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, characteristic2.type());
        assertNull(characteristic2.cutoffType());
        assertNull(characteristic2.cutoff());
        assertEquals("msi", characteristic2.sourceEvent());
        assertTrue(characteristic2.sourceUrls().contains("http"));
        assertEquals("Immuno,Pembro", DatamodelTestFactory.setToField(trial.therapyNames()));
        assertEquals("All cancer types", characteristic2.applicableCancerType().name());
        assertEquals("X", characteristic2.applicableCancerType().doid());
        assertTrue(characteristic2.blacklistCancerTypes()
                .contains(DatamodelTestFactory.cancerTypeBuilder().name("Hematologic cancer").doid("2531").build()));
        assertEquals(EvidenceLevel.B, characteristic2.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristic2.direction());
        assertEquals(2, characteristic2.evidenceUrls().size());
        assertEquals(LocalDate.EPOCH, characteristic2.date());
        assertEquals(Strings.EMPTY, characteristic2.description());
    }

    @NotNull
    private static ActionableCharacteristic findBySource(@NotNull List<ActionableCharacteristic> characteristics,
            @NotNull Knowledgebase sourceToFind) {
        for (ActionableCharacteristic characteristic : characteristics) {
            if (characteristic.source() == sourceToFind) {
                return characteristic;
            }
        }

        throw new IllegalStateException("Could not find actionable characteristic with source: " + sourceToFind);
    }
}

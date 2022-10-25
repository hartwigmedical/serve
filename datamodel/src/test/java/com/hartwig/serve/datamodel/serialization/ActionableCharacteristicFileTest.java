package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableCharacteristicFileTest {

    private static final String ACTIONABLE_CHARACTERISTIC_TSV =
            Resources.getResource("actionability/ActionableCharacteristics.37.tsv").getPath();

    private static final double EPSILON = 1.0E-10;

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableCharacteristic> characteristics = ActionableCharacteristicFile.read(ACTIONABLE_CHARACTERISTIC_TSV);

        assertCharacteristics(characteristics);

        List<ActionableCharacteristic> regeneratedCharacteristics =
                ActionableCharacteristicFile.fromLines(ActionableCharacteristicFile.toLines(characteristics));

        assertEquals(characteristics, regeneratedCharacteristics);
    }

    private static void assertCharacteristics(@NotNull List<ActionableCharacteristic> characteristics) {
        assertEquals(2, characteristics.size());

        ActionableCharacteristic characteristic1 = findBySource(characteristics, Knowledgebase.VICC_CGI);
        assertEquals(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, characteristic1.type());
        assertEquals(TumorCharacteristicCutoffType.EQUAL_OR_GREATER, characteristic1.cutoffType());
        assertEquals(4, characteristic1.cutoff(), EPSILON);
        assertEquals("msi", characteristic1.sourceEvent());
        assertTrue(characteristic1.sourceUrls().contains("http"));
        assertEquals("Nivolumab", characteristic1.treatment().name());
        assertTrue(characteristic1.treatment().sourceRelevantTreatmentApproaches().isEmpty());
        assertTrue(characteristic1.treatment().relevantTreatmentApproaches().isEmpty());
        assertEquals("All cancer types", characteristic1.applicableCancerType().name());
        assertEquals("X", characteristic1.applicableCancerType().doid());
        assertTrue(characteristic1.blacklistCancerTypes()
                .contains(DatamodelTestFactory.cancerTypeBuilder().name("Hematologic cancer").doid("2531").build()));
        assertEquals(EvidenceLevel.A, characteristic1.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristic1.direction());
        assertEquals(2, characteristic1.evidenceUrls().size());

        ActionableCharacteristic characteristic2 = findBySource(characteristics, Knowledgebase.VICC_CIVIC);
        assertEquals(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, characteristic2.type());
        assertNull(characteristic2.cutoffType());
        assertNull(characteristic2.cutoff());
        assertEquals("msi", characteristic2.sourceEvent());
        assertTrue(characteristic2.sourceUrls().contains("http"));
        assertEquals("Pembro", characteristic2.treatment().name());
        assertTrue(characteristic2.treatment().sourceRelevantTreatmentApproaches().isEmpty());
        assertTrue(characteristic2.treatment().relevantTreatmentApproaches().isEmpty());
        assertEquals("All cancer types", characteristic2.applicableCancerType().name());
        assertEquals("X", characteristic2.applicableCancerType().doid());
        assertTrue(characteristic2.blacklistCancerTypes()
                .contains(DatamodelTestFactory.cancerTypeBuilder().name("Hematologic cancer").doid("2531").build()));
        assertEquals(EvidenceLevel.B, characteristic2.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristic2.direction());
        assertEquals(2, characteristic2.evidenceUrls().size());
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

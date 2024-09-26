package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableHLAFileTest {

    private static final String ACTIONABLE_HLA_TSV = Resources.getResource("actionable/ActionableHLA.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableHLA> hlas = ActionableHLAFile.read(ACTIONABLE_HLA_TSV);

        assertActionableHLAs(hlas);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableHLAFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableHLA> regeneratedHLAs = ActionableHLAFile.fromLines(ActionableHLAFile.toLines(hlas), fields);

        assertEquals(hlas, regeneratedHLAs);
    }

    private static void assertActionableHLAs(@NotNull List<ActionableHLA> hlas) {
        assertEquals(2, hlas.size());

        ActionableHLA hla1 = findBySource(hlas, Knowledgebase.CKB_EVIDENCE);
        assertEquals("A*02", hla1.hlaAllele());
        assertEquals("Nivolumab", DatamodelTestFactory.extractTreatment(hla1).name());
        assertEquals("All cancer types", hla1.applicableCancerType().name());
        assertEquals(EvidenceLevelDetails.GUIDELINE, hla1.evidenceLevelDetails());
        assertEquals(LocalDate.of(2021, 2, 3), hla1.entryDate());
        assertEquals("efficacy evidence", hla1.efficacyDescription());

        ActionableHLA hla2 = findBySource(hlas, Knowledgebase.CKB_TRIAL);
        assertEquals("A*02", hla2.hlaAllele());
        assertEquals("Nivolumab", DatamodelTestFactory.setToField(DatamodelTestFactory.extractClinicalTrial(hla2).therapyNames()));
        assertEquals("Skin melanoma", hla2.applicableCancerType().name());
        assertEquals(EvidenceLevelDetails.GUIDELINE, hla2.evidenceLevelDetails());
        assertEquals(LocalDate.EPOCH, hla2.entryDate());
        assertEquals(Strings.EMPTY, hla2.efficacyDescription());
    }

    @NotNull
    private static ActionableHLA findBySource(@NotNull List<ActionableHLA> hlas, @NotNull Knowledgebase sourceToFind) {
        for (ActionableHLA hla : hlas) {
            if (hla.source() == sourceToFind) {
                return hla;
            }
        }

        throw new IllegalStateException("Could not find actionable characteristic with source: " + sourceToFind);
    }
}
package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableFusionFileTest {

    private static final String ACTIONABLE_FUSION_TSV = Resources.getResource("actionable/ActionableFusions.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableFusion> fusions = ActionableFusionFile.read(ACTIONABLE_FUSION_TSV);

        assertActionableFusions(fusions);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableFusionFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableFusion> regeneratedFusions = ActionableFusionFile.fromLines(ActionableFusionFile.toLines(fusions), fields);

        assertEquals(fusions, regeneratedFusions);
    }

    private static void assertActionableFusions(@NotNull List<ActionableFusion> fusions) {
        assertEquals(3, fusions.size());

        ActionableFusion fusion1 = findByGeneUp(fusions, "EGFR");
        assertEquals(1, (int) fusion1.minExonUp());
        assertEquals(2, (int) fusion1.maxExonUp());
        assertEquals("EGFR", fusion1.geneDown());
        assertEquals(8, (int) fusion1.minExonDown());
        assertEquals(9, (int) fusion1.maxExonDown());
        assertEquals(EvidenceLevelDetails.GUIDELINE, fusion1.evidenceLevelDetails());
        assertEquals(LocalDate.of(2021, 2, 3), fusion1.date());
        assertEquals("efficacy evidence A", fusion1.description());

        ActionableFusion fusion2 = findByGeneUp(fusions, "EML4");
        assertNull(fusion2.minExonUp());
        assertNull(fusion2.maxExonUp());
        assertEquals("ALK", fusion2.geneDown());
        assertNull(fusion2.minExonDown());
        assertNull(fusion2.maxExonDown());
        assertEquals(EvidenceLevelDetails.GUIDELINE, fusion2.evidenceLevelDetails());
        assertEquals(LocalDate.of(2021, 2, 4), fusion2.date());
        assertEquals("efficacy evidence B", fusion2.description());
    }

    @NotNull
    private static ActionableFusion findByGeneUp(@NotNull List<ActionableFusion> fusions, @NotNull String geneUpToFind) {
        for (ActionableFusion fusion : fusions) {
            if (fusion.geneUp().equals(geneUpToFind)) {
                return fusion;
            }
        }

        throw new IllegalStateException("Could not find fusion with geneUp:" + geneUpToFind);
    }
}
package com.hartwig.serve.datamodel.serialization;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.range.ActionableCodon;
import com.hartwig.serve.datamodel.range.ActionableExon;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ActionableExonFileTest {

    private static final String ACTIONABLE_EXON_TSV = Resources.getResource("actionable/ActionableExons.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableExon> exons = ActionableExonFile.read(ACTIONABLE_EXON_TSV);

        assertActionableExons(exons);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableExonFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableExon> regeneratedExons= ActionableExonFile.fromLines(ActionableExonFile.toLines(exons), fields);

        assertEquals(exons, regeneratedExons);
    }

    private static void assertActionableExons(@NotNull List<ActionableExon> exons) {
        assertEquals(1, exons.size());

        ActionableExon exon1 = findByGeneUp(exons, "EGFR");
        assertEquals("EGFR", exon1.gene());
        assertEquals("7", exon1.chromosome());
        assertEquals(55241707, (int) exon1.start());
        assertEquals(55279321, (int) exon1.end());
        assertEquals(MutationType.INFRAME, exon1.applicableMutationType());
    }

    @NotNull
    private static ActionableExon findByGeneUp(@NotNull List<ActionableExon> exons,  @NotNull String gene) {
        for (ActionableExon exon : exons) {
            if (exon.gene().equals(gene)) {
                return exon;
            }
        }

        throw new IllegalStateException("Could not find gene:" + gene);
    }
}
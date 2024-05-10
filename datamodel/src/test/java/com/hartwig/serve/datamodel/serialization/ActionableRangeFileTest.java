package com.hartwig.serve.datamodel.serialization;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ActionableRangeFileTest{
    private static final String ACTIONABLE_EXON_TSV = Resources.getResource("actionable/ActionableExons.37.tsv").getPath();
    private static final String ACTIONABLE_CODON_TSV = Resources.getResource("actionable/ActionableCodons.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvertExon() throws IOException {
        List<ActionableRange> exons = ActionableRangeFile.read(ACTIONABLE_EXON_TSV);

        assertActionableExons(exons);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableRangeFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableRange> regeneratedExons= ActionableRangeFile.fromLines(ActionableRangeFile.toLines(exons), fields);

        assertEquals(exons, regeneratedExons);
    }

    @Test
    public void canReadFromFileAndConvertCodon() throws IOException {
        List<ActionableRange> exons = ActionableRangeFile.read(ACTIONABLE_CODON_TSV);

        assertActionableCodons(exons);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableRangeFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableRange> regeneratedExons= ActionableRangeFile.fromLines(ActionableRangeFile.toLines(exons), fields);

        assertEquals(exons, regeneratedExons);
    }

    private static void assertActionableExons(@NotNull List<ActionableRange> exons) {
        assertEquals(2, exons.size());

        ActionableRange exon1 = findByGeneUp(exons, "EGFR");
        assertEquals("EGFR", exon1.gene());
        assertEquals("7", exon1.chromosome());
        assertEquals(55241707, (int) exon1.start());
        assertEquals(55279321, (int) exon1.end());
        assertEquals(MutationType.INFRAME, exon1.applicableMutationType());
    }

    private static void assertActionableCodons(@NotNull List<ActionableRange> codons) {
        assertEquals(2, codons.size());

        ActionableRange codon1 = findByGeneUp(codons, "BRAF");
        assertEquals("BRAF", codon1.gene());
        assertEquals("7", codon1.chromosome());
        assertEquals(140453136, (int) codon1.start());
        assertEquals(140453137, (int) codon1.end());
        assertEquals(MutationType.MISSENSE, codon1.applicableMutationType());
    }
    @NotNull
    private static ActionableRange findByGeneUp(@NotNull List<ActionableRange> exons,  @NotNull String gene) {
        for (ActionableRange exon : exons) {
            if (exon.gene().equals(gene)) {
                return exon;
            }
        }

        throw new IllegalStateException("Could not find gene:" + gene);
    }
}
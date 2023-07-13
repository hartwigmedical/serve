package com.hartwig.serve.datamodel.serialization;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.range.ActionableCodon;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ActionableCodonFileTest  {

    private static final String ACTIONABLE_CODON_TSV = Resources.getResource("actionable/ActionableCodons.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableCodon> codons = ActionableCodonFile.read(ACTIONABLE_CODON_TSV);

        assertActionableCodons(codons);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableCodonFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableCodon> regeneratedCodons = ActionableCodonFile.fromLines(ActionableCodonFile.toLines(codons), fields);

        assertEquals(codons, regeneratedCodons);
    }

    private static void assertActionableCodons(@NotNull List<ActionableCodon> codons) {
        assertEquals(1, codons.size());

        ActionableCodon codon1 = findByGeneUp(codons, "BRAF");
        assertEquals("BRAF", codon1.gene());
        assertEquals("7", codon1.chromosome());
        assertEquals(140453136, (int) codon1.start());
        assertEquals(140453137, (int) codon1.end());
        assertEquals(MutationType.MISSENSE, codon1.applicableMutationType());
    }

    @NotNull
    private static ActionableCodon findByGeneUp(@NotNull List<ActionableCodon> codons,  @NotNull String gene) {
        for (ActionableCodon codon : codons) {
            if (codon.gene().equals(gene)) {
                return codon;
            }
        }

        throw new IllegalStateException("Could not find gene:" + gene);
    }

}
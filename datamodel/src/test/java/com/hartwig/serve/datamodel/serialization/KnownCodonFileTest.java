package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownCodonFileTest {

    private static final String KNOWN_CODON_TSV = Resources.getResource("known/KnownCodons.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownCodon> codons = KnownCodonFile.read(KNOWN_CODON_TSV);

        assertKnownCodons(codons);

        Map<String, Integer> fields = SerializationUtil.createFields(KnownCodonFile.header(), KnownCodonFile.FIELD_DELIMITER);
        List<KnownCodon> regeneratedCodons = KnownCodonFile.fromLines(KnownCodonFile.toLines(codons), fields);

        assertEquals(codons, regeneratedCodons);
    }

    private static void assertKnownCodons(@NotNull List<KnownCodon> codons) {
        assertEquals(2, codons.size());

        // TODO Implement (See ActionableFusionFileTest)
    }
}
package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownExonFileTest {

    private static final String KNOWN_EXON_TSV = Resources.getResource("known/KnownExons.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownExon> exons = KnownExonFile.read(KNOWN_EXON_TSV);

        assertKnownExons(exons);

        Map<String, Integer> fields = SerializationUtil.createFields(KnownExonFile.header(), KnownCodonFile.FIELD_DELIMITER);
        List<KnownExon> regeneratedExons = KnownExonFile.fromLines(KnownExonFile.toLines(exons), fields);

        assertEquals(exons, regeneratedExons);
    }

    private static void assertKnownExons(@NotNull List<KnownExon> exons) {
        assertEquals(2, exons.size());

        // TODO Implement: See ActionableFusionFileTest
    }
}
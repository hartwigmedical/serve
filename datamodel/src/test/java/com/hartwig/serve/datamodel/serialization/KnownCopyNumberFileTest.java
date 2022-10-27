package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownCopyNumberFileTest {

    private static final String KNOWN_COPY_NUMBER_TSV = Resources.getResource("known/KnownCopyNumbers.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownCopyNumber> copyNumbers = KnownCopyNumberFile.read(KNOWN_COPY_NUMBER_TSV);

        assertKnownCopyNumbers(copyNumbers);

        Map<String, Integer> fields = SerializationUtil.createFields(KnownCopyNumberFile.header(), KnownCodonFile.FIELD_DELIMITER);
        List<KnownCopyNumber> regeneratedCopyNumbers = KnownCopyNumberFile.fromLines(KnownCopyNumberFile.toLines(copyNumbers), fields);

        assertEquals(copyNumbers, regeneratedCopyNumbers);
    }

    private static void assertKnownCopyNumbers(@NotNull List<KnownCopyNumber> copyNumbers) {
        assertEquals(2, copyNumbers.size());

        // TODO Implement (See ActionableFusionFileTest)
    }

}
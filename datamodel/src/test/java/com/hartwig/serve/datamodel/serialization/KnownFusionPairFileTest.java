package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.fusion.KnownFusionPair;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownFusionPairFileTest {

    private static final String KNOWN_FUSION_PAIR_TSV = Resources.getResource("known/KnownFusionPairs.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownFusionPair> fusionPairs = KnownFusionPairFile.read(KNOWN_FUSION_PAIR_TSV);

        assertKnownFusionPairs(fusionPairs);

        Map<String, Integer> fields = SerializationUtil.createFields(KnownFusionPairFile.header(), KnownCodonFile.FIELD_DELIMITER);
        List<KnownFusionPair> regeneratedFusionPairs = KnownFusionPairFile.fromLines(KnownFusionPairFile.toLines(fusionPairs), fields);

        assertEquals(fusionPairs, regeneratedFusionPairs);
    }

    private static void assertKnownFusionPairs(@NotNull List<KnownFusionPair> fusionPairs) {
        assertEquals(2, fusionPairs.size());

        // TODO Implement (See ActionableFusionFileTest)
    }

}
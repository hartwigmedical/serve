package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownFusionFileTest {

    private static final String KNOWN_FUSION_PAIR_TSV = Resources.getResource("known/KnownFusions.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownFusion> fusionPairs = KnownFusionFile.read(KNOWN_FUSION_PAIR_TSV);

        assertKnownFusionPairs(fusionPairs);

        Map<String, Integer> fields = SerializationUtil.createFields(KnownFusionFile.header(), KnownCodonFile.FIELD_DELIMITER);
        List<KnownFusion> regeneratedFusionPairs = KnownFusionFile.fromLines(KnownFusionFile.toLines(fusionPairs), fields);

        assertEquals(fusionPairs, regeneratedFusionPairs);
    }

    private static void assertKnownFusionPairs(@NotNull List<KnownFusion> fusionPairs) {
        assertEquals(2, fusionPairs.size());

        // TODO Implement (See ActionableFusionFileTest)
    }

}
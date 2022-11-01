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

    private static final String KNOWN_FUSION_TSV = Resources.getResource("known/KnownFusions.SERVE.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<KnownFusion> fusions = KnownFusionFile.read(KNOWN_FUSION_TSV);

        assertKnownFusions(fusions);

        Map<String, Integer> fields = SerializationUtil.createFields(KnownFusionFile.header(), KnownCodonFile.FIELD_DELIMITER);
        List<KnownFusion> regeneratedFusions = KnownFusionFile.fromLines(KnownFusionFile.toLines(fusions), fields);

        assertEquals(fusions, regeneratedFusions);
    }

    private static void assertKnownFusions(@NotNull List<KnownFusion> fusions) {
        assertEquals(2, fusions.size());

        // TODO Implement (See ActionableFusionFileTest)
    }

}
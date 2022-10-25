package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableRangeFileTest {

    private static final String ACTIONABLE_RANGE_TSV = Resources.getResource("actionability/ActionableRanges.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableRange> ranges = ActionableRangeFile.read(ACTIONABLE_RANGE_TSV);

        assertActionableRanges(ranges);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableRangeFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableRange> regeneratedRanges = ActionableRangeFile.fromLines(ActionableRangeFile.toLines(ranges), fields);

        assertEquals(ranges, regeneratedRanges);
    }

    private static void assertActionableRanges(@NotNull List<ActionableRange> ranges) {
        assertEquals(2, ranges.size());

        // TODO Implement (See ActionableFusionFileTest)
    }
}
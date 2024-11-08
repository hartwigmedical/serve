package com.hartwig.serve.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.ServeAlgoTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvent;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ExtractionFunctionsTest {

    private static final Knowledgebase SOURCE_1 = Knowledgebase.VICC_CIVIC;
    private static final Knowledgebase SOURCE_2 = Knowledgebase.VICC_CGI;

    @Test
    public void canMergeProperExtractionResults() {
        ExtractionResult result1 = ServeAlgoTestFactory.createProperResultForSource(SOURCE_1);
        ExtractionResult result2 = ServeAlgoTestFactory.createProperResultForSource(SOURCE_2);

        ExtractionResult merged = ExtractionFunctions.merge(Lists.newArrayList(result1, result2));

        assertMergedKnownEvents(merged.knownEvents().hotspots());
        assertMergedKnownEvents(merged.knownEvents().codons());
        assertMergedKnownEvents(merged.knownEvents().exons());
        assertMergedKnownEvents(merged.knownEvents().genes());
        assertMergedKnownEvents(merged.knownEvents().copyNumbers());
        assertMergedKnownEvents(merged.knownEvents().fusions());

        assertEquals(2, merged.eventInterpretations().size());
        assertEquals(2, merged.efficacyEvidences().size());
        assertEquals(2, merged.trials().size());
    }

    @Test
    public void retainsNullOnMinimalExtractionResults() {
        ExtractionResult result1 = ServeAlgoTestFactory.createMinimalResultForSource(SOURCE_1);
        ExtractionResult result2 = ServeAlgoTestFactory.createMinimalResultForSource(SOURCE_2);
        ExtractionResult merged = ExtractionFunctions.merge(Lists.newArrayList(result1, result2));

        assertNull(merged.knownEvents());
        assertNull(merged.efficacyEvidences());
        assertNull(merged.trials());
    }

    private static void assertMergedKnownEvents(@NotNull Set<? extends KnownEvent> known) {
        assertEquals(1, known.size());
        KnownEvent first = known.iterator().next();
        assertTrue(first.sources().contains(SOURCE_1));
        assertTrue(first.sources().contains(SOURCE_2));
    }
}
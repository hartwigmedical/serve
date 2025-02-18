package com.hartwig.serve.common.classification;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.common.classification.matchers.EventMatcher;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EventClassifierTest {

    private static final String VARIANT = "variant";
    private static final String FUSION_PAIR = "fusion pair";
    private static final String MULTIPLE = "multiple";

    @Test
    public void canDetermineMutationTypes() {
        EventClassifier classifier = new EventClassifier(buildTestMatcherMap());

        assertEquals(EventType.VARIANT, classifier.determineType("any", VARIANT));
        assertEquals(EventType.FUSION_PAIR, classifier.determineType("any", FUSION_PAIR));

        // Events with multiple types should be UNKNOWN.
        assertEquals(EventType.UNKNOWN, classifier.determineType("any", MULTIPLE));

        assertEquals(EventType.UNKNOWN, classifier.determineType("any", "any"));
    }

    @NotNull
    private static Map<EventType, EventMatcher> buildTestMatcherMap() {
        Map<EventType, EventMatcher> map = Maps.newHashMap();
        map.put(EventType.VARIANT, (gene, event) -> event.equals(VARIANT));
        map.put(EventType.FUSION_PAIR, (gene, event) -> event.equals(FUSION_PAIR));
        map.put(EventType.COMPLEX, (gene, event) -> event.equals(MULTIPLE));
        map.put(EventType.COMBINED, (gene, event) -> event.equals(MULTIPLE));
        return map;
    }
}
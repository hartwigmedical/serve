package com.hartwig.serve.common.classification;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EventClassifierFactoryTest {

    @Test
    public void canBuildEventClassifier() {
        EventClassifier classifier = EventClassifierFactory.buildClassifier(ImmutableEventClassifierConfig.builder()
                .proteinAnnotationExtractor(event -> event)
                .build());

        assertEquals(EventType.UNKNOWN, classifier.determineType("gene", "mutation"));
    }
}
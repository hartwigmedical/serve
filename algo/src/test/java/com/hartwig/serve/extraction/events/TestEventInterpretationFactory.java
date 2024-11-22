package com.hartwig.serve.extraction.events;

import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestEventInterpretationFactory {

    private TestEventInterpretationFactory() {
    }

    @NotNull
    public static ImmutableEventInterpretation.Builder eventInterpretationBuilder() {
        return ImmutableEventInterpretation.builder()
                .source(Knowledgebase.UNKNOWN)
                .sourceEvent(Strings.EMPTY)
                .interpretedGene(Strings.EMPTY)
                .interpretedEvent(Strings.EMPTY)
                .interpretedEventType(EventType.UNKNOWN);
    }

    @NotNull
    public static EventInterpretation createTestEventInterpretationForSource(@NotNull Knowledgebase source) {
        return eventInterpretationBuilder().source(source).build();
    }
}

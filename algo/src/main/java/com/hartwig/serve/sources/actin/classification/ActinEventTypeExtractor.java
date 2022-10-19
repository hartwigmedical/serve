package com.hartwig.serve.sources.actin.classification;

import com.hartwig.serve.common.classification.EventClassifier;
import com.hartwig.serve.common.classification.EventClassifierFactory;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.sources.actin.reader.ActinEntry;

import org.jetbrains.annotations.NotNull;

public final class ActinEventTypeExtractor {

    private static final EventClassifier CLASSIFIER = EventClassifierFactory.buildClassifier(ActinClassificationConfig.build());

    private ActinEventTypeExtractor() {
    }

    @NotNull
    public static EventType determineEventType(@NotNull ActinEntry entry, @NotNull String event) {
        String gene = entry.gene() != null ? entry.gene() : ActinKeywords.NO_GENE;

        return CLASSIFIER.determineType(gene, event);
    }
}

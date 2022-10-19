package com.hartwig.serve.iclusion.classification;

import com.hartwig.serve.common.classification.EventClassifier;
import com.hartwig.serve.common.classification.EventClassifierFactory;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.iclusion.datamodel.IclusionMutation;

import org.jetbrains.annotations.NotNull;

public final class IclusionEventTypeExtractor {

    @NotNull
    private static final EventClassifier CLASSIFIER = EventClassifierFactory.buildClassifier(IclusionClassificationConfig.build());

    private IclusionEventTypeExtractor() {
    }

    @NotNull
    public static EventType classify(@NotNull IclusionMutation mutation) {
        return CLASSIFIER.determineType(mutation.gene(), mutation.name());
    }
}

package com.hartwig.serve.ckb.classification;

import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.serve.classification.EventClassifier;
import com.hartwig.serve.common.serve.classification.EventClassifierFactory;
import com.hartwig.serve.common.serve.classification.EventType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class CkbEventTypeExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbEventTypeExtractor.class);
    private static final EventClassifier CLASSIFIER = EventClassifierFactory.buildClassifier(CkbClassificationConfig.build());

    private CkbEventTypeExtractor() {
    }

    @NotNull
    public static EventType classify(@NotNull CkbEntry entry) {
        int variantCount = entry.variants().size();
        if (variantCount > 1) {
            return EventType.COMBINED;
        } else if (variantCount == 1) {
            Variant variant = entry.variants().get(0);

            String gene = CkbEventAndGeneExtractor.extractGene(variant);
            String event = CkbEventAndGeneExtractor.extractEvent(variant);

            return CLASSIFIER.determineType(gene, event);
        } else {
            LOGGER.warn("CKB entry found with no variants: {}", entry);
            return EventType.UNKNOWN;
        }
    }
}

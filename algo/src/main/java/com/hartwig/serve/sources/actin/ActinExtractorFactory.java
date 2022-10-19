package com.hartwig.serve.sources.actin;

import com.hartwig.serve.common.serve.classification.EventClassifierConfig;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;

import org.jetbrains.annotations.NotNull;

public final class ActinExtractorFactory {

    private ActinExtractorFactory() {
    }

    @NotNull
    public static ActinExtractor buildActinExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource) {
        return new ActinExtractor(EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.WARN_ONLY));
    }
}

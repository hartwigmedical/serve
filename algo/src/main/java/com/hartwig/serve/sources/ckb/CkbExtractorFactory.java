package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.common.serve.classification.EventClassifierConfig;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;

import org.jetbrains.annotations.NotNull;

public final class CkbExtractorFactory {

    private CkbExtractorFactory() {
    }

    @NotNull
    public static CkbExtractor buildCkbExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource) {
        // We want to capture all events from CKB, so ignore driver inconsistencies
        return new CkbExtractor(EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE));
    }
}

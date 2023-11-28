package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;

import org.jetbrains.annotations.NotNull;

public final class CkbTrialExtractorFactory {

    private CkbTrialExtractorFactory() {
    }

    @NotNull
    public static CkbTrialExtractor create(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource) {
        return new CkbTrialExtractor(EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE));
    }
}

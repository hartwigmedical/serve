package com.hartwig.serve.sources.hartwig.trial;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;

import org.jetbrains.annotations.NotNull;

public final class HartwigTrialExtractorFactory {

    private HartwigTrialExtractorFactory() {
    }

    @NotNull
    public static HartwigTrialExtractor createCkbEmulatedExtractor(@NotNull RefGenomeResource refGenomeResource) {
        EventExtractor extractor =
                EventExtractorFactory.create(CkbClassificationConfig.build(), refGenomeResource, DriverInconsistencyMode.IGNORE);
        
        return new HartwigTrialExtractor(extractor);
    }
}

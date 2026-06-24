package com.hartwig.serve.sources.hartwig.trial;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.common.classification.EventClassifier;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.common.classification.EventClassifierFactory;
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
        EventClassifierConfig ckbConfig = CkbClassificationConfig.build();
        
        EventClassifier ckbClassifier = EventClassifierFactory.buildClassifier(ckbConfig);
        EventExtractor ckbExtractor = EventExtractorFactory.create(ckbConfig, refGenomeResource, DriverInconsistencyMode.IGNORE);
        
        return new HartwigTrialExtractor(ckbClassifier, ckbExtractor);
    }
}

package com.hartwig.serve.sources.iclusion;

import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;

import org.jetbrains.annotations.NotNull;

public final class IclusionExtractorFactory {

    private IclusionExtractorFactory() {
    }

    @NotNull
    public static IclusionExtractor buildIclusionExtractor(@NotNull EventClassifierConfig config,
            @NotNull RefGenomeResource refGenomeResource, @NotNull DoidLookup missingDoidLookup) {
        return new IclusionExtractor(EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.WARN_ONLY),
                new ActionableTrialFactory(missingDoidLookup));
    }
}

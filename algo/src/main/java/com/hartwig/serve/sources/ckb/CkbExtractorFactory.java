package com.hartwig.serve.sources.ckb;

import java.util.Set;

import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterModel;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterModel;
import com.hartwig.serve.sources.ckb.region.CkbRegion;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;

import org.jetbrains.annotations.NotNull;

public final class CkbExtractorFactory {

    private CkbExtractorFactory() {
    }

    public static CkbExtractor createExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource,
            @NotNull TreatmentApproachCurator treatmentApproachCurator, @NotNull CkbEvidenceFilterModel evidenceFilter,
            @NotNull CkbTrialFilterModel trialFilter, @NotNull Set<CkbRegion> regionsToInclude) {
        return new CkbExtractor(createEventExtractor(config, refGenomeResource),
                new EfficacyEvidenceFactory(treatmentApproachCurator, evidenceFilter),
                new ActionableTrialFactory(trialFilter, regionsToInclude));
    }

    @NotNull
    public static EventExtractor createEventExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource) {
        // We want to capture all events from CKB, so ignore driver inconsistencies
        return EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE);
    }
}

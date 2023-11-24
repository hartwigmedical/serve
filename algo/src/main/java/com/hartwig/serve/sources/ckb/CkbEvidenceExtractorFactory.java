package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;

import org.jetbrains.annotations.NotNull;

public final class CkbEvidenceExtractorFactory {

    private CkbEvidenceExtractorFactory() {
    }

    @NotNull
    public static CkbEvidenceExtractor create(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource,
            @NotNull TreatmentApproachCurator treatmentApproachCurator) {
        // We want to capture all events from CKB, so ignore driver inconsistencies
        return new CkbEvidenceExtractor(EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE),
                treatmentApproachCurator);
    }
}

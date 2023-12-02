package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;

import org.jetbrains.annotations.NotNull;

public final class CkbExtractorFactory {

    private CkbExtractorFactory() {
    }

    @NotNull
    public static CkbExtractor createEvidenceExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource,
            @NotNull TreatmentApproachCurator treatmentApproachCurator) {
        // We want to capture all events from CKB, so ignore driver inconsistencies
        return new CkbExtractor(Knowledgebase.CKB_EVIDENCE,
                EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE),
                new ActionableEvidenceFactory(treatmentApproachCurator),
                true);
    }

    @NotNull
    public static CkbExtractor createTrialExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource) {
        return new CkbExtractor(Knowledgebase.CKB_TRIAL,
                EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE),
                new ActionableTrialFactory(),
                false);
    }
}

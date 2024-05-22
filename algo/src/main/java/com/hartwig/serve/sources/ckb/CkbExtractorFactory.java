package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.blacklist.CkbEvidenceBlacklistModel;
import com.hartwig.serve.sources.ckb.blacklist.CkbStudyBlacklistModel;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;

import org.jetbrains.annotations.NotNull;

public final class CkbExtractorFactory {

    private CkbExtractorFactory() {
    }

    @NotNull
    public static CkbExtractor createEvidenceExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource,
            @NotNull TreatmentApproachCurator treatmentApproachCurator, @NotNull CkbEvidenceBlacklistModel blacklistEvidence) {
        return new CkbExtractor(Knowledgebase.CKB_EVIDENCE,
                createEventExtractor(config, refGenomeResource),
                new ActionableEvidenceFactory(treatmentApproachCurator, blacklistEvidence),
                true);
    }

    @NotNull
    public static CkbExtractor createTrialExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource,
            @NotNull CkbStudyBlacklistModel blacklistStudy) {
        return new CkbExtractor(Knowledgebase.CKB_TRIAL,
                createEventExtractor(config, refGenomeResource),
                new ActionableTrialFactory(blacklistStudy),
                false);
    }

    @NotNull
    public static EventExtractor createEventExtractor(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource) {
        // We want to capture all events from CKB, so ignore driver inconsistencies
        return EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE);
    }
}

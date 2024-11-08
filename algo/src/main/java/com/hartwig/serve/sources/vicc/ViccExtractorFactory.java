package com.hartwig.serve.sources.vicc;

import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.extraction.EventExtractorFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.vicc.curation.DrugCurator;
import com.hartwig.serve.sources.vicc.curation.EvidenceLevelCurator;
import com.hartwig.serve.sources.vicc.doid.DoidLookup;

import org.jetbrains.annotations.NotNull;

public final class ViccExtractorFactory {

    private ViccExtractorFactory() {
    }

    @NotNull
    public static ViccExtractor create(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource,
            @NotNull DoidLookup missingDoidLookup) {
        return new ViccExtractor(EventExtractorFactory.create(config, refGenomeResource, DriverInconsistencyMode.IGNORE),
                new ActionableEvidenceFactory(missingDoidLookup, new DrugCurator(), new EvidenceLevelCurator()));
    }
}

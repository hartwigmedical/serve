package com.hartwig.serve.sources.hartwig.trial;

import com.hartwig.serve.refgenome.RefGenomeResource;

import org.jetbrains.annotations.NotNull;

public final class HartwigTrialExtractorFactory {

    private HartwigTrialExtractorFactory() {
    }

    @NotNull
    public static HartwigTrialExtractor createCkbEmulatedExtractor(@NotNull RefGenomeResource refGenomeResource) {
        return new HartwigTrialExtractor(MolecularCriteriumExtractorFactory.createCkbEmulatedExtractor(refGenomeResource));
    }
}

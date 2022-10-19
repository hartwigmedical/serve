package com.hartwig.serve.sources.actin.classification;

import com.hartwig.serve.common.codon.AminoAcids;
import com.hartwig.serve.common.serve.classification.EventPreprocessor;

import org.jetbrains.annotations.NotNull;

public class ActinProteinAnnotationExtractor implements EventPreprocessor {

    public ActinProteinAnnotationExtractor() {
    }

    @NotNull
    @Override
    public String apply(@NotNull String event) {
        return AminoAcids.forceSingleLetterProteinAnnotation(event);
    }
}

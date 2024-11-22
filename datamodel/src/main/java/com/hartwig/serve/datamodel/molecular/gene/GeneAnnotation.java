package com.hartwig.serve.datamodel.molecular.gene;

import org.jetbrains.annotations.NotNull;

public interface GeneAnnotation {

    @NotNull
    String gene();

    @NotNull
    GeneEvent event();
}

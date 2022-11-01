package com.hartwig.serve.datamodel.gene;

import org.jetbrains.annotations.NotNull;

public interface GeneAnnotation {

    @NotNull
    String gene();

    @NotNull
    GeneEvent event();
}

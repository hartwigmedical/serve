package com.hartwig.serve.datamodel;

import org.jetbrains.annotations.NotNull;

public interface ActionableGeneMutation {

    @NotNull
    String gene();

    @NotNull
    GeneRole geneRole();

    @NotNull
    ProteinEffect proteinEffect();

}

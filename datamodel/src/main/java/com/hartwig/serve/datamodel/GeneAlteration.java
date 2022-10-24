package com.hartwig.serve.datamodel;

import org.jetbrains.annotations.NotNull;

public interface GeneAlteration {

    @NotNull
    String gene();

    @NotNull
    GeneRole geneRole();

    @NotNull
    ProteinEffect proteinEffect();

}

package com.hartwig.serve.datamodel.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GeneAlteration {

    @NotNull
    String gene();

    @NotNull
    GeneRole geneRole();

    @NotNull
    ProteinEffect proteinEffect();

    @Nullable
    Boolean associatedWithDrugResistance();

}

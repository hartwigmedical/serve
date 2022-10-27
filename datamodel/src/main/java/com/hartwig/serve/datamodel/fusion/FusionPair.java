package com.hartwig.serve.datamodel.fusion;

import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FusionPair {

    @NotNull
    String geneUp();

    @Nullable
    Integer minExonUp();

    @Nullable
    Integer maxExonUp();

    @NotNull
    String geneDown();

    @Nullable
    Integer minExonDown();

    @Nullable
    Integer maxExonDown();

    @NotNull
    ProteinEffect proteinEffect();

}

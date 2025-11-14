package com.hartwig.serve.datamodel.molecular.immuno;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ImmunoAnnotation {

    @NotNull String gene();

    @NotNull String alleleGroup();

    @Nullable String hlaProtein();

    @Nullable String synonymousDnaChange();

    @Nullable String nonCodingDifferences();

    @Nullable String expressionStatus();
}

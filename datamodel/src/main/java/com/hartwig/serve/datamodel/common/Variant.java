package com.hartwig.serve.datamodel.common;

import org.jetbrains.annotations.NotNull;

public interface Variant extends GenomePosition {

    @NotNull
    String ref();

    @NotNull
    String alt();
}

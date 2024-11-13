package com.hartwig.serve.datamodel.molecular.common;

import org.jetbrains.annotations.NotNull;

public interface GenomePosition {

    @NotNull
    String chromosome();

    int position();
}

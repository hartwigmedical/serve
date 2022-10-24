package com.hartwig.serve.datamodel.common;

import org.jetbrains.annotations.NotNull;

public interface GenomePosition {

    @NotNull
    String chromosome();

    int position();
}

package com.hartwig.serve.datamodel.genome;

import org.jetbrains.annotations.NotNull;

public interface GenomePosition {

    @NotNull
    String chromosome();

    int position();
}

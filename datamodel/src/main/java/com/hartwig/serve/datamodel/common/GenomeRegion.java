package com.hartwig.serve.datamodel.common;

import org.jetbrains.annotations.NotNull;

public interface GenomeRegion {

    @NotNull
    String chromosome();

    int start();

    int end();

}

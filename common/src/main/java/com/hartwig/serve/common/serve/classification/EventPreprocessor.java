package com.hartwig.serve.common.serve.classification;

import org.jetbrains.annotations.NotNull;

public interface EventPreprocessor {

    @NotNull
    String apply(@NotNull String event);
}

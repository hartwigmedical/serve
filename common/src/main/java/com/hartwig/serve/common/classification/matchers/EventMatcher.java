package com.hartwig.serve.common.classification.matchers;

import org.jetbrains.annotations.NotNull;

public interface EventMatcher {

    boolean matches(@NotNull String gene, @NotNull String event);
}

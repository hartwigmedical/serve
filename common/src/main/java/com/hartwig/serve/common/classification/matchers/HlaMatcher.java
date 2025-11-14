package com.hartwig.serve.common.classification.matchers;

import org.jetbrains.annotations.NotNull;

public class HlaMatcher implements EventMatcher {

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        return gene.startsWith("HLA") && event.startsWith("*") && (event.contains(":") || event.chars().noneMatch(Character::isLetter));
    }
}

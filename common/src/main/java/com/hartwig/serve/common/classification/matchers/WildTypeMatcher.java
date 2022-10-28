package com.hartwig.serve.common.classification.matchers;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class WildTypeMatcher implements EventMatcher {

    @NotNull
    private final Set<String> geneWildTypeKeyPhrases;

    WildTypeMatcher(@NotNull final Set<String> geneWildTypeKeyPhrases) {
        this.geneWildTypeKeyPhrases = geneWildTypeKeyPhrases;
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        for (String keyPhrase : geneWildTypeKeyPhrases) {
            if (event.contains(keyPhrase)) {
                return true;
            }
        }
        return false;
    }
}

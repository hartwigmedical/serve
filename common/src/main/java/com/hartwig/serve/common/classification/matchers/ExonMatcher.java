package com.hartwig.serve.common.classification.matchers;

import java.util.Set;

import com.hartwig.serve.common.classification.ExonicFusionPair;

import org.jetbrains.annotations.NotNull;

class ExonMatcher implements EventMatcher {

    @NotNull
    private final Set<String> exonIdentifiers;
    @NotNull
    private final Set<String> exonKeywords;
    @NotNull
    private final Set<String> exonBlacklistKeyPhrases;
    @NotNull
    private final Set<String> specificExonEvents;

    public ExonMatcher(@NotNull final Set<String> exonIdentifiers, @NotNull final Set<String> exonKeywords,
            @NotNull final Set<String> exonBlacklistKeyPhrases, @NotNull final Set<String> specificExonEvents) {
        this.exonIdentifiers = exonIdentifiers;
        this.exonKeywords = exonKeywords;
        this.exonBlacklistKeyPhrases = exonBlacklistKeyPhrases;
        this.specificExonEvents = specificExonEvents;
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        if (specificExonEvents.contains(event)) {
            return true;
        }

        // Events like "GENE1 exonX::GENE2 exonY" describe a fusion pair rather than an exon on a single gene.
        if (ExonicFusionPair.isExonicFusionPair(event)) {
            return false;
        }

        for (String blacklistPhrase : exonBlacklistKeyPhrases) {
            if (event.contains(blacklistPhrase)) {
                return false;
            }
        }

        // At least one exon identifier needs to be found.
        boolean identifierFound = false;
        for (String identifier : exonIdentifiers) {
            if (event.contains(identifier)) {
                identifierFound = true;
                break;
            }
        }

        if (identifierFound) {
            String[] words = event.split(" ");
            for (String keyword : exonKeywords) {
                for (String word : words) {
                    if (word.equals(keyword)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

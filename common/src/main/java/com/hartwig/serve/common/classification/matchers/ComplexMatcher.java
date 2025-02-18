package com.hartwig.serve.common.classification.matchers;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

class ComplexMatcher implements EventMatcher {

    @NotNull
    private final VariantMatcher variantMatcher;
    @NotNull
    private final Map<String, Set<String>> complexEventsPerGene;

    ComplexMatcher(@NotNull final VariantMatcher variantMatcher, @NotNull final Map<String, Set<String>> complexEventsPerGene) {
        this.variantMatcher = variantMatcher;
        this.complexEventsPerGene = complexEventsPerGene;
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        Set<String> entriesForGene = complexEventsPerGene.get(gene);
        if (entriesForGene != null && entriesForGene.contains(event.trim())) {
            return true;
        } else if (event.trim().endsWith(".")) {
            // Not sure what a dot means!
            return true;
        } else if (event.split("\\*").length > 2) {
            // Some hotspots contain multiple stop codons.
            return true;
        } else if (event.contains("/")) {
            // Some hotspots appear as V600E/K
            return !event.toLowerCase().contains("exon");
        } else if (variantMatcher.isComplexMatch(gene, event)) {
            // Some hotspots are real hotspots but considered complex by hotspot matcher.
            return true;
        } else {
            // Some frameshifts also change the amino acid itself in the position of the frameshift.
            int fsLocation = event.indexOf("fs");
            return fsLocation > 1 && !Character.isDigit(event.charAt(fsLocation - 1));
        }
    }
}

package com.hartwig.serve.extraction.variant;

import org.jetbrains.annotations.NotNull;

public enum MitochondrialChromosome {
    MT;

    public static boolean contains(@NotNull String contig) {
        return contig.equals("chrM") || contig.equals("MT");
    }
}

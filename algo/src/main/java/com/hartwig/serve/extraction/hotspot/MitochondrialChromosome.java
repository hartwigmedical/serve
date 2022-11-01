package com.hartwig.serve.extraction.hotspot;

import org.jetbrains.annotations.NotNull;

public enum MitochondrialChromosome {
    MT;

    public static boolean contains(@NotNull String contig) {
        return contig.equals("chrM") || contig.equals("MT");
    }
}

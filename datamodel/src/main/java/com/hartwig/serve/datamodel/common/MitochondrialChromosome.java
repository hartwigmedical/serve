package com.hartwig.serve.datamodel.common;

import org.jetbrains.annotations.NotNull;

public enum MitochondrialChromosome implements Chromosome {
    MT;

    @NotNull
    public static MitochondrialChromosome fromString(@NotNull String contig) {
        if (!contains(contig)) {
            throw new IllegalArgumentException("Invalid mitochondrial contig " + contig);
        }

        return MT;
    }

    public static boolean contains(@NotNull String contig) {
        return contig.equals("chrM") || contig.equals("MT");
    }
}

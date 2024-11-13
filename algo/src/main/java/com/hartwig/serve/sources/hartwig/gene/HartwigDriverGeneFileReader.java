package com.hartwig.serve.sources.hartwig.gene;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public final class HartwigDriverGeneFileReader {

    private static final String GENE_ROLE_FIELD_NAME = "likelihoodType";

    private HartwigDriverGeneFileReader() {
    }

    @NotNull
    public static List<HartwigGeneEntry> read(@NotNull String fileName) throws IOException {
        return HartwigGeneFileReader.read(fileName, GENE_ROLE_FIELD_NAME);
    }
}

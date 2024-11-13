package com.hartwig.serve.sources.hartwig.gene;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public final class HartwigCuratedGeneFileReader {

    private static final String GENE_ROLE_FIELD_NAME = "geneRole";

    private HartwigCuratedGeneFileReader() {
    }

    @NotNull
    public static List<HartwigGeneEntry> read(@NotNull String fileName) throws IOException {
        return HartwigGeneFileReader.read(fileName, GENE_ROLE_FIELD_NAME);
    }
}

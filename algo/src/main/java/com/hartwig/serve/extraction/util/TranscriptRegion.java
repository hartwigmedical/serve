package com.hartwig.serve.extraction.util;

import com.hartwig.serve.datamodel.common.GenomeRegion;

import org.jetbrains.annotations.NotNull;

public interface TranscriptRegion extends GenomeRegion {

    @NotNull
    String transName();

    boolean isCanonical();

    @NotNull
    String geneName();

    @NotNull
    String chromosomeBand();
}

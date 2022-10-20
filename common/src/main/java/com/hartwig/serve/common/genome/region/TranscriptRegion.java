package com.hartwig.serve.common.genome.region;

import com.hartwig.serve.datamodel.genome.region.GenomeRegion;

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

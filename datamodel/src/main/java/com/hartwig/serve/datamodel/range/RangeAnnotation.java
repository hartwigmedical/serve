package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.datamodel.GeneAlteration;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.genome.GenomeRegion;

import org.jetbrains.annotations.NotNull;

public interface RangeAnnotation extends GenomeRegion, GeneAlteration {

    @NotNull
    String transcript();

    int rank();

    @NotNull
    MutationType applicableMutationType();

}

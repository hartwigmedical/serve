package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.common.genome.region.GenomeRegion;
import com.hartwig.serve.datamodel.MutationTypeFilter;

import org.jetbrains.annotations.NotNull;

public interface RangeAnnotation extends GenomeRegion {

    @NotNull
    String gene();

    @NotNull
    String transcript();

    int rank();

    @NotNull
    MutationTypeFilter mutationType();

}

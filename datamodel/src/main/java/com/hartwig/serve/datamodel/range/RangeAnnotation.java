package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GenomeRegion;

import org.jetbrains.annotations.NotNull;

public interface RangeAnnotation extends GenomeRegion, GeneAlteration {

    @NotNull
    String transcript();

    int rank();

    @NotNull
    MutationType applicableMutationType();

}

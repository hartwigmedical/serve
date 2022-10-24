package com.hartwig.serve.datamodel.gene;

import com.hartwig.serve.datamodel.common.GeneAlteration;

import org.jetbrains.annotations.NotNull;

public interface GeneAnnotation extends GeneAlteration {

    @NotNull
    GeneLevelEvent event();
}

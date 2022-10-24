package com.hartwig.serve.datamodel.gene;

import com.hartwig.serve.datamodel.GeneAlteration;

import org.jetbrains.annotations.NotNull;

public interface GeneAnnotation extends GeneAlteration {

    @NotNull
    GeneLevelEvent event();
}

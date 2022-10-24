package com.hartwig.serve.datamodel.hotspot;

import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GenomePosition;

import org.jetbrains.annotations.NotNull;

public interface VariantHotspot extends GenomePosition, GeneAlteration {

    @NotNull
    String ref();

    @NotNull
    String alt();
}

package com.hartwig.serve.datamodel.hotspot;

import com.hartwig.serve.datamodel.common.GenomePosition;

import org.jetbrains.annotations.NotNull;

public interface VariantHotspot extends GenomePosition {

    @NotNull
    String gene();

    @NotNull
    String ref();

    @NotNull
    String alt();
}

package com.hartwig.serve.datamodel.hotspot;

import com.hartwig.serve.datamodel.GeneAlteration;
import com.hartwig.serve.datamodel.genome.GenomePosition;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface VariantHotspot extends GenomePosition, GeneAlteration {

    @NotNull
    String ref();

    @NotNull
    String alt();
}

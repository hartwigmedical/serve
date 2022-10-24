package com.hartwig.serve.datamodel;

import java.util.List;

import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface ActionableEvents {

    @NotNull
    List<ActionableHotspot> hotspots();

    @NotNull
    List<ActionableRange> ranges();

    @NotNull
    List<ActionableGene> genes();

    @NotNull
    List<ActionableFusion> fusions();

    @NotNull
    List<ActionableCharacteristic> characteristics();

    @NotNull
    List<ActionableHLA> hla();
}

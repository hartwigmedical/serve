package com.hartwig.serve.actionability;

import java.util.List;

import com.hartwig.serve.actionability.characteristic.ActionableCharacteristic;
import com.hartwig.serve.actionability.fusion.ActionableFusion;
import com.hartwig.serve.actionability.gene.ActionableGene;
import com.hartwig.serve.actionability.hotspot.ActionableHotspot;
import com.hartwig.serve.actionability.immuno.ActionableHLA;
import com.hartwig.serve.actionability.range.ActionableRange;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
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

package com.hartwig.serve.datamodel;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableMolecularProfile.class)
@JsonDeserialize(as = ImmutableMolecularProfile.class)
public abstract class MolecularProfile {

    @NotNull
    public abstract List<ActionableHotspot> hotspots();

    @NotNull
    public abstract List<ActionableRange> codons();

    @NotNull
    public abstract List<ActionableRange> exons();

    @NotNull
    public abstract List<ActionableGene> genes();

    @NotNull
    public abstract List<ActionableFusion> fusions();

    @NotNull
    public abstract List<ActionableCharacteristic> characteristics();

    @NotNull
    public abstract List<ActionableHLA> hla();

}

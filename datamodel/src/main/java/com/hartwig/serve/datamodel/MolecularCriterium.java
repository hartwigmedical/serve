package com.hartwig.serve.datamodel;

import java.util.Set;

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
@JsonSerialize(as = ImmutableMolecularCriterium.class)
@JsonDeserialize(as = ImmutableMolecularCriterium.class)
public abstract class MolecularCriterium {

    @NotNull
    public abstract Set<ActionableHotspot> hotspots();

    @NotNull
    public abstract Set<ActionableRange> codons();

    @NotNull
    public abstract Set<ActionableRange> exons();

    @NotNull
    public abstract Set<ActionableGene> genes();

    @NotNull
    public abstract Set<ActionableFusion> fusions();

    @NotNull
    public abstract Set<ActionableCharacteristic> characteristics();

    @NotNull
    public abstract Set<ActionableHLA> hla();

}

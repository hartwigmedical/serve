package com.hartwig.serve.datamodel;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
@JsonSerialize(as = ImmutableServeRecord.class)
@JsonDeserialize(as = ImmutableServeRecord.class)
public abstract class ServeRecord {

    @NotNull
    public abstract RefGenome refGenomeVersion();

    //    @NotNull
    //    public abstract Set<EventInterpretation> eventInterpretations();

    @NotNull
    public abstract Set<KnownHotspot> knownHotspots();

    @NotNull
    public abstract Set<KnownCodon> knownCodons();

    @NotNull
    public abstract Set<KnownExon> knownExons();

    @NotNull
    public abstract Set<KnownGene> knownGenes();

    @NotNull
    public abstract Set<KnownCopyNumber> knownCopyNumbers();

    @NotNull
    public abstract Set<KnownFusion> knownFusions();

    @NotNull
    public abstract Set<ActionableHotspot> actionableHotspots();

    @NotNull
    public abstract Set<ActionableRange> actionableCodons();

    @NotNull
    public abstract Set<ActionableRange> actionableExons();

    @NotNull
    public abstract Set<ActionableGene> actionableGenes();

    @NotNull
    public abstract Set<ActionableFusion> actionableFusions();

    @NotNull
    public abstract Set<ActionableCharacteristic> actionableCharacteristics();

    @NotNull
    public abstract Set<ActionableHLA> actionableHLA();
}

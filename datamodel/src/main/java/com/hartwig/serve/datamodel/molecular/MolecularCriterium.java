package com.hartwig.serve.datamodel.molecular;

import java.util.Comparator;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.molecular.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ActionableHotspotSet;
import com.hartwig.serve.datamodel.molecular.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.molecular.range.ActionableRange;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableMolecularCriterium.class)
@JsonDeserialize(as = ImmutableMolecularCriterium.class)
public abstract class MolecularCriterium implements Comparable<MolecularCriterium> {

    private static final Comparator<MolecularCriterium> COMPARATOR = new MolecularCriteriumComparator();

    @NotNull
    public abstract Set<ActionableHotspotSet> hotspots();

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

    @Override
    public int compareTo(MolecularCriterium other) {
        return COMPARATOR.compare(this, other);
    }

}

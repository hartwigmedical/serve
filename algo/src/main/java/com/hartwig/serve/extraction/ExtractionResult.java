package com.hartwig.serve.extraction;

import java.util.Set;

import com.hartwig.serve.common.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.actionability.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.actionability.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.actionability.gene.ActionableGene;
import com.hartwig.serve.datamodel.actionability.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.actionability.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.actionability.range.ActionableRange;
import com.hartwig.serve.extraction.codon.KnownCodon;
import com.hartwig.serve.extraction.copynumber.KnownCopyNumber;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.exon.KnownExon;
import com.hartwig.serve.extraction.fusion.KnownFusionPair;
import com.hartwig.serve.extraction.hotspot.KnownHotspot;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class ExtractionResult {

    @NotNull
    public abstract RefGenomeVersion refGenomeVersion();

    @NotNull
    public abstract Set<EventInterpretation> eventInterpretations();

    @NotNull
    public abstract Set<KnownHotspot> knownHotspots();

    @NotNull
    public abstract Set<KnownCodon> knownCodons();

    @NotNull
    public abstract Set<KnownExon> knownExons();

    @NotNull
    public abstract Set<KnownCopyNumber> knownCopyNumbers();

    @NotNull
    public abstract Set<KnownFusionPair> knownFusionPairs();

    @NotNull
    public abstract Set<ActionableHotspot> actionableHotspots();

    @NotNull
    public abstract Set<ActionableRange> actionableRanges();

    @NotNull
    public abstract Set<ActionableGene> actionableGenes();

    @NotNull
    public abstract Set<ActionableFusion> actionableFusions();

    @NotNull
    public abstract Set<ActionableCharacteristic> actionableCharacteristics();

    @NotNull
    public abstract Set<ActionableHLA> actionableHLA();
}
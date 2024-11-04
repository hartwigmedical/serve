package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.RefGenome;
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
import com.hartwig.serve.extraction.events.EventInterpretation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ExtractionResult {

    @NotNull
    public abstract RefGenome refGenomeVersion();

    @NotNull
    public abstract Set<EventInterpretation> eventInterpretations();

    @Nullable
    public abstract KnownEvents knownEvents();

    @Nullable
    public abstract List<EfficacyEvidence> efficacyEvidences();

    @Nullable
    public abstract List<ClinicalTrial> clinicalTrials();
}
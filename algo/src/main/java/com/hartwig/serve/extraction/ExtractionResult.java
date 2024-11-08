package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.ActionableTrial;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.RefGenome;
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
    public abstract List<ActionableTrial> trials();
}
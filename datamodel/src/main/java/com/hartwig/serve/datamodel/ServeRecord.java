package com.hartwig.serve.datamodel;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.trial.ActionableTrial;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableServeRecord.class)
@JsonDeserialize(as = ImmutableServeRecord.class)
public abstract class ServeRecord {

    @NotNull
    public abstract KnownEvents knownEvents();

    @NotNull
    public abstract List<EfficacyEvidence> evidences();

    @NotNull
    public abstract List<ActionableTrial> trials();
}

package com.hartwig.serve.sources.vicc.curation;

import java.util.List;

import com.hartwig.serve.datamodel.efficacy.EvidenceDirection;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevel;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class EvidenceLevelCurationKey {

    @NotNull
    public abstract ViccSource source();

    @NotNull
    public abstract List<String> genes();

    @NotNull
    public abstract String treatment();

    @NotNull
    public abstract EvidenceLevel level();

    @NotNull
    public abstract EvidenceDirection direction();

}

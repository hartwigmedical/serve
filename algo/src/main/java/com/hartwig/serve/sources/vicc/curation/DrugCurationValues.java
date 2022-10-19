package com.hartwig.serve.sources.vicc.curation;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class DrugCurationValues {

    @NotNull
    public abstract List<List<String>> drugs();
}

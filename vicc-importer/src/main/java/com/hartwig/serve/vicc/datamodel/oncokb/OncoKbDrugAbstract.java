package com.hartwig.serve.vicc.datamodel.oncokb;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class OncoKbDrugAbstract {

    @NotNull
    public abstract String text();

    @NotNull
    public abstract String link();

}

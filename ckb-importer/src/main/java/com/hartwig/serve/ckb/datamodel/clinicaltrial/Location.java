package com.hartwig.serve.ckb.datamodel.clinicaltrial;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class Location {

    @NotNull
    public abstract String nctId();

    @Nullable
    public abstract String status();

    @Nullable
    public abstract String facility();

    @NotNull
    public abstract List<Contact> contacts();

    @Nullable
    public abstract String city();

    @Nullable
    public abstract String state();

    @Nullable
    public abstract String zip();

    @Nullable
    public abstract String country();
}
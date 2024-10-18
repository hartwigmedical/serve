package com.hartwig.serve.datamodel;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ClinicalTrial {

    @NotNull
    public abstract String nctId();

    @NotNull
    public abstract String title();

    @Nullable
    public abstract String acronym();

    @NotNull
    public abstract Set<Country> countries();

    @NotNull
    public abstract Set<String> therapyNames();

    @Nullable
    public abstract String genderCriterium();

    @NotNull
    public abstract Set<String> urls();

    @NotNull
    public abstract Set<Indication> indication();

    @NotNull
    public abstract Set<MolecularEvent> inclusionEvents();
}

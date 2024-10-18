package com.hartwig.serve.datamodel;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionableTreatment {

    @NotNull
    public abstract Treatment treatment();

    @NotNull
    public abstract String efficacyDescription();

    @NotNull
    public abstract EvidenceLevel evidenceLevel();

    @NotNull
    public abstract EvidenceLevelDetails evidenceLevelDetails();

    @NotNull
    public abstract EvidenceDirection evidenceDirection();

    public abstract int evidenceYear();

    @NotNull
    public abstract Set<String> evidenceUrls();

    @NotNull
    public abstract Indication indication();

    @NotNull
    public abstract Set<MolecularEvent> inclusionEvents();
}
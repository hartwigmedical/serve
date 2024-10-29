package com.hartwig.serve.datamodel;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableEfficacyEvidence.class)
@JsonDeserialize(as = ImmutableEfficacyEvidence.class)
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
public abstract class EfficacyEvidence {

    @NotNull
    public abstract Knowledgebase source();

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
    public abstract MolecularProfile molecularProfile();
}
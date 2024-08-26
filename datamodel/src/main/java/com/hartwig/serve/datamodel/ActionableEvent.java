package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ActionableEvent {

    @NotNull
    Knowledgebase source();

    @NotNull
    String sourceEvent();

    @NotNull
    Set<String> sourceUrls();

    @NotNull
    Intervention intervention();

    @NotNull
    CancerType applicableCancerType();

    @NotNull
    Set<CancerType> blacklistCancerTypes();

    @NotNull
    EvidenceLevel level();

    @NotNull
    EvidenceDirection direction();

    @NotNull
    Set<String> evidenceUrls();

    @Nullable
    LocalDate lastUpdated();

    @Nullable
    String efficacyEvidence();
}
package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public interface ActionableEvent {

    @NotNull
    Knowledgebase source();

    @NotNull
    LocalDate date();

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
    String description();

    @NotNull
    EvidenceLevel level();

    @NotNull
    ApprovalStatus approvalStatus();

    @NotNull
    EvidenceDirection direction();

    @NotNull
    Set<String> evidenceUrls();
}
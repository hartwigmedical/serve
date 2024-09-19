package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public interface ActionableEvent {

    @NotNull
    Knowledgebase source();

    @NotNull
    LocalDate ckbEntryDate();

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
    String efficacyDescription();

    @NotNull
    String efficacyDescriptionYear();

    @NotNull
    EvidenceLevel level();

    @NotNull
    EvidenceLevelDetails evidenceLevelDetails();

    @NotNull
    EvidenceDirection direction();

    @NotNull
    Set<String> evidenceUrls();
}
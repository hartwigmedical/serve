package com.hartwig.serve.actionability;

import java.util.Set;

import com.hartwig.serve.cancertype.CancerType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.actionability.EvidenceDirection;
import com.hartwig.serve.datamodel.actionability.EvidenceLevel;
import com.hartwig.serve.datamodel.actionability.Treatment;

import org.jetbrains.annotations.NotNull;

public interface ActionableEvent {

    @NotNull
    Knowledgebase source();

    @NotNull
    String sourceEvent();

    @NotNull
    Set<String> sourceUrls();

    @NotNull
    Treatment treatment();

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
}
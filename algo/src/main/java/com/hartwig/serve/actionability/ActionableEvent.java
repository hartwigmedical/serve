package com.hartwig.serve.actionability;

import java.util.Set;

import com.hartwig.serve.cancertype.CancerType;
import com.hartwig.serve.common.serve.Knowledgebase;
import com.hartwig.serve.common.serve.actionability.EvidenceDirection;
import com.hartwig.serve.common.serve.actionability.EvidenceLevel;
import com.hartwig.serve.common.serve.actionability.Treatment;

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
package com.hartwig.serve.extraction;

import java.util.Set;

import com.hartwig.serve.common.serve.Knowledgebase;

import org.jetbrains.annotations.NotNull;

public interface KnownEvent {

    @NotNull
    Set<Knowledgebase> sources();

}

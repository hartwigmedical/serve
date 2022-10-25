package com.hartwig.serve.datamodel;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

public interface KnownEvent {

    @NotNull
    Set<Knowledgebase> sources();

}

package com.hartwig.serve.extraction;

import java.util.Set;

import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;

public interface KnownEvent {

    @NotNull
    Set<Knowledgebase> sources();

}

package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public interface ActionableEvent {

    @NotNull
    LocalDate sourceDate();

    @NotNull
    String sourceEvent();

    @NotNull
    Set<String> sourceUrls();
}

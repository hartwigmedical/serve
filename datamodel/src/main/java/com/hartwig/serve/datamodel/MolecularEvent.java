package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public interface MolecularEvent {

    @NotNull
    LocalDate sourceDate();

    @NotNull
    String sourceEvent();

    @NotNull
    Set<String> sourceUrls();
}

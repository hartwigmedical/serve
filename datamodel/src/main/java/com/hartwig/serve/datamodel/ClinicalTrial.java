package com.hartwig.serve.datamodel;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ClinicalTrial {

        @NotNull
        public abstract String studyNctId();

        @NotNull
        public abstract String studyTitle();

        @NotNull
        public abstract Set<String> countriesOfStudy();
}
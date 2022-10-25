package com.hartwig.serve.extraction.hotspot;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProteinResolver {

    @NotNull
    List<Hotspot> resolve(@NotNull String gene, @Nullable String specificTranscript, @NotNull String proteinAnnotation);

    @NotNull
    Set<String> unresolvedProteinAnnotations();
}

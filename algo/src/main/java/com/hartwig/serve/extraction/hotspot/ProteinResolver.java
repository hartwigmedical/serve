package com.hartwig.serve.extraction.hotspot;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.common.Variant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProteinResolver {

    @NotNull
    List<Variant> resolve(@NotNull String gene, @Nullable String specificTranscript, @NotNull String proteinAnnotation);

    @NotNull
    Set<String> unresolvedProteinAnnotations();
}

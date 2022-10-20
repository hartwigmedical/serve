package com.hartwig.serve.extraction.hotspot;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.hotspot.VariantHotspot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProteinResolver {

    @NotNull
    List<VariantHotspot> resolve(@NotNull String gene, @Nullable String specificTranscript, @NotNull String proteinAnnotation);

    @NotNull
    Set<String> unresolvedProteinAnnotations();
}

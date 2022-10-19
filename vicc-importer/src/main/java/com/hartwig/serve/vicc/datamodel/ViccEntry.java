package com.hartwig.serve.vicc.datamodel;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ViccEntry {

    @NotNull
    public abstract ViccSource source();

    // Transcript ID is not directly available in the data but is derived from kbSpecificObject
    @Nullable
    public abstract String transcriptId();

    @NotNull
    public abstract List<String> genes();

    @NotNull
    public abstract List<GeneIdentifier> geneIdentifiers();

    @NotNull
    public abstract List<String> featureNames();

    @NotNull
    public abstract List<Feature> features();

    @NotNull
    public abstract Association association();

    @NotNull
    public abstract List<String> tags();

    @NotNull
    public abstract List<String> devTags();

    @NotNull
    public abstract KbSpecificObject kbSpecificObject();
}


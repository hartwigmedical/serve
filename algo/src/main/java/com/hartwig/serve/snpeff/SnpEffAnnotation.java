package com.hartwig.serve.snpeff;

import java.util.List;

import com.hartwig.serve.common.variant.VariantConsequence;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class SnpEffAnnotation {

    private static final String FEATURE_TYPE_TRANSCRIPT = "transcript";

    @NotNull
    public abstract String gene(); // Ensembl gene name

    @NotNull
    public abstract String effects();

    @NotNull
    public abstract List<VariantConsequence> consequences();

    @NotNull
    public abstract String geneID(); // Ensembl gene ID

    @NotNull
    public abstract String featureType();

    @NotNull
    public abstract String featureID(); // Ensembl transcript name

    @NotNull
    public abstract String rank(); // exon rank

    @NotNull
    public abstract String hgvsCoding();

    @NotNull
    public abstract String hgvsProtein();

    @NotNull
    public String consequenceString() {
        return VariantConsequence.consequenceString(consequences());
    }

    // when we use the feature ID it is in practice always a transcript, but this mapping may not hold for every annotation
    @NotNull
    public String transcript() {
        String transcript = featureID();
        // In case transcripts appear with their version (e.g. ENST001.1) we strip the version part out.
        if (transcript.contains(".")) {
            return transcript.substring(0, transcript.indexOf("."));
        } else {
            return transcript;
        }
    }

    public boolean isTranscriptFeature() {
        return featureType().equals(FEATURE_TYPE_TRANSCRIPT);
    }
}

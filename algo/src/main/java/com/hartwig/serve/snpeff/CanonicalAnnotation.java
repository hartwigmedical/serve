package com.hartwig.serve.snpeff;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public class CanonicalAnnotation {

    @NotNull
    private final Set<String> driverGenes;
    @NotNull
    private final Set<String> canonicalTranscripts;

    public CanonicalAnnotation(@NotNull final Set<String> driverGenes, @NotNull final Set<String> canonicalTranscripts) {
        this.driverGenes = driverGenes;
        this.canonicalTranscripts = canonicalTranscripts;
    }

    @NotNull
    public Optional<SnpEffAnnotation> canonicalSnpEffAnnotation(@NotNull List<SnpEffAnnotation> allAnnotations) {
        List<SnpEffAnnotation> transcriptAnnotations = Lists.newArrayList();
        for (SnpEffAnnotation annotation : allAnnotations) {
            if (annotation.isTranscriptFeature()) {
                transcriptAnnotations.add(annotation);
            }
        }

        return pickCanonicalFavourDriverGene(transcriptAnnotations);
    }

    @NotNull
    private <T extends SnpEffAnnotation> Optional<T> pickCanonicalFavourDriverGene(@NotNull List<T> annotations) {
        List<T> canonicalAnnotations = Lists.newArrayList();
        for (T annotation : annotations) {
            if (canonicalTranscripts.contains(trimEnsembleVersion(annotation.transcript()))) {
                canonicalAnnotations.add(annotation);
            }
        }

        if (!canonicalAnnotations.isEmpty()) {
            Optional<T> canonicalOnDriverGene =
                    canonicalAnnotations.stream().filter(annotation -> driverGenes.contains(annotation.gene())).findFirst();
            if (canonicalOnDriverGene.isPresent()) {
                return canonicalOnDriverGene;
            }

            return Optional.of(canonicalAnnotations.get(0));
        }

        return Optional.empty();
    }

    @NotNull
    private static String trimEnsembleVersion(@NotNull String transcriptId) {
        if (transcriptId.startsWith("EN") && transcriptId.contains(".")) {
            return transcriptId.substring(0, transcriptId.indexOf("."));
        }

        return transcriptId;
    }
}

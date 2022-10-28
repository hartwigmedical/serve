package com.hartwig.serve.extraction.snpeff;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;

import org.jetbrains.annotations.NotNull;

public class CanonicalAnnotation {

    @NotNull
    private final Set<String> driverGenes;
    @NotNull
    private final Map<String, String> geneNamePerCanonicalTranscriptMap;

    public CanonicalAnnotation(@NotNull Set<String> driverGenes, @NotNull Map<String, String> geneNamePerCanonicalTranscriptMap) {
        this.driverGenes = driverGenes;
        this.geneNamePerCanonicalTranscriptMap = geneNamePerCanonicalTranscriptMap;
    }

    @NotNull
    public Optional<SnpEffAnnotation> canonicalSnpEffAnnotation(@NotNull List<SnpEffAnnotation> allAnnotations) {
        final List<SnpEffAnnotation> transcriptAnnotations =
                allAnnotations.stream().filter(SnpEffAnnotation::isTranscriptFeature).collect(Collectors.toList());

        return pickCanonicalFavourDriverGene(transcriptAnnotations);
    }

    @VisibleForTesting
    @NotNull
    <T extends SnpEffAnnotation> Optional<T> pickCanonicalFavourDriverGene(@NotNull List<T> annotations) {
        List<T> canonicalAnnotations = annotations.stream()
                .filter(annotation -> geneNamePerCanonicalTranscriptMap.containsKey(trimEnsembleVersion(annotation.transcript())))
                .collect(Collectors.toList());

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

package com.hartwig.serve.extraction.exon;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.datamodel.molecular.MutationType;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.EnsemblFunctions;
import com.hartwig.serve.extraction.util.GeneChecker;
import com.hartwig.serve.extraction.util.HmfExonRegion;
import com.hartwig.serve.extraction.util.HmfTranscriptRegion;
import com.hartwig.serve.extraction.util.MutationTypeFilterAlgo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExonExtractor {

    private static final Logger LOGGER = LogManager.getLogger(ExonExtractor.class);

    private static final int SPLICE_SIZE = 10;

    private static final Set<EventType> EXON_EVENTS = Sets.newHashSet(EventType.EXON, EventType.FUSION_PAIR_AND_EXON);

    @NotNull
    private final GeneChecker geneChecker;
    @NotNull
    private final MutationTypeFilterAlgo mutationTypeFilterAlgo;
    @NotNull
    private final EnsemblDataCache ensemblDataCache;
    @NotNull
    private final DriverInconsistencyMode driverInconsistencyMode;
    @NotNull
    private final List<DriverGene> driverGenes;

    public ExonExtractor(@NotNull final GeneChecker geneChecker, @NotNull final MutationTypeFilterAlgo mutationTypeFilterAlgo,
            @NotNull final EnsemblDataCache ensemblDataCache, @NotNull final DriverInconsistencyMode driverInconsistencyMode,
            @NotNull final List<DriverGene> driverGenes) {
        this.geneChecker = geneChecker;
        this.mutationTypeFilterAlgo = mutationTypeFilterAlgo;
        this.ensemblDataCache = ensemblDataCache;
        this.driverInconsistencyMode = driverInconsistencyMode;
        this.driverGenes = driverGenes;
    }

    static boolean geneInDriverGenes(@NotNull List<DriverGene> driverGenes, @NotNull String gene) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(gene)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public List<ExonAnnotation> extract(@NotNull String gene, @Nullable String transcriptId, @NotNull EventType type,
            @NotNull String event) {
        if (EXON_EVENTS.contains(type) && geneChecker.isValidGene(gene)) {
            boolean geneInDriverGenesDatabase = geneInDriverGenes(driverGenes, gene);
            if (!geneInDriverGenesDatabase && driverInconsistencyMode.isActive()) {
                if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                    LOGGER.warn("Exon event on {} on {} is not included in driver catalog and won't ever be reported.", type, gene);
                } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                    LOGGER.info("Exon event filtered -- {} on {} is not included in driver catalog and won't ever be reported.",
                            type,
                            gene);
                    return null;
                }
            }

            HmfTranscriptRegion canonicalTranscript = EnsemblFunctions.findCanonicalTranscript(ensemblDataCache, gene);
            assert canonicalTranscript != null;

            if (transcriptId == null || transcriptId.equals(canonicalTranscript.transcriptId())) {
                List<Integer> exonRanks = extractExonIndices(event);
                if (exonRanks == null) {
                    LOGGER.warn("Could not extract exon indices from '{}'", event);
                    return null;
                }

                MutationType applicableMutationType = mutationTypeFilterAlgo.determine(gene, event);

                List<ExonAnnotation> annotations = Lists.newArrayList();
                for (int exonRank : exonRanks) {
                    ExonAnnotation annotation = determineExonAnnotation(gene,
                            canonicalTranscript,
                            exonRank,
                            applicableMutationType,
                            canonicalTranscript.transcriptId());
                    if (annotation != null) {
                        annotations.add(annotation);
                    } else {
                        LOGGER.warn("Could not determine exon annotation for exon rank {} on transcript '{}' on '{}'",
                                exonRank,
                                canonicalTranscript.transcriptId(),
                                gene);
                    }
                }
                return !annotations.isEmpty() ? annotations : null;
            } else {
                LOGGER.warn("Transcript IDs not equal for provided transcript '{}' and HMF canonical transcript '{}' for {} ",
                        transcriptId,
                        canonicalTranscript.transcriptId(),
                        event);
            }
        }

        return null;
    }

    @Nullable
    @VisibleForTesting
    static List<Integer> extractExonIndices(@NotNull String event) {
        List<Integer> exons = Lists.newArrayList();
        if (event.contains(" or ") || event.contains(" & ")) {
            exons = extractMultipleExonIndices(event);
        } else if (event.contains("-")) {
            exons = extractContinuousRangeOfExonIndices(event);
        } else {
            String[] words = event.split(" ");
            for (String word : words) {
                if (isInteger(word)) {
                    exons.add(Integer.valueOf(word));
                }
            }
        }

        return !exons.isEmpty() ? exons : null;
    }

    @NotNull
    private static List<Integer> extractMultipleExonIndices(@NotNull String event) {
        List<Integer> exonIndices = Lists.newArrayList();
        String[] words = event.replace(" or ", ",").replace(" & ", ",").replace(")", "").split(" ");
        for (String word : words) {
            if (word.contains(",")) {
                String[] exons = word.split(",");
                for (String exon : exons) {
                    exonIndices.add(Integer.valueOf(exon));
                }
            }
        }
        return exonIndices;
    }

    @NotNull
    private static List<Integer> extractContinuousRangeOfExonIndices(@NotNull String event) {
        List<Integer> exonIndices = Lists.newArrayList();
        String[] words = event.split(" ");
        for (String word : words) {
            if (word.contains("-")) {
                String[] splitEvents = word.split("-");
                int eventStart = Integer.parseInt(splitEvents[0]);
                String eventEndString =
                        splitEvents[1].endsWith(")") ? splitEvents[1].substring(0, splitEvents[1].length() - 1) : splitEvents[1];
                int eventEnd = Integer.parseInt(eventEndString);
                for (int i = eventStart; i <= eventEnd; i++) {
                    exonIndices.add(i);
                }
            }
        }
        return exonIndices;
    }

    @Nullable
    private static ExonAnnotation determineExonAnnotation(@NotNull String gene, @NotNull HmfTranscriptRegion transcript, int exonRank,
            @NotNull MutationType applicableMutationType, @NotNull String canonicalTranscriptID) {
        HmfExonRegion hmfExonRegion = transcript.exonByIndex(exonRank);

        if (hmfExonRegion == null) {
            return null;
        }

        // Extend exonic range to include SPLICE variants.
        // First exon does not start with a splice region, but we don't take this into account since it would not matter downstream anyway.
        int start = hmfExonRegion.start() - SPLICE_SIZE;
        int end = hmfExonRegion.end() + SPLICE_SIZE;

        return ImmutableExonAnnotation.builder()
                .gene(gene)
                .chromosome(hmfExonRegion.chromosome())
                .start(start)
                .end(end)
                .applicableMutationType(applicableMutationType)
                .inputTranscript(canonicalTranscriptID)
                .inputExonRank(exonRank)
                .build();
    }

    private static boolean isInteger(@NotNull String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
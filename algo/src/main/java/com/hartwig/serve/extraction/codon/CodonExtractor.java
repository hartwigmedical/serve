package com.hartwig.serve.extraction.codon;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.datamodel.molecular.MutationType;
import com.hartwig.serve.datamodel.molecular.common.GenomeRegion;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.EnsemblFunctions;
import com.hartwig.serve.extraction.util.GeneChecker;
import com.hartwig.serve.extraction.util.HmfTranscriptRegion;
import com.hartwig.serve.extraction.util.HmfTranscriptRegionUtils;
import com.hartwig.serve.extraction.util.MutationTypeFilterAlgo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodonExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CodonExtractor.class);

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

    public CodonExtractor(@NotNull final GeneChecker geneChecker, @NotNull final MutationTypeFilterAlgo mutationTypeFilterAlgo,
            @NotNull final EnsemblDataCache ensemblDataCache, @NotNull final DriverInconsistencyMode driverInconsistencyMode,
            @NotNull final List<DriverGene> driverGenes) {
        this.geneChecker = geneChecker;
        this.mutationTypeFilterAlgo = mutationTypeFilterAlgo;
        this.ensemblDataCache = ensemblDataCache;
        this.driverInconsistencyMode = driverInconsistencyMode;
        this.driverGenes = driverGenes;
    }

    @Nullable
    public List<CodonAnnotation> extract(@NotNull String gene, @Nullable String transcriptId, @NotNull EventType type,
            @NotNull String event) {
        if (type == EventType.CODON && geneChecker.isValidGene(gene)) {
            boolean geneInDriverGenesDatabase = geneInDriverGenes(driverGenes, gene);
            if (!geneInDriverGenesDatabase && driverInconsistencyMode.isActive()) {
                if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                    LOGGER.warn("Codon event on {} on {} is not included in driver catalog and won't ever be reported.", type, gene);
                } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                    LOGGER.info("Codon event filtered -- {} on {} is not included in driver catalog and won't ever be reported.",
                            type,
                            gene);
                    return null;
                }
            }

            HmfTranscriptRegion canonicalTranscript = EnsemblFunctions.findCanonicalTranscript(ensemblDataCache, gene);
            assert canonicalTranscript != null;

            if (transcriptId == null || transcriptId.equals(canonicalTranscript.transcriptId())) {
                Integer codonRank = extractCodonRank(event);
                if (codonRank == null) {
                    LOGGER.warn("Could not extract codon rank from '{}'", event);
                    return null;
                }

                MutationType applicableMutationType = mutationTypeFilterAlgo.determine(gene, event);
                List<CodonAnnotation> codonAnnotations =
                        determineCodonAnnotations(gene, canonicalTranscript, codonRank, applicableMutationType);

                if (codonAnnotations == null) {
                    LOGGER.warn("Could not resolve codon rank {} on transcript '{}' for gene '{}'",
                            codonRank,
                            canonicalTranscript.transcriptId(),
                            gene);
                }

                return codonAnnotations;
            } else {
                LOGGER.warn("Transcript IDs not equal for provided transcript '{}' and HMF canonical transcript '{}' for {} ",
                        transcriptId,
                        canonicalTranscript.transcriptId(),
                        event);
            }
        }

        return null;
    }

    @VisibleForTesting
    static boolean geneInDriverGenes(@NotNull List<DriverGene> driverGenes, @NotNull String gene) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(gene)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @VisibleForTesting
    static Integer extractCodonRank(@NotNull String event) {
        String codonPart;
        if (event.contains(" ")) {
            codonPart = event.split(" ")[1];
        } else {
            codonPart = event;
        }
        codonPart = codonPart.replaceAll("\\D+", "");
        if (isInteger(codonPart)) {
            return Integer.parseInt(codonPart);
        }

        return null;
    }

    @Nullable
    private static List<CodonAnnotation> determineCodonAnnotations(@NotNull String gene, @NotNull HmfTranscriptRegion canonicalTranscript,
            @Nullable Integer codonRank, @NotNull MutationType applicableMutationType) {
        List<GenomeRegion> regions = Lists.newArrayList();
        if (codonRank != null) {
            regions = HmfTranscriptRegionUtils.codonRangeByRank(canonicalTranscript, codonRank, codonRank);
        }

        if (regions != null) {
            List<CodonAnnotation> codonAnnotations = Lists.newArrayList();
            for (GenomeRegion region : regions) {
                codonAnnotations.add(ImmutableCodonAnnotation.builder()
                        .gene(gene)
                        .chromosome(region.chromosome())
                        .start(region.start())
                        .end(region.end())
                        .applicableMutationType(applicableMutationType)
                        .inputTranscript(canonicalTranscript.transcriptId())
                        .inputCodonRank(codonRank)
                        .build());
            }
            return codonAnnotations;
        }

        return null;
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

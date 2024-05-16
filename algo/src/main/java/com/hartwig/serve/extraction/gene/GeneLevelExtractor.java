package com.hartwig.serve.extraction.gene;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivergene.DriverCategory;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneLevelExtractor {

    private static final Logger LOGGER = LogManager.getLogger(GeneLevelExtractor.class);

    @NotNull
    private final GeneChecker exomeGeneChecker;
    @NotNull
    private final GeneChecker fusionGeneChecker;
    @NotNull
    private final List<DriverGene> driverGenes;
    @NotNull
    private final KnownFusionCache knownFusionCache;
    @NotNull
    private final Set<String> activationKeyPhrases;
    @NotNull
    private final Set<String> inactivationKeyPhrases;
    @NotNull
    private final Set<String> genericKeyPhrases;
    private final DriverInconsistencyMode driverInconsistencyMode;

    public GeneLevelExtractor(@NotNull final GeneChecker exomeGeneChecker, @NotNull final GeneChecker fusionGeneChecker,
            @NotNull final List<DriverGene> driverGenes, @NotNull final KnownFusionCache knownFusionCache,
            @NotNull final Set<String> activationKeyPhrases, @NotNull final Set<String> inactivationKeyPhrases,
            @NotNull final Set<String> genericKeyPhrases, @NotNull DriverInconsistencyMode driverInconsistencyMode) {
        this.exomeGeneChecker = exomeGeneChecker;
        this.fusionGeneChecker = fusionGeneChecker;
        this.driverGenes = driverGenes;
        this.knownFusionCache = knownFusionCache;
        this.activationKeyPhrases = activationKeyPhrases;
        this.inactivationKeyPhrases = inactivationKeyPhrases;
        this.driverInconsistencyMode = driverInconsistencyMode;
        this.genericKeyPhrases = genericKeyPhrases;
    }

    @Nullable
    public GeneAnnotation extract(@NotNull String gene, @NotNull EventType type, @NotNull String event) {
        if (exomeGeneChecker.isValidGene(gene) || fusionGeneChecker.isValidGene(gene)) {
            switch (type){
                case WILD_TYPE:
                    return extractWildTypeEvent(gene, type);
                case GENE_LEVEL:
                    return extractGeneLevelEvent(gene, event);
                case PROMISCUOUS_FUSION:
                    return extractPromiscuousFusion(gene);
                case ABSENCE_OF_PROTEIN:
                    return extractNegativePositiveEvent(gene, GeneEvent.ABSENCE_OF_PROTEIN, type);
                case PRESENCE_OF_PROTEIN:
                    return extractNegativePositiveEvent(gene, GeneEvent.PRESENCE_OF_PROTEIN, type);
                default:
                    LOGGER.warn("Unrecognized event type : '{}'", type);
                    return null;
            }
        }
        return null;
    }

    @Nullable
    GeneAnnotation extractPromiscuousFusion(@NotNull String gene) {
        if (driverInconsistencyMode.isActive() && !geneIsPresentInFusionCache(gene)) {
            if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                LOGGER.warn("Promiscuous fusion '{}' is not present in the known fusion cache", gene);
            } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                LOGGER.info("Promiscuous fusion filtered -- Promiscuous fusion '{}' is not present in the known fusion cache", gene);
                return null;
            }
        }

        return ImmutableGeneAnnotationImpl.builder().gene(gene).event(GeneEvent.FUSION).build();
    }

    @Nullable
    GeneAnnotation extractWildTypeEvent(@NotNull String gene, @NotNull EventType type) {
        boolean geneInDriverGenesDatabase = geneInDriverGenes(driverGenes, gene);

        if (!geneInDriverGenesDatabase && driverInconsistencyMode.isActive()) {
            if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                LOGGER.warn("Wildtype event {} on {} is not included in driver catalog and won't ever be reported.", type, gene);
            } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                LOGGER.info("Wildtype event filtered -- {} on {} is not included in driver catalog and won't ever be reported.",
                        type,
                        gene);
                return null;
            }
        }

        return ImmutableGeneAnnotationImpl.builder().gene(gene).event(GeneEvent.WILD_TYPE).build();
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
    @VisibleForTesting
    GeneAnnotation extractNegativePositiveEvent(@NotNull String gene, @NotNull GeneEvent geneEvent, @NotNull EventType type) {
        boolean geneInDriverGenesDatabase = geneInDriverGenes(driverGenes, gene);
        if (!geneInDriverGenesDatabase && driverInconsistencyMode.isActive()) {
            if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                LOGGER.warn("{} event {} on {} is not included in driver catalog and won't ever be reported.", geneEvent, type, gene);
            } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                LOGGER.info("{}  event filtered -- {} on {} is not included in driver catalog and won't ever be reported.",
                        geneEvent,
                        type,
                        gene);
                return null;
            }
        }

        return ImmutableGeneAnnotationImpl.builder().gene(gene).event(geneEvent).build();

    }

    @Nullable
    @VisibleForTesting
    GeneAnnotation extractGeneLevelEvent(@NotNull String gene, @NotNull String event) {
        GeneEvent result = GeneEvent.ANY_MUTATION;
        for (String keyPhrase : genericKeyPhrases) {
            if (event.contains(keyPhrase)) {
                result = GeneEvent.ANY_MUTATION;
                break;
            }
        }
        for (String keyPhrase : activationKeyPhrases) {
            if (event.contains(keyPhrase)) {
                result = GeneEvent.ACTIVATION;
                break;
            }
        }
        for (String keyPhrase : inactivationKeyPhrases) {
            if (event.contains(keyPhrase)) {
                result = GeneEvent.INACTIVATION;
                break;
            }
        }

        GeneEvent driverBasedEvent = determineGeneLevelEventFromDriverGenes(driverGenes, gene);

        if (driverInconsistencyMode.isActive()) {
            if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                if (!geneInDriverGenes(driverGenes, gene)) {
                    LOGGER.warn("Gene level event on gene {} not present in driver catalog. {} will never be reported", gene, result);
                } else if (geneInDriverGenes(driverGenes, gene) && result != GeneEvent.ANY_MUTATION && result != driverBasedEvent) {
                    LOGGER.warn(
                            "Gene level event mismatch in driver gene event for '{}'. Event suggests {} while driver catalog suggests {}",
                            gene,
                            result,
                            driverBasedEvent);
                }
            } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                if (!geneInDriverGenes(driverGenes, gene)) {
                    LOGGER.info("Gene level event filtered -- {} on {} is not included in driver catalog and won't ever be reported.",
                            result,
                            gene);
                    return null;
                } else if (geneInDriverGenes(driverGenes, gene) && result != GeneEvent.ANY_MUTATION && result != driverBasedEvent) {
                    LOGGER.info("Gene level event filtered -- Mismatch in driver gene event for '{}'. "
                            + "Event suggests {} while driver catalog suggests {}", gene, result, driverBasedEvent);
                    return null;
                }
            }
        }

        return ImmutableGeneAnnotationImpl.builder().gene(gene).event(result).build();
    }

    @NotNull
    @VisibleForTesting
    static GeneEvent determineGeneLevelEventFromDriverGenes(@NotNull List<DriverGene> driverGenes, @NotNull String gene) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(gene)) {
                if (driverGene.likelihoodType() == DriverCategory.ONCO) {
                    return GeneEvent.ACTIVATION;
                } else if (driverGene.likelihoodType() == DriverCategory.TSG) {
                    return GeneEvent.INACTIVATION;
                }
            }
        }
        return GeneEvent.ANY_MUTATION;
    }

    private boolean geneIsPresentInFusionCache(@NotNull String gene) {
        return knownFusionCache.hasKnownPairGene(gene) || knownFusionCache.hasPromiscuousFiveGene(gene)
                || knownFusionCache.hasPromiscuousThreeGene(gene) || knownFusionCache.hasAnyIgFusion(gene);
    }
}
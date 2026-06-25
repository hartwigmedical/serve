package com.hartwig.serve.extraction.fusion;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FusionExtractor {

    private static final Logger LOGGER = LogManager.getLogger(FusionExtractor.class);

    @NotNull
    private final GeneChecker geneChecker;
    @NotNull
    private final KnownFusionCache knownFusionCache;
    @NotNull
    private final Set<String> exonicDelDupFusionKeyPhrases;
    private final DriverInconsistencyMode driverInconsistencyMode;

    public FusionExtractor(@NotNull final GeneChecker geneChecker, @NotNull final KnownFusionCache knownFusionCache,
            @NotNull final Set<String> exonicDelDupFusionKeyPhrases, @NotNull final DriverInconsistencyMode driverInconsistencyMode) {
        this.geneChecker = geneChecker;
        this.knownFusionCache = knownFusionCache;
        this.exonicDelDupFusionKeyPhrases = exonicDelDupFusionKeyPhrases;
        this.driverInconsistencyMode = driverInconsistencyMode;
    }

    @Nullable
    public FusionPair extract(@NotNull String gene, @NotNull EventType type, @NotNull String event) {
        if (type == EventType.FUSION_PAIR) {
            if (FusionAnnotationConfig.EXONIC_FUSIONS_MAP.containsKey(event)) {
                return fromConfiguredPair(FusionAnnotationConfig.EXONIC_FUSIONS_MAP.get(event), gene);
            } else if (hasExonicDelDupKeyPhrase(event)) {
                return validate(fromExonicDelDup(gene, event));
            } else {
                return validate(fromStandardFusionPairEvent(event));
            }
        } else if (type == EventType.FUSION_PAIR_AND_EXON) {
            return validate(fromExonicDelDup(gene, event));
        } else {
            return null;
        }
    }

    private boolean hasExonicDelDupKeyPhrase(@NotNull String event) {
        for (String keyPhrase : exonicDelDupFusionKeyPhrases) {
            if (event.contains(keyPhrase)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    private static FusionPair fromExonicDelDup(@NotNull String gene, @NotNull String event) {
        Integer exonRankUp = null;
        Integer exonRankDown = null;

        if (event.contains("-")) {
            String[] wordsUp = event.split("-");
            String[] wordsDown = event.split("-");
            if (wordsUp.length == 2 && wordsDown.length == 2) {
                exonRankUp = extractExonRank(event, wordsUp[0].split(" "));
                exonRankDown = extractExonRank(event, wordsDown[1].split(" "));
            }
        } else {
            String[] wordsUp = event.split(" ");
            String[] wordsDown = event.split(" ");
            exonRankUp = extractExonRank(event, wordsUp);
            exonRankDown = extractExonRank(event, wordsDown);
        }

        if (exonRankUp == null || exonRankDown == null) {
            return null;
        }

        int exonUp = exonRankUp - 1;
        int exonDown = exonRankDown + 1;

        return ImmutableFusionPairImpl.builder()
                .geneUp(gene)
                .minExonUp(exonUp)
                .maxExonUp(exonUp)
                .geneDown(gene)
                .minExonDown(exonDown)
                .maxExonDown(exonDown)
                .build();
    }

    @Nullable
    private static Integer extractExonRank(@NotNull String event, @NotNull String[] words) {
        List<Integer> exons = Lists.newArrayList();

        for (String word : words) {
            if (isInteger(word)) {
                exons.add(Integer.valueOf(word));
            }
        }
        if (exons.size() > 1) {
            LOGGER.warn("Multiple exon ranks extracted from '{}' while expecting 1", event);
            return null;
        } else if (exons.isEmpty()) {
            LOGGER.warn("No exon rank could be resolved from '{}'", event);
            return null;
        }

        return exons.get(0);
    }

    @Nullable
    private FusionPair fromStandardFusionPairEvent(@NotNull String event) {
        String[] fusionArray = event.split("-");
        String geneUp = null;
        String geneDown = null;
        if (fusionArray.length == 2) {
            geneUp = fusionArray[0];
            geneDown = fusionArray[1].split(" ")[0];
        } else if (fusionArray.length == 3) {
            String geneUpScenario1 = fusionArray[0] + "-" + fusionArray[1];
            String geneDownScenario1 = fusionArray[2].split(" ")[0];
            String geneUpScenario2 = fusionArray[0];
            String geneDownScenario2 = fusionArray[1] + "-" + fusionArray[2].split(" ")[0];

            if (geneChecker.geneExistsInAllValidGenes(geneUpScenario1) && geneChecker.geneExistsInAllValidGenes(geneDownScenario1)) {
                geneUp = geneUpScenario1;
                geneDown = geneDownScenario1;
            } else if (geneChecker.geneExistsInAllValidGenes(geneUpScenario2) && geneChecker.geneExistsInAllValidGenes(geneDownScenario2)) {
                geneUp = geneUpScenario2;
                geneDown = geneDownScenario2;
            }
        } else if (fusionArray.length == 4) {
            geneUp = fusionArray[0] + "-" + fusionArray[1];
            geneDown = fusionArray[2] + "-" + fusionArray[3].split(" ")[0];
        }

        if (geneUp == null || geneDown == null) {
            LOGGER.warn("Could not resolve fusion pair from '{}'", event);
            return null;
        }

        return ImmutableFusionPairImpl.builder().geneUp(removeAllSpaces(geneUp)).geneDown(removeAllSpaces(geneDown)).build();
    }

    private static boolean isInteger(@NotNull String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Nullable
    private FusionPair fromConfiguredPair(@NotNull FusionPair configuredPair, @NotNull String gene) {
        FusionPair pair = ImmutableFusionPairImpl.builder().from(configuredPair).build();
        FusionPair pairValidated = validate(pair);
        if (pairValidated != null) {
            if (!pairValidated.geneUp().equals(gene) || !pairValidated.geneDown().equals(gene)) {
                LOGGER.warn("Preconfigured fusion '{}' does not match on gene level: {}", configuredPair, gene);
                return null;
            }
        }

        return pair;
    }

    @Nullable
    private FusionPair validate(@Nullable FusionPair pair) {
        if (pair == null) {
            return null;
        }

        if (geneChecker.isValidGene(pair.geneUp()) && geneChecker.isValidGene(pair.geneDown())) {
            if (driverInconsistencyMode.isActive() && !isIncludedSomewhereInFusionCache(pair.geneUp(), pair.geneDown())) {
                if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                    LOGGER.warn("Fusion event on fusion '{}-{}' is not part of the known fusion cache", pair.geneUp(), pair.geneDown());
                } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                    LOGGER.info("Fusion event filtered -- Fusion '{}-{}' is not part of the known fusion cache",
                            pair.geneUp(),
                            pair.geneDown());
                    return null;
                }
            }
            return pair;
        }

        return null;
    }

    private boolean isIncludedSomewhereInFusionCache(@NotNull String fiveGene, @NotNull String threeGene) {
        return knownFusionCache.hasAnyExonDelDup(fiveGene) || knownFusionCache.hasPromiscuousFiveGene(fiveGene)
                || knownFusionCache.hasPromiscuousThreeGene(threeGene) || knownFusionCache.hasKnownFusion(fiveGene, threeGene)
                || knownFusionCache.hasKnownIgFusion(fiveGene, threeGene) || knownFusionCache.hasPromiscuousIgFusion(fiveGene);
    }

    @NotNull
    private static String removeAllSpaces(@NotNull String value) {
        return value.replaceAll("\\s+", "");
    }
}
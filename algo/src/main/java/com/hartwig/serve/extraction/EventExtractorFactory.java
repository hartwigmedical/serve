package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.ensemblcache.GeneData;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.common.knownfusion.KnownFusionData;
import com.hartwig.serve.common.knownfusion.KnownFusionType;
import com.hartwig.serve.extraction.characteristic.TumorCharacteristicExtractor;
import com.hartwig.serve.extraction.codon.CodonExtractor;
import com.hartwig.serve.extraction.copynumber.CopyNumberExtractor;
import com.hartwig.serve.extraction.exon.ExonExtractor;
import com.hartwig.serve.extraction.fusion.FusionExtractor;
import com.hartwig.serve.extraction.gene.GeneLevelExtractor;
import com.hartwig.serve.extraction.hotspot.HotspotExtractor;
import com.hartwig.serve.extraction.immuno.ImmunoHLAExtractor;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;
import com.hartwig.serve.extraction.util.MutationTypeFilterAlgo;
import com.hartwig.serve.refgenome.RefGenomeResource;

import org.jetbrains.annotations.NotNull;

public final class EventExtractorFactory {

    private EventExtractorFactory() {
    }

    @NotNull
    public static EventExtractor create(@NotNull EventClassifierConfig config, @NotNull RefGenomeResource refGenomeResource,
            @NotNull DriverInconsistencyMode driverInconsistencyMode) {
        Set<String> genesInExome = extractAllValidGenes(refGenomeResource.ensemblDataCache());
        GeneChecker exomeGeneChecker = new GeneChecker(genesInExome);

        Set<String> fusionGeneSet = Sets.newHashSet();
        fusionGeneSet.addAll(genesInExome);
        fusionGeneSet.addAll(extractAllGenesInvolvedInFusions(refGenomeResource.knownFusionCache()));
        GeneChecker fusionGeneChecker = new GeneChecker(fusionGeneSet);

        MutationTypeFilterAlgo mutationTypeFilterAlgo = new MutationTypeFilterAlgo(refGenomeResource.driverGenes());
        return new EventExtractor(new HotspotExtractor(exomeGeneChecker,
                refGenomeResource.proteinResolver(),
                config.proteinAnnotationExtractor(),
                driverInconsistencyMode,
                refGenomeResource.driverGenes()),
                new CodonExtractor(exomeGeneChecker,
                        mutationTypeFilterAlgo,
                        refGenomeResource.ensemblDataCache(),
                        driverInconsistencyMode,
                        refGenomeResource.driverGenes()),
                new ExonExtractor(exomeGeneChecker,
                        mutationTypeFilterAlgo,
                        refGenomeResource.ensemblDataCache(),
                        driverInconsistencyMode,
                        refGenomeResource.driverGenes()),
                new GeneLevelExtractor(exomeGeneChecker,
                        fusionGeneChecker,
                        refGenomeResource.driverGenes(),
                        refGenomeResource.knownFusionCache(),
                        config.activatingGeneLevelKeyPhrases(),
                        config.inactivatingGeneLevelKeyPhrases(),
                        config.genericGeneLevelKeyPhrases(),
                        driverInconsistencyMode),
                new CopyNumberExtractor(exomeGeneChecker, refGenomeResource.driverGenes(), driverInconsistencyMode),
                new FusionExtractor(fusionGeneChecker,
                        refGenomeResource.knownFusionCache(),
                        config.exonicDelDupFusionKeyPhrases(),
                        driverInconsistencyMode),
                new TumorCharacteristicExtractor(config.microsatelliteUnstableKeyPhrases(),
                        config.microsatelliteStableKeyPhrases(),
                        config.highTumorMutationalLoadKeyPhrases(),
                        config.lowTumorMutationalLoadKeyPhrases(),
                        config.highTumorMutationalBurdenKeyPhrases(),
                        config.lowTumorMutationalBurdenKeyPhrases(),
                        config.hrDeficiencyKeyPhrases(),
                        config.hpvPositiveEvents(),
                        config.ebvPositiveEvents()),
                new ImmunoHLAExtractor());
    }

    @NotNull
    private static Set<String> extractAllValidGenes(@NotNull EnsemblDataCache ensemblDataCache) {
        Set<String> genes = Sets.newHashSet();
        for (List<GeneData> genesPerChromosome : ensemblDataCache.genesPerChromosome().values()) {
            for (GeneData geneData : genesPerChromosome) {
                genes.add(geneData.geneName());
            }
        }
        return genes;
    }

    @NotNull
    private static Set<String> extractAllGenesInvolvedInFusions(@NotNull KnownFusionCache knownFusionCache) {
        Set<String> genes = Sets.newHashSet();
        for (KnownFusionData fusion : knownFusionCache.knownFusions()) {
            if (fusion.type() == KnownFusionType.KNOWN_PAIR || fusion.type() == KnownFusionType.IG_KNOWN_PAIR
                    || fusion.type() == KnownFusionType.EXON_DEL_DUP) {
                genes.add(fusion.fiveGene());
                genes.add(fusion.threeGene());
            } else if (fusion.type() == KnownFusionType.PROMISCUOUS_5 || fusion.type() == KnownFusionType.IG_PROMISCUOUS) {
                genes.add(fusion.fiveGene());
            } else if (fusion.type() == KnownFusionType.PROMISCUOUS_3) {
                genes.add(fusion.threeGene());
            }
        }
        return genes;
    }
}
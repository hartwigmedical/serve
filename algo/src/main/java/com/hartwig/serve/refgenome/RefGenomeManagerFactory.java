package com.hartwig.serve.refgenome;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.ServeConfig;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.common.drivergene.DriverGeneFile;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.ensemblcache.EnsemblDataLoader;
import com.hartwig.serve.common.ensemblcache.GeneData;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.common.knownfusion.KnownFusionCacheLoader;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.variant.ProteinResolver;
import com.hartwig.serve.extraction.variant.ProteinResolverFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public final class RefGenomeManagerFactory {

    private static final Logger LOGGER = LogManager.getLogger(RefGenomeManagerFactory.class);

    private RefGenomeManagerFactory() {
    }

    @NotNull
    public static RefGenomeManager createFromServeConfig(@NotNull ServeConfig config) throws IOException {
        Map<RefGenome, RefGenomeResource> refGenomeResourceMap = Maps.newHashMap();
        refGenomeResourceMap.put(RefGenome.V37, buildRefGenomeResource37(config));
        refGenomeResourceMap.put(RefGenome.V38, buildRefGenomeResource38(config));
        return new RefGenomeManager(refGenomeResourceMap);
    }

    @NotNull
    private static RefGenomeResource buildRefGenomeResource37(@NotNull ServeConfig config) throws IOException {
        String fastaFile37 = config.refGenome37FastaFile();
        LOGGER.info("Creating ref genome resource for V37 using fasta {}", fastaFile37);
        EnsemblDataCache ensemblDataCache37 = loadEnsemblDataCache(RefGenome.V37, config.ensemblDataDir37());
        ProteinResolver proteinResolver37 = config.skipVariantResolving()
                ? ProteinResolverFactory.dummy()
                : ProteinResolverFactory.transvarWithRefGenome(RefGenome.V37, fastaFile37, ensemblDataCache37);

        return ImmutableRefGenomeResource.builder()
                .refSequence(new IndexedFastaSequenceFile(new File(fastaFile37)))
                .driverGenes(readDriverGenesFromFile(config.driverGene37Tsv()))
                .knownFusionCache(buildKnownFusionCacheFromFile(config.knownFusion37File()))
                .ensemblDataCache(ensemblDataCache37)
                .putChainToOtherRefGenomeMap(RefGenome.V38, config.refGenome37To38Chain())
                .proteinResolver(proteinResolver37)
                .build();
    }

    @NotNull
    private static RefGenomeResource buildRefGenomeResource38(@NotNull ServeConfig config) throws IOException {
        String fastaFile38 = config.refGenome38FastaFile();
        LOGGER.info("Creating ref genome resource for V38 using fasta {}", fastaFile38);
        EnsemblDataCache ensemblDataCache38 = loadEnsemblDataCache(RefGenome.V38, config.ensemblDataDir38());
        ProteinResolver proteinResolver38 = config.skipVariantResolving()
                ? ProteinResolverFactory.dummy()
                : ProteinResolverFactory.transvarWithRefGenome(RefGenome.V38, fastaFile38, ensemblDataCache38);

        return ImmutableRefGenomeResource.builder()
                .refSequence(new IndexedFastaSequenceFile(new File(fastaFile38)))
                .driverGenes(readDriverGenesFromFile(config.driverGene38Tsv()))
                .knownFusionCache(buildKnownFusionCacheFromFile(config.knownFusion38File()))
                .ensemblDataCache(ensemblDataCache38)
                .putChainToOtherRefGenomeMap(RefGenome.V37, config.refGenome38To37Chain())
                .proteinResolver(proteinResolver38)
                .build();
    }

    @NotNull
    private static List<DriverGene> readDriverGenesFromFile(@NotNull String driverGeneTsv) throws IOException {
        LOGGER.info(" Reading driver genes from {}", driverGeneTsv);
        List<DriverGene> driverGenes = DriverGeneFile.read(driverGeneTsv);
        LOGGER.info("  Read {} driver gene entries", driverGenes.size());
        return driverGenes;
    }

    @NotNull
    public static KnownFusionCache buildKnownFusionCacheFromFile(@NotNull String knownFusionFile) throws IOException {
        LOGGER.info(" Reading known fusions from {}", knownFusionFile);
        KnownFusionCache cache = KnownFusionCacheLoader.load(knownFusionFile);
        LOGGER.info("  Read {} known fusion entries", cache.knownFusions().size());
        return cache;
    }

    @NotNull
    private static EnsemblDataCache loadEnsemblDataCache(@NotNull RefGenome refGenome, @NotNull String ensemblDataDir) throws IOException {
        LOGGER.info(" Reading ensembl data cache from {}", ensemblDataDir);
        EnsemblDataCache ensemblDataCache = EnsemblDataLoader.load(ensemblDataDir, refGenome);
        int geneCount = 0;
        for (List<GeneData> genesPerChromosome : ensemblDataCache.genesPerChromosome().values()) {
            geneCount += genesPerChromosome.size();
        }
        LOGGER.info("  Loaded entries for {} genes", geneCount);
        return ensemblDataCache;
    }
}

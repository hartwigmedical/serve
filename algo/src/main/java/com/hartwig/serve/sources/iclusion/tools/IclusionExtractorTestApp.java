package com.hartwig.serve.sources.iclusion.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.hartwig.serve.ServeConfig;
import com.hartwig.serve.ServeLocalConfigProvider;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.common.drivergene.DriverGeneFile;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.ensemblcache.EnsemblDataLoader;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.common.knownfusion.KnownFusionCacheLoader;
import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.curation.DoidLookupFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ExtractionResultWriter;
import com.hartwig.serve.extraction.hotspot.ProteinResolverFactory;
import com.hartwig.serve.iclusion.classification.IclusionClassificationConfig;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.refgenome.ImmutableRefGenomeResource;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.iclusion.IclusionExtractor;
import com.hartwig.serve.sources.iclusion.IclusionExtractorFactory;
import com.hartwig.serve.sources.iclusion.IclusionReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class IclusionExtractorTestApp {

    private static final Logger LOGGER = LogManager.getLogger(IclusionExtractorTestApp.class);

    private static final String VERSION = IclusionExtractorTestApp.class.getPackage().getImplementationVersion();

    public static void main(String[] args) throws IOException {
        Configurator.setRootLevel(Level.INFO);

        ServeConfig config = ServeLocalConfigProvider.create();

        Path outputPath = new File(config.outputDir()).toPath();
        if (!Files.exists(outputPath)) {
            LOGGER.info("Creating {} directory for writing SERVE output", outputPath.toString());
            Files.createDirectory(outputPath);
        }

        DoidLookup doidLookup = DoidLookupFactory.buildFromMappingTsv(config.missingDoidsMappingTsv());

        RefGenomeResource refGenomeResource = buildRefGenomeResource(config);
        IclusionExtractor extractor = IclusionExtractorFactory.create(IclusionClassificationConfig.build(), refGenomeResource, doidLookup);

        List<IclusionTrial> trials = IclusionReader.readAndCurate(config.iClusionTrialTsv(), config.iClusionFilterTsv());
        ExtractionResult result = extractor.extract(trials);

        IclusionUtil.printIclusionResult(result);
        String iclusionMutationTsv = config.outputDir() + File.separator + "IclusionEventClassification.tsv";
        IclusionUtil.writeIclusionMutationTypes(iclusionMutationTsv, trials);

        new ExtractionResultWriter(config.outputDir(),
                Knowledgebase.ICLUSION.refGenomeVersion(),
                refGenomeResource.refSequence(),
                VERSION).write(
                result);
    }

    @NotNull
    private static RefGenomeResource buildRefGenomeResource(@NotNull ServeConfig config) throws IOException {
        LOGGER.info("Reading driver genes from {}", config.driverGene37Tsv());
        List<DriverGene> driverGenes = DriverGeneFile.read(config.driverGene37Tsv());
        LOGGER.info(" Read {} driver genes", driverGenes.size());

        LOGGER.info("Reading known fusions from {}", config.knownFusion37File());
        KnownFusionCache fusionCache = KnownFusionCacheLoader.load(config.knownFusion37File());
        LOGGER.info(" Read {} known fusions", fusionCache.knownFusions().size());

        LOGGER.info(" Reading ensembl data cache from {}", config.ensemblDataDir37());
        EnsemblDataCache ensemblDataCache = EnsemblDataLoader.load(config.ensemblDataDir37(), RefGenome.V37);
        LOGGER.info("  Loaded ensembl data cache from {}", ensemblDataCache);

        return ImmutableRefGenomeResource.builder()
                .refSequence(new IndexedFastaSequenceFile(new File(config.refGenome37FastaFile())))
                .driverGenes(driverGenes)
                .knownFusionCache(fusionCache)
                .ensemblDataCache(ensemblDataCache)
                .proteinResolver(ProteinResolverFactory.dummy())
                .build();
    }
}

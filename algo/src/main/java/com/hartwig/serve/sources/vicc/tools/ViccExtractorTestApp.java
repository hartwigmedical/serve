package com.hartwig.serve.sources.vicc.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.ServeConfig;
import com.hartwig.serve.ServeLocalConfigProvider;
import com.hartwig.serve.common.drivercatalog.panel.DriverGene;
import com.hartwig.serve.common.drivercatalog.panel.DriverGeneFile;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.fusion.KnownFusionCache;
import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.curation.DoidLookupFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ExtractionResultWriter;
import com.hartwig.serve.extraction.hotspot.ProteinResolverFactory;
import com.hartwig.serve.refgenome.EnsemblDataCacheLoader;
import com.hartwig.serve.refgenome.ImmutableRefGenomeResource;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.vicc.ViccExtractor;
import com.hartwig.serve.sources.vicc.ViccExtractorFactory;
import com.hartwig.serve.sources.vicc.ViccReader;
import com.hartwig.serve.sources.vicc.ViccUtil;
import com.hartwig.serve.vicc.annotation.ViccClassificationConfig;
import com.hartwig.serve.vicc.datamodel.ViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class ViccExtractorTestApp {

    private static final Logger LOGGER = LogManager.getLogger(ViccExtractorTestApp.class);

    private static final Set<ViccSource> VICC_SOURCES_TO_INCLUDE =
            Sets.newHashSet(ViccSource.CIVIC, ViccSource.CGI, ViccSource.ONCOKB, ViccSource.JAX);
    private static final Integer MAX_VICC_ENTRIES = null;

    public static void main(String[] args) throws IOException {
        Configurator.setRootLevel(Level.DEBUG);

        ServeConfig config = ServeLocalConfigProvider.create();

        Path outputPath = new File(config.outputDir()).toPath();
        if (!Files.exists(outputPath)) {
            LOGGER.info("Creating {} directory for writing SERVE output", outputPath.toString());
            Files.createDirectory(outputPath);
        }

        RefGenomeResource refGenomeResource = buildRefGenomeResource(config);
        DoidLookup doidLookup = DoidLookupFactory.buildFromMappingTsv(config.missingDoidsMappingTsv());
        ViccExtractor viccExtractor =
                ViccExtractorFactory.buildViccExtractor(ViccClassificationConfig.build(), refGenomeResource, doidLookup);

        List<ViccEntry> entries = ViccReader.readAndCurateRelevantEntries(config.viccJson(), VICC_SOURCES_TO_INCLUDE, MAX_VICC_ENTRIES);
        ExtractionResult result = viccExtractor.extract(entries);

        String featureTsv = config.outputDir() + File.separator + "ViccEventClassification.tsv";
        ViccUtil.writeFeaturesToTsv(featureTsv, entries);

        new ExtractionResultWriter(config.outputDir(), Knowledgebase.VICC_CIVIC.refGenomeVersion(), refGenomeResource.refSequence()).write(
                result);
    }

    @NotNull
    private static RefGenomeResource buildRefGenomeResource(@NotNull ServeConfig config) throws IOException {
        LOGGER.info("Reading driver genes from {}", config.driverGene37Tsv());
        List<DriverGene> driverGenes = DriverGeneFile.read(config.driverGene37Tsv());
        LOGGER.info(" Read {} driver genes", driverGenes.size());

        LOGGER.info("Reading known fusions from {}", config.knownFusion37File());
        KnownFusionCache fusionCache = new KnownFusionCache();
        if (!fusionCache.loadFile(config.knownFusion37File())) {
            throw new IOException("Could not load known fusion cache from " + config.knownFusion37File());
        }
        LOGGER.info(" Read {} known fusions", fusionCache.getData().size());

        LOGGER.info(" Reading ensembl data cache from {}", config.ensemblDataDir37());
        EnsemblDataCache ensemblDataCache = EnsemblDataCacheLoader.load(config.ensemblDataDir37(), RefGenomeVersion.V37);
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

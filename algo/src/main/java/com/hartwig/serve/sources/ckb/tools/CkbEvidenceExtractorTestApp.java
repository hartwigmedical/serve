package com.hartwig.serve.sources.ckb.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.hartwig.serve.ServeConfig;
import com.hartwig.serve.ServeLocalConfigProvider;
import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.common.drivergene.DriverGeneFile;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.ensemblcache.EnsemblDataLoader;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.common.knownfusion.KnownFusionCacheLoader;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ExtractionResultWriter;
import com.hartwig.serve.extraction.hotspot.ProteinResolverFactory;
import com.hartwig.serve.refgenome.ImmutableRefGenomeResource;
import com.hartwig.serve.refgenome.RefGenomeManager;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.CkbExtractor;
import com.hartwig.serve.sources.ckb.CkbExtractorFactory;
import com.hartwig.serve.sources.ckb.CkbReader;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterFile;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterModel;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterFile;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterModel;
import com.hartwig.serve.sources.ckb.region.CkbRegion;
import com.hartwig.serve.sources.ckb.region.CkbRegionFile;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurationFile;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class CkbEvidenceExtractorTestApp {

    private static final Logger LOGGER = LogManager.getLogger(CkbEvidenceExtractorTestApp.class);

    private static final String VERSION = CkbEvidenceExtractorTestApp.class.getPackage().getImplementationVersion();

    public static void main(String[] args) throws IOException {
        Configurator.setRootLevel(Level.DEBUG);

        ServeConfig config = ServeLocalConfigProvider.create();

        Path outputPath = new File(config.outputDir()).toPath();
        if (!Files.exists(outputPath)) {
            LOGGER.info("Creating {} directory for writing SERVE output", outputPath.toString());
            Files.createDirectory(outputPath);
        }

        RefGenomeManager refGenomeManager = buildRefGenomeManager(config);
        TreatmentApproachCurator curator = new TreatmentApproachCurator(TreatmentApproachCurationFile.read(config.ckbDrugCurationTsv()));
        CkbEvidenceFilterModel evidenceFilter = new CkbEvidenceFilterModel(CkbEvidenceFilterFile.read(config.ckbEvidenceFilterTsv()));
        CkbTrialFilterModel trialFilter = new CkbTrialFilterModel(CkbTrialFilterFile.read(config.ckbTrialFilterTsv()));
        Set<CkbRegion> regionsToInclude = CkbRegionFile.read(config.ckbRegionsToIncludeTsv());

        CkbExtractor extractor = CkbExtractorFactory.createExtractor(CkbClassificationConfig.build(),
                refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.CKB),
                curator,
                evidenceFilter,
                trialFilter,
                regionsToInclude);

        List<CkbEntry> entries = CkbReader.readAndCurate(config.ckbDir(),
                config.ckbMolecularProfileFilterTsv(),
                config.ckbFacilityCurationNameTsv(),
                config.ckbFacilityCurationZipTsv(),
                config.ckbFacilityCurationManualTsv());

        ExtractionResult result = extractor.extract(entries);
        Map<RefGenome, ExtractionResult> results = Maps.newHashMap();
        results.put(RefGenome.V38, result);

        String eventsTsv = config.outputDir() + File.separator + "CkbEventClassification.tsv";
        CkbUtil.writeEventsToTsv(eventsTsv, entries);
        CkbUtil.printExtractionResults(result);

        new ExtractionResultWriter(VERSION, refGenomeManager, config.outputDir()).write(results);
    }

    @NotNull
    private static RefGenomeManager buildRefGenomeManager(@NotNull ServeConfig config) throws IOException {
        LOGGER.info("Reading driver genes from {}", config.driverGene38Tsv());
        List<DriverGene> driverGenes = DriverGeneFile.read(config.driverGene38Tsv());
        LOGGER.info(" Read {} driver genes", driverGenes.size());

        LOGGER.info("Reading known fusions from {}", config.knownFusion38File());
        KnownFusionCache fusionCache = KnownFusionCacheLoader.load(config.knownFusion38File());
        LOGGER.info(" Read {} known fusions", fusionCache.knownFusions().size());

        LOGGER.info(" Reading ensembl data cache from {}", config.ensemblDataDir38());
        EnsemblDataCache ensemblDataCache = EnsemblDataLoader.load(config.ensemblDataDir38(), RefGenome.V38);
        LOGGER.info("  Loaded ensembl data cache from {}", ensemblDataCache);

        Map<RefGenome, RefGenomeResource> resourcesPerRefGenome = Maps.newHashMap();
        resourcesPerRefGenome.put(RefGenome.V38,
                ImmutableRefGenomeResource.builder()
                        .refSequence(new IndexedFastaSequenceFile(new File(config.refGenome38FastaFile())))
                        .driverGenes(driverGenes)
                        .knownFusionCache(fusionCache)
                        .ensemblDataCache(ensemblDataCache)
                        .proteinResolver(ProteinResolverFactory.dummy())
                        .build());

        return new RefGenomeManager(resourcesPerRefGenome);
    }
}
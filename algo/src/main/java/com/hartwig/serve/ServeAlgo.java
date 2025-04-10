package com.hartwig.serve;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.refgenome.RefGenomeManager;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.CkbExtractor;
import com.hartwig.serve.sources.ckb.CkbExtractorFactory;
import com.hartwig.serve.sources.ckb.CkbReader;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterEntry;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterFile;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterModel;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterEntry;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterFile;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterModel;
import com.hartwig.serve.sources.ckb.region.CkbRegion;
import com.hartwig.serve.sources.ckb.region.CkbRegionFile;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurationEntry;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurationEntryKey;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurationFile;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;
import com.hartwig.serve.sources.docm.DocmEntry;
import com.hartwig.serve.sources.docm.DocmExtractor;
import com.hartwig.serve.sources.docm.DocmReader;
import com.hartwig.serve.sources.hartwig.gene.HartwigGeneEntry;
import com.hartwig.serve.sources.hartwig.gene.HartwigGeneExtractor;
import com.hartwig.serve.sources.hartwig.gene.HartwigGeneFileReader;
import com.hartwig.serve.sources.hartwig.hotspot.HartwigHotspotEntry;
import com.hartwig.serve.sources.hartwig.hotspot.HartwigHotspotExtractor;
import com.hartwig.serve.sources.hartwig.hotspot.HartwigHotspotFileReader;
import com.hartwig.serve.sources.vicc.ViccExtractor;
import com.hartwig.serve.sources.vicc.ViccExtractorFactory;
import com.hartwig.serve.sources.vicc.ViccReader;
import com.hartwig.serve.sources.vicc.doid.DoidLookup;
import com.hartwig.serve.sources.vicc.doid.DoidLookupFactory;
import com.hartwig.serve.vicc.annotation.ViccClassificationConfig;
import com.hartwig.serve.vicc.datamodel.ViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ServeAlgo {

    private static final Logger LOGGER = LogManager.getLogger(ServeAlgo.class);

    @NotNull
    private final RefGenomeManager refGenomeManager;

    public ServeAlgo(@NotNull final RefGenomeManager refGenomeManager) {
        this.refGenomeManager = refGenomeManager;
    }

    @NotNull
    public Map<RefGenome, ExtractionResult> run(@NotNull ServeConfig config) throws IOException {
        List<ExtractionResult> extractions = Stream.of(config.useVicc()
                                ? extractViccKnowledge(config.viccJson(), config.viccSources(), config.viccMissingDoidsMappingTsv())
                                : null,
                        config.useCkb() ? extractCkbKnowledge(config.ckbDir(),
                                config.ckbMolecularProfileFilterTsv(),
                                config.ckbEvidenceFilterTsv(),
                                config.ckbTrialFilterTsv(),
                                config.ckbDrugCurationTsv(),
                                config.ckbRegionsToIncludeTsv(),
                                config.ckbFacilityCurationNameTsv(),
                                config.ckbFacilityCurationZipTsv(),
                                config.ckbFacilityCurationManualTsv()) : null,
                        config.useDocm() ? extractDocmKnowledge(config.docmTsv()) : null,
                        config.useHartwigCohortHotspots() ? extractHartwigCohortHotspotKnowledge(config.hartwigCohortHotspotTsv(),
                                !config.skipVariantResolving()) : null,
                        config.useHartwigCuratedHotspots() ? extractHartwigCuratedHotspotKnowledge(config.hartwigCuratedHotspotTsv(),
                                !config.skipVariantResolving()) : null,
                        config.useHartwigDriverGenes() ? extractHartwigDriverGeneKnowledge(config.driverGene37Tsv()) : null,
                        config.useHartwigCuratedGenes() ? extractHartwigCuratedGeneKnowledge(config.hartwigCuratedGeneTsv()) : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<RefGenome, ExtractionResult> versionedMap = refGenomeManager.makeVersioned(extractions);

        refGenomeManager.evaluate();

        return versionedMap;
    }

    @NotNull
    private ExtractionResult extractViccKnowledge(@NotNull String viccJson, @NotNull Set<ViccSource> viccSources,
            @NotNull String viccMissingDoidsMappingTsv) throws IOException {
        LOGGER.info("Creating missing doid lookup mapping from {}", viccMissingDoidsMappingTsv);
        DoidLookup missingDoidLookup = DoidLookupFactory.buildFromMappingTsv(viccMissingDoidsMappingTsv);

        List<ViccEntry> entries = ViccReader.readAndCurateRelevantEntries(viccJson, viccSources, null);

        EventClassifierConfig config = ViccClassificationConfig.build();
        // Assume all VICC sources share the same ref genome version
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.VICC_CIVIC);
        ViccExtractor extractor = ViccExtractorFactory.create(config, refGenomeResource, missingDoidLookup);

        LOGGER.info("Running VICC knowledge extraction");
        ExtractionResult result = extractor.extract(entries);

        missingDoidLookup.evaluate();
        return result;
    }

    @NotNull
    private ExtractionResult extractCkbKnowledge(@NotNull String ckbDir, @NotNull String molecularProfileFilterTsv,
            @NotNull String ckbEvidenceFilterTsv, @NotNull String ckbTrialFilterTsv, @NotNull String ckbDrugCurationTsv,
            @NotNull String ckbRegionTsv, @NotNull String facilityCurationNameTsv, @NotNull String facilityCurationZipTsv,
            @NotNull String facilityCurationManualTsv) throws IOException {
        List<CkbEntry> ckbEntries = CkbReader.readAndCurate(ckbDir,
                molecularProfileFilterTsv,
                facilityCurationNameTsv,
                facilityCurationZipTsv,
                facilityCurationManualTsv);

        Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> treatmentApproachMap =
                TreatmentApproachCurationFile.read(ckbDrugCurationTsv);
        TreatmentApproachCurator treatmentApproachCurator = new TreatmentApproachCurator(treatmentApproachMap);

        LOGGER.info("Reading evidence filter entries from {}", ckbEvidenceFilterTsv);
        List<CkbEvidenceFilterEntry> ckbEvidenceFilterEntries = CkbEvidenceFilterFile.read(ckbEvidenceFilterTsv);
        LOGGER.info(" Read {} evidence filter entries", ckbEvidenceFilterEntries.size());
        CkbEvidenceFilterModel evidenceFilter = new CkbEvidenceFilterModel(ckbEvidenceFilterEntries);

        LOGGER.info("Reading trial filter entries from {}", ckbTrialFilterTsv);
        List<CkbTrialFilterEntry> ckbTrialFilterEntries = CkbTrialFilterFile.read(ckbTrialFilterTsv);
        LOGGER.info(" Read {} trial filter entries", ckbTrialFilterEntries.size());
        CkbTrialFilterModel trialFilter = new CkbTrialFilterModel(ckbTrialFilterEntries);

        LOGGER.info("Reading regions to include from {}", ckbRegionTsv);
        Set<CkbRegion> regionsToInclude = CkbRegionFile.read(ckbRegionTsv);
        LOGGER.info(" Read {} regions to include", regionsToInclude.size());

        EventClassifierConfig config = CkbClassificationConfig.build();
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.CKB);

        CkbExtractor extractor = CkbExtractorFactory.createExtractor(config,
                refGenomeResource,
                treatmentApproachCurator,
                evidenceFilter,
                trialFilter,
                regionsToInclude);

        LOGGER.info("Running CKB knowledge extraction");
        ExtractionResult result = extractor.extract(ckbEntries);

        evidenceFilter.reportUnusedFilterEntries();
        trialFilter.reportUnusedFilterEntries();
        treatmentApproachCurator.reportUnusedCuratedEntries();

        return result;
    }

    @NotNull
    private ExtractionResult extractDocmKnowledge(@NotNull String docmTsv) throws IOException {
        List<DocmEntry> entries = DocmReader.readAndCurate(docmTsv);

        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.DOCM);
        DocmExtractor extractor = new DocmExtractor(refGenomeResource.proteinResolver());
        LOGGER.info("Running DoCM knowledge extraction");
        return extractor.extract(entries);
    }

    @NotNull
    private ExtractionResult extractHartwigCohortHotspotKnowledge(@NotNull String hartwigCohortHotspotTsv, boolean addExplicitHotspots)
            throws IOException {
        LOGGER.info("Reading Hartwig Cohort Hotspot TSV from '{}'", hartwigCohortHotspotTsv);
        List<HartwigHotspotEntry> entries = HartwigHotspotFileReader.read(hartwigCohortHotspotTsv);
        LOGGER.info(" Read {} entries", entries.size());

        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.HARTWIG_HOTSPOT_COHORT);
        HartwigHotspotExtractor extractor =
                new HartwigHotspotExtractor(Knowledgebase.HARTWIG_HOTSPOT_COHORT, refGenomeResource.proteinResolver(), addExplicitHotspots);
        LOGGER.info("Running Hartwig cohort hotspot knowledge extraction");
        return extractor.extract(entries);
    }

    @NotNull
    private ExtractionResult extractHartwigCuratedHotspotKnowledge(@NotNull String hartwigCuratedHotspotTsv, boolean addExplicitHotspots)
            throws IOException {
        LOGGER.info("Reading Hartwig Curated Hotspot TSV from '{}'", hartwigCuratedHotspotTsv);
        List<HartwigHotspotEntry> entries = HartwigHotspotFileReader.read(hartwigCuratedHotspotTsv);
        LOGGER.info(" Read {} entries", entries.size());

        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.HARTWIG_HOTSPOT_CURATED);
        HartwigHotspotExtractor extractor = new HartwigHotspotExtractor(Knowledgebase.HARTWIG_HOTSPOT_CURATED,
                refGenomeResource.proteinResolver(),
                addExplicitHotspots);
        LOGGER.info("Running Hartwig curated hotspot knowledge extraction");
        return extractor.extract(entries);
    }

    @NotNull
    private ExtractionResult extractHartwigDriverGeneKnowledge(@NotNull String driverGeneFileTsv) throws IOException {
        LOGGER.info("Reading Hartwig Driver Genes TSV from '{}'", driverGeneFileTsv);
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.readDriverGenes(driverGeneFileTsv);
        LOGGER.info(" Read {} entries", entries.size());

        HartwigGeneExtractor extractor = new HartwigGeneExtractor(Knowledgebase.HARTWIG_GENE_DRIVER_PANEL);
        LOGGER.info("Running Hartwig driver gene knowledge extraction");
        return extractor.extract(entries);
    }

    @NotNull
    private ExtractionResult extractHartwigCuratedGeneKnowledge(@NotNull String curatedGeneFileTsv) throws IOException {
        LOGGER.info("Reading Hartwig Curated Genes TSV from '{}'", curatedGeneFileTsv);
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.readCuratedGenes(curatedGeneFileTsv);
        LOGGER.info(" Read {} entries", entries.size());

        HartwigGeneExtractor extractor = new HartwigGeneExtractor(Knowledgebase.HARTWIG_GENE_CURATED);
        LOGGER.info("Running Hartwig curated gene knowledge extraction");
        return extractor.extract(entries);
    }
}
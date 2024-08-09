package com.hartwig.serve;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.iclusion.classification.IclusionClassificationConfig;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.refgenome.RefGenomeManager;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.CkbExtractor;
import com.hartwig.serve.sources.ckb.CkbExtractorFactory;
import com.hartwig.serve.sources.ckb.CkbReader;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistEvidenceEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistEvidenceFile;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudyEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudyFile;
import com.hartwig.serve.sources.ckb.blacklist.CkbEvidenceBlacklistModel;
import com.hartwig.serve.sources.ckb.blacklist.CkbStudyBlacklistModel;
import com.hartwig.serve.sources.ckb.facility_curation.CkbFacilityCurationFilterEntry;
import com.hartwig.serve.sources.ckb.facility_curation.CkbFacilityCurationModel;
import com.hartwig.serve.sources.ckb.facility_curation.CkbFacilityCurationNameFile;
import com.hartwig.serve.sources.ckb.facility_curation.CkbFacilityCurationFilterFile;
import com.hartwig.serve.sources.ckb.facility_curation.CkbFacilityCurationNameEntry;
import com.hartwig.serve.sources.ckb.facility_curation.CkbFacilityCurationZipEntry;
import com.hartwig.serve.sources.ckb.facility_curation.CkbFacilityCurationZipFile;
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
import com.hartwig.serve.sources.iclusion.IclusionExtractor;
import com.hartwig.serve.sources.iclusion.IclusionExtractorFactory;
import com.hartwig.serve.sources.iclusion.IclusionReader;
import com.hartwig.serve.sources.vicc.ViccExtractor;
import com.hartwig.serve.sources.vicc.ViccExtractorFactory;
import com.hartwig.serve.sources.vicc.ViccReader;
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
    @NotNull
    private final DoidLookup missingDoidLookup;

    public ServeAlgo(@NotNull final RefGenomeManager refGenomeManager, @NotNull final DoidLookup missingDoidLookup) {
        this.refGenomeManager = refGenomeManager;
        this.missingDoidLookup = missingDoidLookup;
    }

    @NotNull
    public Map<RefGenome, ExtractionResult> run(@NotNull ServeConfig config) throws IOException {
        List<CkbEntry> ckbEntries = (config.useCkbEvidence() || config.useCkbTrials()) ? CkbReader.readAndCurate(config.ckbDir(),
                config.ckbBlacklistMolecularProfileTsv()) : Collections.emptyList();

        List<ExtractionResult> extractions =
                Stream.of(config.useVicc() ? extractViccKnowledge(config.viccJson(), config.viccSources()) : null,
                                config.useIclusion() ? extractIclusionKnowledge(config.iClusionTrialTsv(), config.iClusionFilterTsv()) : null,
                                config.useCkbEvidence() ? extractCkbEvidenceKnowledge(config.ckbDrugCurationTsv(),
                                        config.ckbBlacklistEvidenceTsv(),
                                        ckbEntries) : null,
                                config.useCkbTrials() ? extractCkbTrialKnowledge(config.ckbBlacklistTrialTsv(),
                                        config.ckbRegionTsv(),
                                        config.ckbFacilityCurationNameTsv(),
                                        config.ckbFacilityCurationZipTsv(),
                                        config.ckbFacilityCurationFilterTsv(),
                                        ckbEntries) : null,
                                config.useDocm() ? extractDocmKnowledge(config.docmTsv()) : null,
                                config.useHartwigCohortHotspots() ? extractHartwigCohortHotspotKnowledge(config.hartwigCohortHotspotTsv(),
                                        !config.skipHotspotResolving()) : null,
                                config.useHartwigCuratedHotspots() ? extractHartwigCuratedHotspotKnowledge(config.hartwigCuratedHotspotTsv(),
                                        !config.skipHotspotResolving()) : null,
                                config.useHartwigDriverGenes() ? extractHartwigDriverGeneKnowledge(config.driverGene37Tsv()) : null,
                                config.useHartwigCuratedGenes() ? extractHartwigCuratedGeneKnowledge(config.hartwigCuratedGeneTsv()) : null)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        Map<RefGenome, ExtractionResult> versionedMap = refGenomeManager.makeVersioned(extractions);

        missingDoidLookup.evaluate();
        refGenomeManager.evaluate();

        return versionedMap;
    }

    @NotNull
    private ExtractionResult extractViccKnowledge(@NotNull String viccJson, @NotNull Set<ViccSource> viccSources) throws IOException {
        List<ViccEntry> entries = ViccReader.readAndCurateRelevantEntries(viccJson, viccSources, null);

        EventClassifierConfig config = ViccClassificationConfig.build();
        // Assume all VICC sources share the same ref genome version
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.VICC_CIVIC);
        ViccExtractor extractor = ViccExtractorFactory.create(config, refGenomeResource, missingDoidLookup);

        LOGGER.info("Running VICC knowledge extraction");
        return extractor.extract(entries);
    }

    @NotNull
    private ExtractionResult extractIclusionKnowledge(@NotNull String iClusionTrialTsv, @NotNull String iClusionFilterTsv)
            throws IOException {
        List<IclusionTrial> trials = IclusionReader.readAndCurate(iClusionTrialTsv, iClusionFilterTsv);

        EventClassifierConfig config = IclusionClassificationConfig.build();
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.ICLUSION);
        IclusionExtractor extractor = IclusionExtractorFactory.create(config, refGenomeResource, missingDoidLookup);

        LOGGER.info("Running iClusion knowledge extraction");
        return extractor.extract(trials);
    }

    @NotNull
    private ExtractionResult extractCkbEvidenceKnowledge(@NotNull String ckbDrugCurationTsv, @NotNull String ckbBlacklistEvidenceTsv,
            @NotNull List<CkbEntry> ckbEntries) throws IOException {
        EventClassifierConfig config = CkbClassificationConfig.build();
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.CKB_EVIDENCE);

        Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> treatmentApproachMap =
                TreatmentApproachCurationFile.read(ckbDrugCurationTsv);

        TreatmentApproachCurator curator = new TreatmentApproachCurator(treatmentApproachMap);

        List<CkbBlacklistEvidenceEntry> ckbBlacklistEvidenceEntries = CkbBlacklistEvidenceFile.read(ckbBlacklistEvidenceTsv);
        LOGGER.info(" Read {} blacklisting evidence entries", ckbBlacklistEvidenceEntries.size());
        CkbEvidenceBlacklistModel blacklistEvidence = new CkbEvidenceBlacklistModel(ckbBlacklistEvidenceEntries);

        CkbExtractor extractor = CkbExtractorFactory.createEvidenceExtractor(config, refGenomeResource, curator, blacklistEvidence);

        LOGGER.info("Running CKB evidence knowledge extraction");
        ExtractionResult result = extractor.extract(ckbEntries);

        blacklistEvidence.reportUnusedBlacklistEntries();
        curator.reportUnusedCuratedEntries();

        return result;
    }

    @NotNull
    private ExtractionResult extractCkbTrialKnowledge(@NotNull String ckbBlacklistStudyTsv, @NotNull String ckbRegionTsv,
            @NotNull String ckbFacilityCurationNameTsv, @NotNull String ckbFacilityCurationZipTsv,
            @NotNull String ckbFacilityCurationFilterTsv, @NotNull List<CkbEntry> ckbEntries) throws IOException {
        EventClassifierConfig config = CkbClassificationConfig.build();
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.CKB_TRIAL);

        List<CkbBlacklistStudyEntry> ckbBlacklistStudyEntriesEntries = CkbBlacklistStudyFile.read(ckbBlacklistStudyTsv);
        LOGGER.info(" Read {} blacklisting studies entries", ckbBlacklistStudyEntriesEntries.size());

        CkbStudyBlacklistModel blacklistStudy = new CkbStudyBlacklistModel(ckbBlacklistStudyEntriesEntries);

        LOGGER.info("Reading regions to include from {}", ckbRegionTsv);
        Set<CkbRegion> regionsToInclude = CkbRegionFile.read(ckbRegionTsv);
        LOGGER.info(" Read {} regions to include", regionsToInclude.size());

        LOGGER.info("Reading facility name curations from {}", ckbFacilityCurationNameTsv);
        List<CkbFacilityCurationNameEntry> facilityNameCurations = CkbFacilityCurationNameFile.read(ckbFacilityCurationNameTsv);
        LOGGER.info(" Read {} facility name curations to include", facilityNameCurations.size());

        LOGGER.info("Reading facility zip curations from {}", ckbFacilityCurationZipTsv);
        List<CkbFacilityCurationZipEntry> facilityZipCurations = CkbFacilityCurationZipFile.read(ckbFacilityCurationZipTsv);
        LOGGER.info(" Read {} facility zip curations to include", facilityZipCurations.size());

        LOGGER.info("Reading facility filter curations from {}", ckbFacilityCurationFilterTsv);
        List<CkbFacilityCurationFilterEntry> facilityFilterCurations = CkbFacilityCurationFilterFile.read(ckbFacilityCurationFilterTsv);
        LOGGER.info(" Read {} facility filter curations to include", facilityFilterCurations.size());

        CkbFacilityCurationModel ckbFacilityCurationModel =
                new CkbFacilityCurationModel(facilityNameCurations, facilityZipCurations, facilityFilterCurations);

        CkbExtractor extractor = CkbExtractorFactory.createTrialExtractor(config,
                refGenomeResource,
                blacklistStudy,
                regionsToInclude,
                ckbFacilityCurationModel);

        LOGGER.info("Running CKB trial knowledge extraction");
        ExtractionResult result = extractor.extract(ckbEntries);

        blacklistStudy.reportUnusedBlacklistEntries();

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
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.read(driverGeneFileTsv);
        LOGGER.info(" Read {} entries", entries.size());

        HartwigGeneExtractor extractor = new HartwigGeneExtractor(Knowledgebase.HARTWIG_GENE_DRIVER_PANEL);
        LOGGER.info("Running Hartwig driver gene knowledge extraction");
        return extractor.extract(entries);
    }

    @NotNull
    private ExtractionResult extractHartwigCuratedGeneKnowledge(@NotNull String curatedGeneFileTsv) throws IOException {
        LOGGER.info("Reading Hartwig Curated Genes TSV from '{}'", curatedGeneFileTsv);
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.read(curatedGeneFileTsv);
        LOGGER.info(" Read {} entries", entries.size());

        HartwigGeneExtractor extractor = new HartwigGeneExtractor(Knowledgebase.HARTWIG_GENE_CURATED);
        LOGGER.info("Running Hartwig curated gene knowledge extraction");
        return extractor.extract(entries);
    }
}
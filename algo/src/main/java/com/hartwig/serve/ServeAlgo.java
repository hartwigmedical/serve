package com.hartwig.serve;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.iclusion.classification.IclusionClassificationConfig;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.refgenome.RefGenomeManager;
import com.hartwig.serve.refgenome.RefGenomeResource;
import com.hartwig.serve.sources.ckb.CkbExtractor;
import com.hartwig.serve.sources.ckb.CkbExtractorFactory;
import com.hartwig.serve.sources.ckbtrial.*;
import com.hartwig.serve.sources.ckb.CkbReader;
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
        List<ExtractionResult> extractions = Lists.newArrayList();
        if (config.useVicc()) {
            extractions.add(extractViccKnowledge(config.viccJson(), config.viccSources()));
        }

        if (config.useIclusion()) {
            extractions.add(extractIclusionKnowledge(config.iClusionTrialTsv(), config.iClusionFilterTsv()));
        }

        if (config.useCkb()) {
            extractions.add(extractCkbKnowledge(config.ckbDir(), config.ckbFilterTsv(), config.ckbDrugCurationTsv()));
        }

        if (config.useCkbTrials()) {
            extractions.add(extractCkbTrialKnowledge(config.ckbDir(), config.ckbFilterTsv(), config.ckbDrugCurationTsv()));
        }

        if (config.useDocm()) {
            extractions.add(extractDocmKnowledge(config.docmTsv()));
        }

        if (config.useHartwigCohortHotspots()) {
            extractions.add(extractHartwigCohortHotspotKnowledge(config.hartwigCohortHotspotTsv(), !config.skipHotspotResolving()));
        }

        if (config.useHartwigCuratedHotspots()) {
            extractions.add(extractHartwigCuratedHotspotKnowledge(config.hartwigCuratedHotspotTsv(), !config.skipHotspotResolving()));
        }

        if (config.useHartwigDriverGenes()) {
            extractions.add(extractHartwigDriverGeneKnowledge(config.driverGene37Tsv()));
        }

        if (config.useHartwigCuratedGenes()) {
            extractions.add(extractHartwigCuratedGeneKnowledge(config.hartwigCuratedGeneTsv()));
        }

        Map<RefGenome, List<ExtractionResult>> versionedMap = refGenomeManager.makeVersioned(extractions);

        Map<RefGenome, ExtractionResult> refDependentExtractionMap = Maps.newHashMap();
        for (Map.Entry<RefGenome, List<ExtractionResult>> entry : versionedMap.entrySet()) {
            refDependentExtractionMap.put(entry.getKey(), ExtractionFunctions.merge(entry.getValue()));
        }

        missingDoidLookup.evaluate();
        refGenomeManager.evaluate();

        return refDependentExtractionMap;
    }

    @NotNull
    private ExtractionResult extractViccKnowledge(@NotNull String viccJson, @NotNull Set<ViccSource> viccSources) throws IOException {
        List<ViccEntry> entries = ViccReader.readAndCurateRelevantEntries(viccJson, viccSources, null);

        EventClassifierConfig config = ViccClassificationConfig.build();
        // Assume all VICC sources share the same ref genome version
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.VICC_CIVIC);
        ViccExtractor extractor = ViccExtractorFactory.buildViccExtractor(config, refGenomeResource, missingDoidLookup);

        LOGGER.info("Running VICC knowledge extraction");
        return extractor.extract(entries);
    }

    @NotNull
    private ExtractionResult extractIclusionKnowledge(@NotNull String iClusionTrialTsv, @NotNull String iClusionFilterTsv)
            throws IOException {
        List<IclusionTrial> trials = IclusionReader.readAndCurate(iClusionTrialTsv, iClusionFilterTsv);

        EventClassifierConfig config = IclusionClassificationConfig.build();
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.ICLUSION);
        IclusionExtractor extractor = IclusionExtractorFactory.buildIclusionExtractor(config, refGenomeResource, missingDoidLookup);

        LOGGER.info("Running iClusion knowledge extraction");
        return extractor.extract(trials);
    }

    @NotNull
    private ExtractionResult extractCkbKnowledge(@NotNull String ckbDir, @NotNull String ckbFilterTsv, @NotNull String ckbDrugCurationTsv)
            throws IOException {
        List<CkbEntry> ckbEntries = CkbReader.readAndCurate(ckbDir, ckbFilterTsv);

        EventClassifierConfig config = CkbClassificationConfig.build();
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.CKB);

        Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> treatmentApproachMap =
                TreatmentApproachCurationFile.read(ckbDrugCurationTsv);

        TreatmentApproachCurator curator = new TreatmentApproachCurator(treatmentApproachMap);

        CkbExtractor extractor = CkbExtractorFactory.buildCkbExtractor(config, refGenomeResource, curator);

        LOGGER.info("Running CKB knowledge extraction");
        return extractor.extract(ckbEntries);
    }

    @NotNull
    private ExtractionResult extractCkbTrialKnowledge(@NotNull String ckbDir, @NotNull String ckbFilterTsv, @NotNull String ckbDrugCurationTsv)
            throws IOException {
        List<CkbEntry> ckbEntries = CkbReader.readAndCurate(ckbDir, ckbFilterTsv);

        EventClassifierConfig config = CkbClassificationConfig.build();
        RefGenomeResource refGenomeResource = refGenomeManager.pickResourceForKnowledgebase(Knowledgebase.CKB);

        CkbTrialExtractor extractor = CkbTrialExtractorFactory.buildCkbTrialsExtractor(config, refGenomeResource);

        LOGGER.info("Running CKB trial knowledge extraction");
        return extractor.extract(ckbEntries);
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
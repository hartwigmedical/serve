package com.hartwig.serve;

import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface ServeConfig {

    Logger LOGGER = LogManager.getLogger(ServeConfig.class);
    String NOT_APPLICABLE = "N/A";

    // Input sources to SERVE
    String USE_VICC = "use_vicc";
    String VICC_JSON = "vicc_json";
    String VICC_SOURCES = "vicc_sources";
    String USE_ICLUSION = "use_iclusion";
    String ICLUSION_TRIAL_TSV = "iclusion_trial_tsv";
    String ICLUSION_FILTER_TSV = "iclusion_filter_tsv";
    String USE_CKB_EVIDENCE = "use_ckb_evidence";
    String USE_CKB_TRIALS = "use_ckb_trials";
    String CKB_DIR = "ckb_dir";
    String CKB_FILTER_TSV = "ckb_filter_tsv";
    String CKB_BLACKLIST_STUDY_TSV = "ckb_blacklist_studies_tsv";
    String CKB_BLACKLIST_EVIDENCE_TSV = "ckb_blacklist_evidence_tsv";
    String CKB_DRUG_CURATION_TSV = "ckb_drug_curation_tsv";
    String USE_DOCM = "use_docm";
    String DOCM_TSV = "docm_tsv";
    String USE_HARTWIG_COHORT_HOTSPOTS = "use_hartwig_cohort_hotspots";
    String HARTWIG_COHORT_HOTSPOT_TSV = "hartwig_cohort_hotspot_tsv";
    String USE_HARTWIG_CURATED_HOTSPOTS = "use_hartwig_curated_hotspots";
    String HARTWIG_CURATED_HOTSPOT_TSV = "hartwig_curated_hotspot_tsv";
    String USE_HARTWIG_DRIVER_GENES = "use_hartwig_driver_genes";
    String USE_HARTWIG_CURATED_GENES = "use_hartwig_curated_genes";
    String HARTWIG_CURATED_GENE_TSV = "hartwig_curated_gene_tsv";
    // Config for curation of evidence
    String MISSING_DOIDS_MAPPING_TSV = "missing_doids_mapping_tsv";

    // Additional config for knowledge generation
    String ENSEMBL_DATA_DIR_37 = "ensembl_data_dir_37";
    String ENSEMBL_DATA_DIR_38 = "ensembl_data_dir_38";
    String REF_GENOME_37_FASTA_FILE = "ref_genome_37_fasta_file";
    String REF_GENOME_38_FASTA_FILE = "ref_genome_38_fasta_file";
    String REF_GENOME_37_TO_38_CHAIN = "ref_genome_37_to_38_chain";
    String REF_GENOME_38_TO_37_CHAIN = "ref_genome_38_to_37_chain";
    String DRIVER_GENE_37_TSV = "driver_gene_37_tsv";
    String DRIVER_GENE_38_TSV = "driver_gene_38_tsv";
    String KNOWN_FUSION_37_FILE = "known_fusion_37_file";
    String KNOWN_FUSION_38_FILE = "known_fusion_38_file";

    // All output from SERVE will be written to this dir
    String OUTPUT_DIR = "output_dir";

    // Options to help with debugging / testing
    String SKIP_HOTSPOT_RESOLVING = "skip_hotspot_resolving";
    String LOG_DEBUG = "log_debug";

    @NotNull
    static Options createOptions() {
        Options options = new Options();

        options.addOption(USE_VICC, false, "If provided, VICC will be used as a source in SERVE");
        options.addOption(VICC_JSON, true, "Path to the VICC JSON knowledgebase");
        options.addOption(VICC_SOURCES, true, "Comma-separated list of (lowercase) VICC sources to include");
        options.addOption(USE_ICLUSION, false, "If provided, iClusion will be used as a source in SERVE");
        options.addOption(ICLUSION_TRIAL_TSV, true, "Path to the iClusion input trial TSV");
        options.addOption(ICLUSION_FILTER_TSV, true, "Path to the iClusion filter tsv");
        options.addOption(USE_CKB_EVIDENCE, false, "If provided, evidence from CKB FLEX will be used as a source in SERVE");
        options.addOption(USE_CKB_TRIALS, false, "If provided, clinical trials from CKB FLEX will be used as a source in SERVE");
        options.addOption(CKB_DIR, true, "Path to the CKB FLEX json input dir");
        options.addOption(CKB_FILTER_TSV, true, "Path to the CKB filter tsv");
        options.addOption(CKB_BLACKLIST_STUDY_TSV, true, "Path to the CKB blacklist studies tsv");
        options.addOption(CKB_BLACKLIST_EVIDENCE_TSV, true, "Path to the CKB blacklist evidence tsv");
        options.addOption(CKB_DRUG_CURATION_TSV, true, "Path to the CKB drug curation tsv");
        options.addOption(USE_DOCM, false, "If provided, DoCM will be used as a source in SERVE");
        options.addOption(DOCM_TSV, true, "Path to the DoCM knowledgebase input TSV");
        options.addOption(USE_HARTWIG_COHORT_HOTSPOTS, false, "If provided, Hartwig Cohort Hotspots will be used as a source in SERVE");
        options.addOption(HARTWIG_COHORT_HOTSPOT_TSV, true, "Path to the Hartwig Cohort Hotspots input TSV");
        options.addOption(USE_HARTWIG_CURATED_HOTSPOTS, false, "If provided, Hartwig Curated Hotspots will be used as a source in SERVE");
        options.addOption(HARTWIG_CURATED_HOTSPOT_TSV, true, "Path to the Hartwig Curated Hotspots input TSV");
        options.addOption(USE_HARTWIG_DRIVER_GENES, false, "If provided, Hartwig Driver Genes will be used as a source in SERVE");
        options.addOption(USE_HARTWIG_CURATED_GENES, false, "If provided, Hartwig Curated Genes will be used as a source in SERVE");
        options.addOption(HARTWIG_CURATED_GENE_TSV, true, "Path to the Hartwig Curated Genes input TSV");

        options.addOption(MISSING_DOIDS_MAPPING_TSV, true, "Path to the mapping TSV containing entries for missing DOIDs");

        options.addOption(ENSEMBL_DATA_DIR_37, true, "Ensembl data file directory for ref genome V37");
        options.addOption(ENSEMBL_DATA_DIR_38, true, "Ensembl data file directory for ref genome V38");
        options.addOption(REF_GENOME_37_FASTA_FILE, true, "Path to the V37 ref genome fasta file");
        options.addOption(REF_GENOME_38_FASTA_FILE, true, "Path to the V38 ref genome fasta file");
        options.addOption(REF_GENOME_37_TO_38_CHAIN, true, "Chain file to lift over ref genome V37 to V38");
        options.addOption(REF_GENOME_38_TO_37_CHAIN, true, "Chain file to lift over ref genome V38 to V37");
        options.addOption(DRIVER_GENE_37_TSV, true, "Path to driver gene v37 TSV");
        options.addOption(DRIVER_GENE_38_TSV, true, "Path to driver gene v38 TSV");
        options.addOption(KNOWN_FUSION_37_FILE, true, "Path to the known fusion v37 file");
        options.addOption(KNOWN_FUSION_38_FILE, true, "Path to the known fusion v38 file");

        options.addOption(OUTPUT_DIR, true, "Dir which will hold all SERVE output files");

        options.addOption(SKIP_HOTSPOT_RESOLVING, false, "If present, skips hotspot resolving");
        options.addOption(LOG_DEBUG, false, "If present, switches the logging to DEBUG mode");

        return options;
    }

    boolean useVicc();

    @NotNull
    String viccJson();

    @NotNull
    Set<ViccSource> viccSources();

    boolean useIclusion();

    @NotNull
    String iClusionTrialTsv();

    @NotNull
    String iClusionFilterTsv();

    boolean useCkbEvidence();

    boolean useCkbTrials();

    @NotNull
    String ckbDir();

    @NotNull
    String ckbFilterTsv();
    @NotNull
    String ckbBlacklistStudyTsv();
    @NotNull
    String ckbBlacklistEvidenceTsv();
    @NotNull
    String ckbDrugCurationTsv();

    boolean useDocm();

    @NotNull
    String docmTsv();

    boolean useHartwigCohortHotspots();

    @NotNull
    String hartwigCohortHotspotTsv();

    boolean useHartwigCuratedHotspots();

    @NotNull
    String hartwigCuratedHotspotTsv();

    boolean useHartwigDriverGenes();

    boolean useHartwigCuratedGenes();

    String hartwigCuratedGeneTsv();

    @NotNull
    String missingDoidsMappingTsv();

    @NotNull
    String ensemblDataDir37();

    @NotNull
    String ensemblDataDir38();

    @NotNull
    String refGenome37FastaFile();

    @NotNull
    String refGenome38FastaFile();

    @NotNull
    String refGenome37To38Chain();

    @NotNull
    String refGenome38To37Chain();

    @NotNull
    String driverGene37Tsv();

    @NotNull
    String driverGene38Tsv();

    @NotNull
    String knownFusion37File();

    @NotNull
    String knownFusion38File();

    @NotNull
    String outputDir();

    boolean skipHotspotResolving();

    @NotNull
    static ServeConfig createConfig(@NotNull CommandLine cmd) throws ParseException {
        if (cmd.hasOption(LOG_DEBUG)) {
            Configurator.setRootLevel(Level.DEBUG);
            LOGGER.debug("Switched root level logging to DEBUG");
        }

        boolean useVicc = cmd.hasOption(USE_VICC);
        boolean useIclusion = cmd.hasOption(USE_ICLUSION);
        boolean useCkbEvidence = cmd.hasOption(USE_CKB_EVIDENCE);
        boolean useCkbTrials = cmd.hasOption(USE_CKB_TRIALS);
        boolean useDocm = cmd.hasOption(USE_DOCM);
        boolean useHartwigCohortHotspots = cmd.hasOption(USE_HARTWIG_COHORT_HOTSPOTS);
        boolean useHartwigCuratedHotspots = cmd.hasOption(USE_HARTWIG_CURATED_HOTSPOTS);
        boolean useHartwigDriverGenes = cmd.hasOption(USE_HARTWIG_DRIVER_GENES);
        boolean useHartwigCuratedGenes = cmd.hasOption(USE_HARTWIG_CURATED_GENES);

        return ImmutableServeConfig.builder()
                .useVicc(useVicc)
                .viccJson(useVicc ? nonOptionalFile(cmd, VICC_JSON) : NOT_APPLICABLE)
                .viccSources(useVicc ? readViccSources(cmd) : Sets.newHashSet())
                .useIclusion(useIclusion)
                .iClusionTrialTsv(useIclusion ? nonOptionalFile(cmd, ICLUSION_TRIAL_TSV) : NOT_APPLICABLE)
                .iClusionFilterTsv(useIclusion ? nonOptionalFile(cmd, ICLUSION_FILTER_TSV) : NOT_APPLICABLE)
                .useCkbEvidence(useCkbEvidence)
                .useCkbTrials(useCkbTrials)
                .ckbDir(useCkbEvidence || useCkbTrials ? nonOptionalDir(cmd, CKB_DIR) : NOT_APPLICABLE)
                .ckbFilterTsv(useCkbEvidence || useCkbTrials ? nonOptionalFile(cmd, CKB_FILTER_TSV) : NOT_APPLICABLE)
                .ckbBlacklistStudyTsv(useCkbTrials ? nonOptionalFile(cmd, CKB_BLACKLIST_STUDY_TSV) : NOT_APPLICABLE)
                .ckbBlacklistEvidenceTsv(useCkbTrials ? nonOptionalFile(cmd, CKB_BLACKLIST_EVIDENCE_TSV) : NOT_APPLICABLE)
                .ckbDrugCurationTsv(useCkbEvidence ? nonOptionalFile(cmd, CKB_DRUG_CURATION_TSV) : NOT_APPLICABLE)
                .useDocm(useDocm)
                .docmTsv(useDocm ? nonOptionalFile(cmd, DOCM_TSV) : NOT_APPLICABLE)
                .useHartwigCohortHotspots(useHartwigCohortHotspots)
                .hartwigCohortHotspotTsv(useHartwigCohortHotspots ? nonOptionalFile(cmd, HARTWIG_COHORT_HOTSPOT_TSV) : NOT_APPLICABLE)
                .useHartwigCuratedHotspots(useHartwigCuratedHotspots)
                .hartwigCuratedHotspotTsv(useHartwigCuratedHotspots ? nonOptionalFile(cmd, HARTWIG_CURATED_HOTSPOT_TSV) : NOT_APPLICABLE)
                .useHartwigDriverGenes(useHartwigDriverGenes)
                .useHartwigCuratedGenes(useHartwigCuratedGenes)
                .hartwigCuratedGeneTsv(useHartwigCuratedGenes ? nonOptionalFile(cmd, HARTWIG_CURATED_GENE_TSV) : NOT_APPLICABLE)
                .missingDoidsMappingTsv(nonOptionalFile(cmd, MISSING_DOIDS_MAPPING_TSV))
                .ensemblDataDir37(nonOptionalDir(cmd, ENSEMBL_DATA_DIR_37))
                .ensemblDataDir38(nonOptionalDir(cmd, ENSEMBL_DATA_DIR_38))
                .refGenome37FastaFile(nonOptionalFile(cmd, REF_GENOME_37_FASTA_FILE))
                .refGenome38FastaFile(nonOptionalFile(cmd, REF_GENOME_38_FASTA_FILE))
                .refGenome37To38Chain(nonOptionalFile(cmd, REF_GENOME_37_TO_38_CHAIN))
                .refGenome38To37Chain(nonOptionalFile(cmd, REF_GENOME_38_TO_37_CHAIN))
                .driverGene37Tsv(nonOptionalFile(cmd, DRIVER_GENE_37_TSV))
                .driverGene38Tsv(nonOptionalFile(cmd, DRIVER_GENE_38_TSV))
                .knownFusion37File(nonOptionalFile(cmd, KNOWN_FUSION_37_FILE))
                .knownFusion38File(nonOptionalFile(cmd, KNOWN_FUSION_38_FILE))
                .outputDir(nonOptionalDir(cmd, OUTPUT_DIR))
                .skipHotspotResolving(cmd.hasOption(SKIP_HOTSPOT_RESOLVING))
                .build();
    }

    @NotNull
    static Set<ViccSource> readViccSources(@NotNull CommandLine cmd) throws ParseException {
        Set<ViccSource> viccSources = Sets.newHashSet();
        String[] sources = nonOptionalValue(cmd, VICC_SOURCES).split(",");
        for (String source : sources) {
            viccSources.add(ViccSource.fromViccKnowledgebaseString(source));
        }
        return viccSources;
    }

    @NotNull
    static String nonOptionalValue(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = cmd.getOptionValue(param);
        if (value == null) {
            throw new ParseException("Parameter must be provided: " + param);
        }

        return value;
    }

    @NotNull
    static String nonOptionalDir(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (!pathExists(value) || !pathIsDirectory(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing directory: " + value);
        }

        return value;
    }

    @NotNull
    static String nonOptionalFile(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (!pathExists(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing file: " + value);
        }

        return value;
    }

    static boolean pathExists(@NotNull String path) {
        return Files.exists(new File(path).toPath());
    }

    static boolean pathIsDirectory(@NotNull String path) {
        return Files.isDirectory(new File(path).toPath());
    }
}

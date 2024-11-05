package com.hartwig.serve;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ServeLocalConfigProvider {

    private static final Logger LOGGER = LogManager.getLogger(ServeLocalConfigProvider.class);

    private ServeLocalConfigProvider() {
    }

    @NotNull
    public static ServeConfig create() throws IOException {
        String hostname = InetAddress.getLocalHost().getHostName();
        LOGGER.info("Generating config for '{}'", hostname);

        ImmutableServeConfig.Builder builder = ImmutableServeConfig.builder()
                .useVicc(false)
                .useCkb(false)
                .useDocm(false)
                .docmTsv(Strings.EMPTY)
                .useHartwigCuratedHotspots(false)
                .hartwigCuratedHotspotTsv(Strings.EMPTY)
                .useHartwigCohortHotspots(false)
                .hartwigCohortHotspotTsv(Strings.EMPTY)
                .useHartwigDriverGenes(false)
                .useHartwigCuratedGenes(false)
                .hartwigCuratedGeneTsv(Strings.EMPTY)
                .skipHotspotResolving(true);

        // SERVE-VM is a shared vm tailored for running SERVE on GCP.
        if (hostname.toLowerCase().contains("serve-vm")) {
            builder.viccJson("/data/resources/crunch/serve/vicc/all.json");
            builder.ckbDir("/data/resources/custom/ckb/latest");
            builder.ckbMolecularProfileFilterTsv("/data/resources/crunch/serve/curation/ckb_blacklist_molecular_profile.tsv");
            builder.ckbEvidenceFilterTsv("/data/resources/crunch/serve/curation/ckb_blacklist_evidence.tsv");
            builder.ckbTrialFilterTsv("/data/resources/crunch/serve/curation/ckb_blacklist_trial.tsv");
            builder.ckbDrugCurationTsv("/data/resources/crunch/serve/curation/ckb_drugs_class_curation.tsv");
            builder.ckbRegionTsv("/data/resources/crunch/serve/region/ckb_regions.tsv");

            builder.outputDir(System.getProperty("user.home") + "/tmp/serve");
            builder.missingDoidsMappingTsv("/data/resources/crunch/serve/curation/public_missing_doids_mapping.tsv");
            builder.ensemblDataDir37("/data/resources/public/ensembl_data_cache/37");
            builder.ensemblDataDir38("/data/resources/public/ensembl_data_cache/38");
            builder.driverGene37Tsv("/data/resources/public/gene_panel/37/DriverGenePanel.37.tsv");
            builder.driverGene38Tsv("/data/resources/public/gene_panel/38/DriverGenePanel.38.tsv");
            builder.knownFusion37File("/data/resources/public/fusions/37/known_fusion_data.37.csv");
            builder.knownFusion38File("/data/resources/public/fusions/38/known_fusion_data.38.csv");
            builder.refGenome37FastaFile("/data/resources/bucket/reference_genome/37/Homo_sapiens.GRCh37.GATK.illumina.fasta");
            builder.refGenome38FastaFile("/data/resources/bucket/reference_genome/38/GCA_000001405.15_GRCh38_no_alt_analysis_set.fna");
            builder.refGenome37To38Chain("/data/resources/crunch/reference_genome_liftover/hg19ToHg38.over.chain");
            builder.refGenome38To37Chain("/data/resources/crunch/reference_genome_liftover/hg38ToHg19.over.chain");
        } else {
            // Assume we run on local machine with fixed paths
            String baseDir = System.getProperty("user.home") + "/hmf/";
            builder.viccJson(baseDir + "actionable/static_sources/vicc/all.json");
            builder.ckbDir(baseDir + "actionable/ckb");
            builder.ckbMolecularProfileFilterTsv(baseDir + "actionable/curation/ckb_blacklist_molecular_profile.tsv");
            builder.ckbEvidenceFilterTsv(baseDir + "actionable/curation/ckb_blacklist_evidence.tsv");
            builder.ckbTrialFilterTsv(baseDir + "actionable/curation/ckb_blacklist_trial.tsv");
            builder.ckbDrugCurationTsv(baseDir + "actionable/curation/ckb_drugs_class_curation.tsv");
            builder.ckbRegionTsv(baseDir + "region/ckb_regions.tsv");
            builder.ckbFacilityCurationNameTsv(baseDir + "facility_curation/ckb_facility_curation_name.tsv");
            builder.ckbFacilityCurationZipTsv(baseDir + "facility_curation/ckb_facility_curation_zip.tsv");
            builder.ckbFacilityCurationManualTsv(baseDir + "facility_curation/ckb_facility_curation_manual.tsv");

            builder.outputDir(baseDir + "tmp/serve");
            builder.missingDoidsMappingTsv(baseDir + "actionable/curation/missing_doids_mapping.tsv");

            builder.ensemblDataDir37(baseDir + "repos/common-resources-public/ensembl_data_cache/37");
            builder.ensemblDataDir38(baseDir + "repos/common-resources-public/ensembl_data_cache/38");
            builder.driverGene37Tsv(baseDir + "repos/common-resources-public/gene_panel/37/DriverGenePanel.37.tsv");
            builder.driverGene38Tsv(baseDir + "repos/common-resources-public/gene_panel/38/DriverGenePanel.38.tsv");
            builder.knownFusion37File(baseDir + "repos/common-resources-public/fusions/37/known_fusion_data.37.csv");
            builder.knownFusion38File(baseDir + "repos/common-resources-public/fusions/38/known_fusion_data.38.csv");
            builder.refGenome37FastaFile(baseDir + "refgenomes/grch37/Homo_sapiens.GRCh37.GATK.illumina.fasta");
            builder.refGenome38FastaFile(baseDir + "refgenomes/grch38/GCA_000001405.15_GRCh38_no_alt_analysis_set.fna");
            builder.refGenome37To38Chain(baseDir + "refgenomes/liftover/hg19ToHg38.over.chain");
            builder.refGenome38To37Chain(baseDir + "refgenomes/liftover/hg38ToHg19.over.chain");
        }

        return builder.build();
    }
}

package com.hartwig.serve;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.curation.DoidLookupTestFactory;
import com.hartwig.serve.refgenome.RefGenomeManagerFactory;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ServeAlgoTest {

    private static final String VICC_JSON = Resources.getResource("vicc/empty.vicc.json").getPath();

    private static final String CKB_DIR = Resources.getResource("ckb_data").getPath();
    private static final String CKB_MOLECULAR_PROFILE_FILTER_TSV =
            Resources.getResource("ckb_filter/ckb_molecular_profile_filter.tsv").getPath();
    private static final String CKB_TRIAL_FILTER_TSV = Resources.getResource("ckb_filter/ckb_trial_filter.tsv").getPath();
    private static final String CKB_EVIDENCE_FILTER_TSV = Resources.getResource("ckb_filter/ckb_evidence_filter.tsv").getPath();
    private static final String CKB_REGIONS_TO_INCLUDE_TSV = Resources.getResource("ckb_region/ckb_regions_to_include.tsv").getPath();
    private static final String CKB_DRUG_CLASS_CURATION_TSV =
            Resources.getResource("ckb_curation/ckb_treatment_approach_curation.tsv").getPath();
    private static final String CKB_FACILITY_CURATION_NAME_TSV = Resources.getResource("ckb_curation/ckb_facility_name.tsv").getPath();
    private static final String CKB_FACILITY_CURATION_ZIP_TSV = Resources.getResource("ckb_curation/ckb_facility_zip.tsv").getPath();
    private static final String CKB_FACILITY_CURATION_MANUAL_TSV = Resources.getResource("ckb_curation/ckb_facility_manual.tsv").getPath();

    private static final String DOCM_TSV = Resources.getResource("docm/example.tsv").getPath();
    private static final String HARTWIG_CURATED_HOTSPOT_TSV = Resources.getResource("hartwig/hartwig_curated_hotspots.tsv").getPath();
    private static final String HARTWIG_COHORT_HOTSPOT_TSV = Resources.getResource("hartwig/hartwig_cohort_hotspots.tsv").getPath();
    private static final String HARTWIG_CURATED_GENE_TSV = Resources.getResource("hartwig/hartwig_curated_genes.tsv").getPath();

    private static final String ENSEMBL_DATA_DIR_37 = Resources.getResource("ensembl_data_cache/v37").getPath();
    private static final String ENSEMBL_DATA_DIR_38 = Resources.getResource("ensembl_data_cache/v38").getPath();

    private static final String REF_GENOME_37_FASTA_FILE = Resources.getResource("refgenome/v37/ref.fasta").getPath();
    private static final String REF_GENOME_38_FASTA_FILE = Resources.getResource("refgenome/v38/ref.fasta").getPath();
    private static final String REF_GENOME_37_TO_38_CHAIN = Resources.getResource("refgenome/liftover/V37ToV38.over.chain").getPath();
    private static final String REF_GENOME_38_TO_37_CHAIN = Resources.getResource("refgenome/liftover/V38ToV37.over.chain").getPath();

    private static final String DRIVER_GENE_37_TSV = Resources.getResource("driver_gene_panel/driver_gene_panel.37.tsv").getPath();
    private static final String DRIVER_GENE_38_TSV = Resources.getResource("driver_gene_panel/driver_gene_panel.38.tsv").getPath();
    private static final String KNOWN_FUSION_37_FILE = Resources.getResource("known_fusion_data/known_fusion_data.37.csv").getPath();
    private static final String KNOWN_FUSION_38_FILE = Resources.getResource("known_fusion_data/known_fusion_data.38.csv").getPath();

    @Test
    public void canRunServeAlgo() throws IOException {
        ServeConfig config = algoBuilder().useVicc(true)
                .viccJson(VICC_JSON)
                .addViccSources(ViccSource.CIVIC, ViccSource.CGI)
                .useCkb(true)
                .ckbDir(CKB_DIR)
                .ckbMolecularProfileFilterTsv(CKB_MOLECULAR_PROFILE_FILTER_TSV)
                .ckbEvidenceFilterTsv(CKB_EVIDENCE_FILTER_TSV)
                .ckbTrialFilterTsv(CKB_TRIAL_FILTER_TSV)
                .ckbRegionsToIncludeTsv(CKB_REGIONS_TO_INCLUDE_TSV)
                .ckbDrugCurationTsv(CKB_DRUG_CLASS_CURATION_TSV)
                .ckbFacilityCurationNameTsv(CKB_FACILITY_CURATION_NAME_TSV)
                .ckbFacilityCurationZipTsv(CKB_FACILITY_CURATION_ZIP_TSV)
                .ckbFacilityCurationManualTsv(CKB_FACILITY_CURATION_MANUAL_TSV)
                .useDocm(true)
                .docmTsv(DOCM_TSV)
                .useHartwigCohortHotspots(true)
                .hartwigCohortHotspotTsv(HARTWIG_COHORT_HOTSPOT_TSV)
                .useHartwigCuratedHotspots(true)
                .hartwigCuratedHotspotTsv(HARTWIG_CURATED_HOTSPOT_TSV)
                .useHartwigDriverGenes(true)
                .useHartwigCuratedGenes(true)
                .hartwigCuratedGeneTsv(HARTWIG_CURATED_GENE_TSV)
                .ensemblDataDir37(ENSEMBL_DATA_DIR_37)
                .ensemblDataDir38(ENSEMBL_DATA_DIR_38)
                .refGenome37FastaFile(REF_GENOME_37_FASTA_FILE)
                .refGenome38FastaFile(REF_GENOME_38_FASTA_FILE)
                .refGenome37To38Chain(REF_GENOME_37_TO_38_CHAIN)
                .refGenome38To37Chain((REF_GENOME_38_TO_37_CHAIN))
                .driverGene37Tsv(DRIVER_GENE_37_TSV)
                .driverGene38Tsv(DRIVER_GENE_38_TSV)
                .knownFusion37File(KNOWN_FUSION_37_FILE)
                .knownFusion38File(KNOWN_FUSION_38_FILE)
                .build();

        ServeAlgo algo = new ServeAlgo(RefGenomeManagerFactory.createFromServeConfig(config), DoidLookupTestFactory.dummy());

        assertNotNull(algo.run(config));
    }

    @NotNull
    private static ImmutableServeConfig.Builder algoBuilder() {
        return ImmutableServeConfig.builder().missingDoidsMappingTsv(Strings.EMPTY).outputDir(Strings.EMPTY).skipHotspotResolving(true);
    }
}
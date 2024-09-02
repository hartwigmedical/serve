package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableGeneFileTest {

    private static final String ACTIONABLE_GENE_TSV = Resources.getResource("actionable/ActionableGenes.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableGene> genes = ActionableGeneFile.read(ACTIONABLE_GENE_TSV);

        assertActionableGenes(genes);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableGeneFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableGene> regeneratedGenes = ActionableGeneFile.fromLines(ActionableGeneFile.toLines(genes), fields);

        assertEquals(genes, regeneratedGenes);
    }

    private static void assertActionableGenes(@NotNull List<ActionableGene> genes) {
        assertEquals(7, genes.size());

        ActionableGene gene1 = findByGeneAndEvent(genes, "ALK", GeneEvent.FUSION, "Lung Adenocarcinoma");
        assertEquals("ALK", gene1.gene());
        assertEquals(GeneEvent.FUSION, gene1.event());
        assertEquals("Crizotinib", DatamodelTestFactory.extractTreatment(gene1).name());
        assertEquals("Lung Adenocarcinoma", gene1.applicableCancerType().name());
        assertEquals(LocalDate.of(2021, 2, 3), gene1.date());
        assertEquals("efficacy evidence A", gene1.description());

        ActionableGene gene2 = findByGeneAndEvent(genes, "BRCA1", GeneEvent.INACTIVATION, "Solid Tumors");
        assertEquals("BRCA1", gene2.gene());
        assertEquals(GeneEvent.INACTIVATION, gene2.event());
        assertEquals("DRUP", DatamodelTestFactory.extractTreatment(gene2).name());
        assertEquals("Solid Tumors", gene2.applicableCancerType().name());
        assertEquals(LocalDate.of(2021, 2, 4), gene2.date());
        assertEquals("efficacy evidence B", gene2.description());

        ActionableGene gene3 = findByGeneAndEvent(genes, "ERBB2", GeneEvent.AMPLIFICATION, "Breast Cancer");
        assertEquals("ERBB2", gene3.gene());
        assertEquals(GeneEvent.AMPLIFICATION, gene3.event());
        assertEquals("Trastuzumab", DatamodelTestFactory.extractTreatment(gene3).name());
        assertEquals("Breast Cancer", gene3.applicableCancerType().name());
        assertEquals(LocalDate.of(2021, 2, 5), gene3.date());
        assertEquals("efficacy evidence C", gene3.description());

        ActionableGene gene4 = findByGeneAndEvent(genes, "ERBB2", GeneEvent.AMPLIFICATION, "Colorectal Cancer");
        assertEquals("ERBB2", gene4.gene());
        assertEquals(GeneEvent.AMPLIFICATION, gene4.event());
        assertEquals("Panitumumab", DatamodelTestFactory.extractTreatment(gene4).name());
        assertEquals("Colorectal Cancer", gene4.applicableCancerType().name());
        assertEquals(LocalDate.of(2021, 2, 6), gene4.date());
        assertEquals("efficacy evidence D", gene4.description());

        ActionableGene gene5 = findByGeneAndEvent(genes, "FGFR2", GeneEvent.FUSION, "Bladder Cancer");
        assertEquals("FGFR2", gene5.gene());
        assertEquals(GeneEvent.FUSION, gene5.event());
        assertEquals("Erdafitinib", DatamodelTestFactory.extractTreatment(gene5).name());
        assertEquals("Bladder Cancer", gene5.applicableCancerType().name());
        assertEquals(LocalDate.of(2021, 2, 7), gene5.date());
        assertEquals("efficacy evidence E", gene5.description());

        ActionableGene gene6 = findByGeneAndEvent(genes, "MET", GeneEvent.AMPLIFICATION, "Advanced Solid Tumor");
        assertEquals("MET", gene6.gene());
        assertEquals(GeneEvent.AMPLIFICATION, gene6.event());
        assertEquals("DRUP", DatamodelTestFactory.extractTreatment(gene6).name());
        assertEquals("Advanced Solid Tumor", gene6.applicableCancerType().name());
        assertEquals(LocalDate.of(2021, 2, 8), gene6.date());
        assertEquals("efficacy evidence F", gene6.description());

        ActionableGene gene7 = findByGeneAndEvent(genes, "NRAS", GeneEvent.ACTIVATION, "Colorectal Cancer");
        assertEquals("NRAS", gene7.gene());
        assertEquals(GeneEvent.ACTIVATION, gene7.event());
        assertEquals("Cetuximab", DatamodelTestFactory.setToField(DatamodelTestFactory.extractClinicalTrial(gene7).therapyNames()));
        assertEquals("Colorectal Cancer", gene7.applicableCancerType().name());
        assertEquals(LocalDate.EPOCH, gene7.date());
        assertEquals(Strings.EMPTY, gene7.description());
    }

    @NotNull
    private static ActionableGene findByGeneAndEvent(@NotNull List<ActionableGene> genes, @NotNull String geneName,
            @NotNull GeneEvent event, @NotNull String tumorLocation) {
        for (ActionableGene gene : genes) {
            if (gene.gene().equals(geneName) && gene.event().equals(event) && gene.applicableCancerType().name().equals(tumorLocation)) {
                return gene;
            }
        }

        throw new IllegalStateException(
                "Could not find data with gene " + geneName + " and event " + event + "and tumor location " + tumorLocation);
    }
}
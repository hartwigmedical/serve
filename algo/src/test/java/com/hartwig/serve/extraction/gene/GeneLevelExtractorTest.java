package com.hartwig.serve.extraction.gene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import com.google.common.collect.Sets;
import com.hartwig.serve.DriverGenesTestFactory;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GeneLevelExtractorTest {

    private static final GeneChecker GENE_CHECKER =
            new GeneChecker(Sets.newHashSet("KIT", "NTRK3", "STK11", "MET", "TP53", "KRAS", "NOTCH1", "BRCA1", "NTRK3", "BRAF"));

    @Test
    public void canFilterInCatalogFusion() {
        GeneLevelExtractor geneLevelExtractorIgnore =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.IGNORE);
        assertNotNull(geneLevelExtractorIgnore.extract("NTRK3", EventType.PROMISCUOUS_FUSION, "NTRK3  fusion"));

        GeneLevelExtractor geneLevelExtractorWarn =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.WARN_ONLY);
        assertNotNull(geneLevelExtractorWarn.extract("NTRK3", EventType.PROMISCUOUS_FUSION, "NTRK3  fusion"));

        GeneLevelExtractor geneLevelExtractorFilter =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.FILTER);
        assertNotNull(geneLevelExtractorFilter.extract("NTRK3", EventType.PROMISCUOUS_FUSION, "NTRK3  fusion"));
    }

    @Test
    public void canFilterNotInCatalogFusion() {
        GeneLevelExtractor geneLevelExtractorIgnore =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.IGNORE);
        assertNotNull(geneLevelExtractorIgnore.extract("KIT", EventType.PROMISCUOUS_FUSION, "KIT  fusion"));

        GeneLevelExtractor geneLevelExtractorWarn =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.WARN_ONLY);
        assertNotNull(geneLevelExtractorWarn.extract("KIT", EventType.PROMISCUOUS_FUSION, "KIT  fusion"));

        GeneLevelExtractor geneLevelExtractorFilter =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.FILTER);
        assertNull(geneLevelExtractorFilter.extract("KIT", EventType.PROMISCUOUS_FUSION, "KIT  fusion"));
    }

    @Test
    public void canFilterInCatalogWildType() {
        GeneLevelExtractor geneLevelExtractorIgnore =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.IGNORE);
        assertNotNull(geneLevelExtractorIgnore.extract("KIT", EventType.WILD_TYPE, "KIT  wild type"));

        GeneLevelExtractor geneLevelExtractorWarn =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.WARN_ONLY);
        assertNotNull(geneLevelExtractorWarn.extract("KIT", EventType.WILD_TYPE, "KIT  wild type"));

        GeneLevelExtractor geneLevelExtractorFilter =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.FILTER);
        assertNotNull(geneLevelExtractorFilter.extract("KIT", EventType.WILD_TYPE, "KIT  wild type"));
    }

    @Test
    public void canFilterNotInCatalogWildType() {
        GeneLevelExtractor geneLevelExtractorIgnore =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KRAS"), DriverInconsistencyMode.IGNORE);
        assertNotNull(geneLevelExtractorIgnore.extract("BRAF", EventType.WILD_TYPE, "BRAF  wild type"));

        GeneLevelExtractor geneLevelExtractorWarn =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KRAS"), DriverInconsistencyMode.WARN_ONLY);
        assertNotNull(geneLevelExtractorWarn.extract("BRAF", EventType.WILD_TYPE, "BRAF  wild type"));

        GeneLevelExtractor geneLevelExtractorFilter =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KRAS"), DriverInconsistencyMode.FILTER);
        assertNull(geneLevelExtractorFilter.extract("BRAF", EventType.WILD_TYPE, "BRAF  wild type"));
    }

    @Test
    public void canFilterInCatalogGeneLevel() {
        GeneLevelExtractor geneLevelExtractorIgnore =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.IGNORE);
        assertNotNull(geneLevelExtractorIgnore.extract("KIT", EventType.GENE_LEVEL, "KIT  mutant"));

        GeneLevelExtractor geneLevelExtractorWarnMis =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.WARN_ONLY);
        assertNotNull(geneLevelExtractorWarnMis.extract("STK11", EventType.GENE_LEVEL, "STK11  act mut"));

        GeneLevelExtractor geneLevelExtractorFilter =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.FILTER);
        assertNotNull(geneLevelExtractorFilter.extract("KIT", EventType.GENE_LEVEL, "KIT  mutant"));

        GeneLevelExtractor geneLevelExtractorFilterMismatch =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.FILTER);
        assertNull(geneLevelExtractorFilterMismatch.extract("STK11", EventType.GENE_LEVEL, "STK11  act mut"));
    }

    @Test
    public void canFilterNotInCatalogGeneLevel() {
        GeneLevelExtractor geneLevelExtractorIgnore =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.IGNORE);
        assertNotNull(geneLevelExtractorIgnore.extract("MET", EventType.GENE_LEVEL, "MET  activation"));

        GeneLevelExtractor geneLevelExtractorWarn =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.WARN_ONLY);
        assertNotNull(geneLevelExtractorWarn.extract("MET", EventType.GENE_LEVEL, "MET  activation"));

        GeneLevelExtractor geneLevelExtractorFilter =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.FILTER);
        assertNull(geneLevelExtractorFilter.extract("MET", EventType.GENE_LEVEL, "MET  activation"));
    }

    @Test
    public void canExtractGeneLevelEventWildType() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.IGNORE);
        GeneAnnotation geneAnnotation = geneLevelExtractor.extract("KIT", EventType.WILD_TYPE, "KIT  wild type");

        assertNotNull(geneAnnotation);
        assertEquals("KIT", geneAnnotation.gene());
        assertEquals(GeneEvent.WILD_TYPE, geneAnnotation.event());
    }

    @Test
    public void canExtractGeneLevelEventOnco() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.IGNORE);
        GeneAnnotation geneAnnotation = geneLevelExtractor.extract("KIT", EventType.GENE_LEVEL, "KIT  positive");

        assertNotNull(geneAnnotation);
        assertEquals("KIT", geneAnnotation.gene());
        assertEquals(GeneEvent.ACTIVATION, geneAnnotation.event());
    }

    @Test
    public void canExtractGeneLevelEventTsg() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("TP53", "KIT"), DriverInconsistencyMode.IGNORE);
        GeneAnnotation geneAnnotation = geneLevelExtractor.extract("TP53", EventType.GENE_LEVEL, "TP53  negative");

        assertNotNull(geneAnnotation);
        assertEquals("TP53", geneAnnotation.gene());
        assertEquals(GeneEvent.INACTIVATION, geneAnnotation.event());
    }

    @Test
    public void pickEventClassificationOnConflict() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "KIT"), DriverInconsistencyMode.FILTER);

        GeneAnnotation conflictingGeneAnnotation = geneLevelExtractor.extract("STK11", EventType.GENE_LEVEL, "STK11 positive");
        assertNull(conflictingGeneAnnotation);
    }

    @Test
    public void canExtractGeneLevelEventGeneral() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "MET"), DriverInconsistencyMode.IGNORE);
        GeneAnnotation geneAnnotation = geneLevelExtractor.extract("STK11", EventType.GENE_LEVEL, "Truncating Mutations");

        assertNotNull(geneAnnotation);
        assertEquals("STK11", geneAnnotation.gene());
        assertEquals(GeneEvent.ANY_MUTATION, geneAnnotation.event());
    }

    @Test
    public void canExtractGeneLevelEventFusion() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "MET"), DriverInconsistencyMode.IGNORE);
        GeneAnnotation geneAnnotation = geneLevelExtractor.extract("NTRK3", EventType.PROMISCUOUS_FUSION, "NTRK3 fusion");

        assertNotNull(geneAnnotation);
        assertEquals("NTRK3", geneAnnotation.gene());
        assertEquals(GeneEvent.FUSION, geneAnnotation.event());
    }

    @Test
    public void filtersNonExistingGenes() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("STK11", "MET"), DriverInconsistencyMode.IGNORE);
        assertNull(geneLevelExtractor.extract("NOT-A-GENE", EventType.PROMISCUOUS_FUSION, "NTRK3 fusion"));
    }

    @Test
    public void canExtractGeneLevelEvent() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("NOTCH1", "MET"), DriverInconsistencyMode.FILTER);

        assertEquals(testBuilder().gene("MET").event(GeneEvent.ACTIVATION).build(),
                geneLevelExtractor.extractGeneLevelEvent("MET", "MET activating mutation"));
        assertEquals(testBuilder().gene("MET").event(GeneEvent.ACTIVATION).build(),
                geneLevelExtractor.extractGeneLevelEvent("MET", "MET act mut"));
        assertEquals(testBuilder().gene("NOTCH1").event(GeneEvent.INACTIVATION).build(),
                geneLevelExtractor.extractGeneLevelEvent("NOTCH1", "LOSS-OF-FUNCTION"));
        assertEquals(testBuilder().gene("NOTCH1").event(GeneEvent.INACTIVATION).build(),
                geneLevelExtractor.extractGeneLevelEvent("NOTCH1", "inact mut"));

        assertEquals(testBuilder().gene("MET").event(GeneEvent.ANY_MUTATION).build(),
                geneLevelExtractor.extractGeneLevelEvent("MET", "MUTATION"));
        assertEquals(testBuilder().gene("NOTCH1").event(GeneEvent.ANY_MUTATION).build(),
                geneLevelExtractor.extractGeneLevelEvent("NOTCH1", "MUTATION"));
        assertEquals(testBuilder().gene("NOTCH1").event(GeneEvent.ANY_MUTATION).build(),
                geneLevelExtractor.extractGeneLevelEvent("NOTCH1", "NOTCH1 "));
        assertNull(geneLevelExtractor.extractGeneLevelEvent("BRCA1", "BRCA1"));
        assertNull(geneLevelExtractor.extractGeneLevelEvent("KRAS", "not a gene level event"));
    }

    @Test
    public void canExtractWildTypeEvents() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("NOTCH1", "MET"), DriverInconsistencyMode.IGNORE);
        assertEquals(testBuilder().gene("MET").event(GeneEvent.WILD_TYPE).build(),
                geneLevelExtractor.extractWildTypeEvent("MET", EventType.WILD_TYPE));
        assertEquals(testBuilder().gene("AB").event(GeneEvent.WILD_TYPE).build(),
                geneLevelExtractor.extractWildTypeEvent("AB", EventType.WILD_TYPE));
        assertNotEquals(testBuilder().gene("AB").event(GeneEvent.WILD_TYPE).build(),
                geneLevelExtractor.extractWildTypeEvent("TP53", EventType.WILD_TYPE));
    }

    @Test(expected = IllegalStateException.class)
    public void hasIllegalProteinEvents() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("NOTCH1", "MET"), DriverInconsistencyMode.IGNORE);
        geneLevelExtractor.extractProteinEvent("NOTCH1", EventType.OVEREXPRESSION);
    }

    @Test
    public void canExtractProteinEvents() {
        GeneLevelExtractor geneLevelExtractor =
                createWithDriverGenes(DriverGenesTestFactory.createDriverGenes("NOTCH1", "MET"), DriverInconsistencyMode.IGNORE);
        assertEquals(testBuilder().gene("MET").event(GeneEvent.ABSENCE_OF_PROTEIN).build(),
                geneLevelExtractor.extractProteinEvent("MET", EventType.ABSENCE_OF_PROTEIN));
        assertEquals(testBuilder().gene("NOTCH1").event(GeneEvent.PRESENCE_OF_PROTEIN).build(),
                geneLevelExtractor.extractProteinEvent("NOTCH1", EventType.PRESENCE_OF_PROTEIN));
    }

    @Test
    public void canDetermineGeneLevelFromDriverGenes() {
        List<DriverGene> driverGenes = DriverGenesTestFactory.createDriverGenes("STK11", "MET");

        assertEquals(GeneEvent.ACTIVATION, GeneLevelExtractor.determineGeneLevelEventFromDriverGenes(driverGenes, "MET"));
        assertEquals(GeneEvent.INACTIVATION, GeneLevelExtractor.determineGeneLevelEventFromDriverGenes(driverGenes, "STK11"));
        assertEquals(GeneEvent.ANY_MUTATION, GeneLevelExtractor.determineGeneLevelEventFromDriverGenes(driverGenes, "MAP1K1"));
    }

    @NotNull
    private static GeneLevelExtractor createWithDriverGenes(@NotNull List<DriverGene> driverGenes,
            @NotNull DriverInconsistencyMode annotation) {
        return new GeneLevelExtractor(GENE_CHECKER,
                GENE_CHECKER,
                driverGenes,
                RefGenomeResourceTestFactory.buildTestResource37().knownFusionCache(),
                Sets.newHashSet("positive", "activating mutation", "act mut"),
                Sets.newHashSet("negative", "LOSS-OF-FUNCTION", "inact mut"),
                Sets.newHashSet("mutant"),
                annotation);
    }

    @NotNull
    private static ImmutableGeneAnnotationImpl.Builder testBuilder() {
        return ImmutableGeneAnnotationImpl.builder().from(GeneTestFactory.createTestGeneAnnotation());
    }
}
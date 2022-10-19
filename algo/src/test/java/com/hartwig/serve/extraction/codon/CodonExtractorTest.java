package com.hartwig.serve.extraction.codon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Sets;
import com.hartwig.serve.DriverGenesTestFactory;
import com.hartwig.serve.EnsemblDataCacheTestFactory;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivercatalog.panel.DriverGene;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;
import com.hartwig.serve.extraction.util.MutationTypeFilter;
import com.hartwig.serve.extraction.util.MutationTypeFilterAlgo;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

public class CodonExtractorTest {

    @Test
    public void canFindByGene() {
        List<DriverGene> driverGenes = createTestDriverGenes();
        assertTrue(CodonExtractor.geneInDriverGenes(driverGenes, "TP53"));
        assertTrue(CodonExtractor.geneInDriverGenes(driverGenes, "KRAS"));
        assertFalse(CodonExtractor.geneInDriverGenes(driverGenes, "AB"));
    }

    @Test
    public void canCheckFilterNotInCatalog() {
        CodonExtractor extractorFilter = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.FILTER);
        assertNull(extractorFilter.extract("EGFR", null, EventType.CODON, "R249"));

        CodonExtractor extractorWarn = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.WARN_ONLY);
        assertEquals(1, extractorWarn.extract("EGFR", null, EventType.CODON, "R249").size());

        CodonExtractor extractorIgnore = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.IGNORE);
        assertEquals(1, extractorIgnore.extract("EGFR", null, EventType.CODON, "R249").size());
    }

    @Test
    public void canCheckFilterInCatalog() {
        CodonExtractor extractorFilter = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.FILTER);
        assertEquals(1, extractorFilter.extract("TP53", null, EventType.CODON, "R249").size());

        CodonExtractor extractorWarn = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.WARN_ONLY);
        assertEquals(1, extractorWarn.extract("TP53", null, EventType.CODON, "R249").size());

        CodonExtractor extractorIgnore = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.IGNORE);
        assertEquals(1, extractorIgnore.extract("TP53", null, EventType.CODON, "R249").size());
    }

    @Test
    public void canExtractSimpleCodon() {
        CodonExtractor extractor = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.IGNORE);
        List<CodonAnnotation> codons = extractor.extract("TP53", null, EventType.CODON, "R249");

        assertEquals(1, codons.size());

        Assert.assertEquals("17", codons.get(0).chromosome());
        Assert.assertEquals(7577534, codons.get(0).start());
        Assert.assertEquals(7577536, codons.get(0).end());
        Assert.assertEquals("TP53", codons.get(0).gene());
        Assert.assertEquals(MutationTypeFilter.ANY, codons.get(0).mutationType());
    }

    @Test
    public void canExtractCodonOnMultipleExons() {
        CodonExtractor extractor = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.IGNORE);
        List<CodonAnnotation> codons = extractor.extract("KRAS", null, EventType.CODON, "R97");

        assertEquals(2, codons.size());

        Assert.assertEquals("12", codons.get(0).chromosome());
        Assert.assertEquals(25378707, codons.get(0).start());
        Assert.assertEquals(25378707, codons.get(0).end());
        Assert.assertEquals("KRAS", codons.get(0).gene());
        Assert.assertEquals(MutationTypeFilter.MISSENSE, codons.get(0).mutationType());

        Assert.assertEquals("12", codons.get(1).chromosome());
        Assert.assertEquals(25380168, codons.get(1).start());
        Assert.assertEquals(25380169, codons.get(1).end());
        Assert.assertEquals("KRAS", codons.get(1).gene());
        Assert.assertEquals(MutationTypeFilter.MISSENSE, codons.get(1).mutationType());
    }

    @Test
    public void failsOnTranscriptMismatch() {
        CodonExtractor extractor = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.WARN_ONLY);
        assertNull(extractor.extract("KRAS", "not the canonical transcript", EventType.CODON, "R97"));
    }

    @Test
    public void failsOnUnresolvableCodonIndex() {
        CodonExtractor extractor = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.WARN_ONLY);
        assertNull(extractor.extract("KRAS", "ENST00000256078", EventType.CODON, "Not a codon"));
    }

    @Test
    public void failsOnNonExistingCodonIndex() {
        CodonExtractor extractor = createWithDriverGenes(createTestDriverGenes(), DriverInconsistencyMode.WARN_ONLY);
        assertNull(extractor.extract("KRAS", "ENST00000256078", EventType.CODON, "R10000"));
    }

    @Test
    public void canExtractCodonIndices() {
        assertEquals(600, (int) CodonExtractor.extractCodonRank("BRAF (V600)"));
        assertEquals(742, (int) CodonExtractor.extractCodonRank("W742"));
        assertEquals(179, (int) CodonExtractor.extractCodonRank("Q179X"));
        assertEquals(61, (int) CodonExtractor.extractCodonRank("KRAS Q61X"));

        assertNull(CodonExtractor.extractCodonRank("Not a codon number"));
    }

    @NotNull
    private static List<DriverGene> createTestDriverGenes() {
        return DriverGenesTestFactory.createDriverGenes("TP53", "KRAS");
    }

    @NotNull
    private static CodonExtractor createWithDriverGenes(@NotNull List<DriverGene> driverGenes,
            @NotNull DriverInconsistencyMode annotation) {
        return new CodonExtractor(new GeneChecker(Sets.newHashSet("TP53", "KRAS", "EGFR")),
                new MutationTypeFilterAlgo(driverGenes),
                EnsemblDataCacheTestFactory.create37(),
                annotation,
                createTestDriverGenes());
    }
}
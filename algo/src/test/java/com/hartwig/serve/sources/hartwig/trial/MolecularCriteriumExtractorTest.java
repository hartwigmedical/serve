package com.hartwig.serve.sources.hartwig.trial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.hotspot.ActionableHotspot;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;

import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class MolecularCriteriumExtractorTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2026, 2, 22);
    private static final String TEST_URL = "https://test.url";

    private final MolecularCriteriumExtractor extractor =
            MolecularCriteriumExtractorFactory.createCkbEmulatedExtractor(RefGenomeResourceTestFactory.buildTestResource37());

    @Test
    public void skipsUnknownEvents() {
        assertNull(extractor.create(entry("-", "unknown event")));
    }

    @Test
    public void skipsCombinedEvents() {
        assertNull(extractor.create(entry("-", "EGFR amp + BRAF mutation")));
    }
    
    @Test
    public void canCreateCriteriumForHotspot() {
        Set<ActionableHotspot> hotspots = extract("BRAF", "V600E").hotspots();
        assertEquals(1, hotspots.size());
        
        ActionableHotspot hotspot = hotspots.iterator().next();
        assertEquals(TEST_DATE, hotspot.sourceDate());
        assertEquals("BRAF V600E", hotspot.sourceEvent());
        assertEquals(Sets.newHashSet(TEST_URL), hotspot.sourceUrls());
    }

    @Test
    public void canCreateCriteriumForCodon() {
        assertEquals(1, extract("BRAF", "V600X").codons().size());
    }

    @Test
    public void canCreateCriteriumForExon() {
        assertEquals(1, extract("KIT", "exon 11 insertion").exons().size());
    }

    @Test
    public void canCreateCriteriumForGeneEvent() {
        assertEquals(1, extract("BRAF", "wild-type").genes().size());
        assertEquals(1, extract("BRAF", "amp").genes().size());
    }

    @Test
    public void canCreateCriteriumForFusion() {
        assertEquals(1, extract("-", "EML4-ALK fusion").fusions().size());
    }

    @Test
    public void canCreateCriteriumForCharacteristic() {
        Set<ActionableCharacteristic> characteristics =  extract("-", "TMB high").characteristics();
        assertEquals(1, characteristics.size());

        ActionableCharacteristic characteristic = characteristics.iterator().next();
        assertEquals(TEST_DATE, characteristic.sourceDate());
        assertEquals("TMB high", characteristic.sourceEvent());
        assertEquals(Sets.newHashSet(TEST_URL), characteristic.sourceUrls());
    }

    @Test
    public void canCreateCriteriumForHLA() {
        assertEquals(1, extract("HLA-A", "*01:02").hla().size());
    }
    
    @NotNull
    private MolecularCriterium extract(@NotNull String actionableGene, @NotNull String actionableEvent) {
        return Objects.requireNonNull(extractor.create(entry(actionableGene, actionableEvent)));
    }
    
    @NotNull
    private static HartwigTrialEntry entry(@NotNull String actionableGene, @NotNull String actionableEvent) {
        return ImmutableHartwigTrialEntry.builder()
                .date(TEST_DATE)
                .nctId("nct")
                .title("title")
                .country("NL")
                .cancerType("cancer")
                .cancerTypeDoid("123")
                .actionableGene(actionableGene)
                .actionableEvent(actionableEvent)
                .url(TEST_URL)
                .build();
    }
}
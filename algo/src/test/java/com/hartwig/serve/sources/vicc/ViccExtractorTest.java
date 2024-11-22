package com.hartwig.serve.sources.vicc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;
import com.hartwig.serve.sources.vicc.doid.DoidLookupTestFactory;
import com.hartwig.serve.vicc.annotation.ViccClassificationConfig;
import com.hartwig.serve.vicc.datamodel.Association;
import com.hartwig.serve.vicc.datamodel.ViccEntry;

import org.junit.Test;

public class ViccExtractorTest {

    @Test
    public void canExtractFromViccEntries() {
        EventClassifierConfig config = ViccClassificationConfig.build();
        ViccExtractor extractor =
                ViccExtractorFactory.create(config, RefGenomeResourceTestFactory.buildTestResource37(), DoidLookupTestFactory.dummy());

        Association association =
                ViccTestFactory.testActionableAssociation("drugs", "colorectal cancer", "DOID:123", "A", "Responsive", "http");

        List<ViccEntry> entries = Lists.newArrayList();
        entries.add(ViccTestFactory.testEntryWithGeneEventAndAssociation("KIT", "KIT Amplification", association));
        entries.add(ViccTestFactory.testEntryWithGeneEventAndAssociation("BRAF", "V600E", association));
        entries.add(ViccTestFactory.testEntryWithGeneEventAndAssociation("NTRK3", "NTRK3 Fusion", association));
        entries.add(ViccTestFactory.testEntryWithGeneEventAndAssociation("BRAF", "V600", association));
        entries.add(ViccTestFactory.testEntryWithGeneEventAndAssociation("BRAF", "Exon 3 deletion", association));
        entries.add(ViccTestFactory.testEntryWithGeneEventAndAssociation("ALK", "EML4-ALK Fusion", association));
        entries.add(ViccTestFactory.testEntryWithGeneEventAndAssociation("-", "Microsatellite_Instability_High", association));

        ExtractionResult result = extractor.extract(entries);
        assertEquals(1, result.knownEvents().hotspots().size());
        assertEquals(1, result.knownEvents().copyNumbers().size());
        assertEquals(1, result.knownEvents().fusions().size());

        // TODO (KD) Consider checking the molecular criteria (1 hotspot, 1 codon, 1 exon, 2 genes, 1 fusion, 1 characteristic
        assertEquals(7, result.evidences().size());

        assertNull(result.trials());
    }
}
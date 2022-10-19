package com.hartwig.serve.sources.vicc;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.serve.classification.EventClassifierConfig;
import com.hartwig.serve.curation.DoidLookupTestFactory;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;
import com.hartwig.serve.vicc.annotation.ViccClassificationConfig;
import com.hartwig.serve.vicc.datamodel.Association;
import com.hartwig.serve.vicc.datamodel.ViccEntry;

import org.junit.Test;

public class ViccExtractorTest {

    @Test
    public void canExtractFromViccEntries() {
        EventClassifierConfig config = ViccClassificationConfig.build();
        ViccExtractor extractor = ViccExtractorFactory.buildViccExtractor(config,
                RefGenomeResourceTestFactory.buildTestResource37(),
                DoidLookupTestFactory.dummy());

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
        assertEquals(1, result.knownHotspots().size());
        assertEquals(1, result.knownCopyNumbers().size());
        assertEquals(1, result.knownFusionPairs().size());
        assertEquals(1, result.actionableHotspots().size());
        assertEquals(2, result.actionableRanges().size());
        assertEquals(2, result.actionableGenes().size());
        assertEquals(1, result.actionableFusions().size());
        assertEquals(1, result.actionableCharacteristics().size());
    }
}
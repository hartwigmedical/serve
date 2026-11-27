package com.hartwig.serve.ckb.classification;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.CkbTestFactory;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;

import org.junit.Test;

public class CkbEventTypeExtractorTest {

    @Test
    public void entriesWithMultipleVariantsAreCombined() {
        Variant variant1 = CkbTestFactory.createVariant();
        Variant variant2 = CkbTestFactory.createVariant();
        CkbEntry entry = CkbTestFactory.createEntry(Lists.newArrayList(variant1, variant2));

        assertEquals(EventType.COMBINED, CkbEventTypeExtractor.classify(entry));
    }

    @Test
    public void canClassifyCharacteristics() {
        Variant characteristic =
                CkbTestFactory.createVariant(CkbConstants.NO_GENE, CkbConstants.MSI_NEGATIVE, CkbConstants.MSI_NEGATIVE, null);
        assertEquals(EventType.CHARACTERISTIC, CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(characteristic)));
    }
    
    @Test
    public void canClassifyExonicFusions() {
        Variant exonicFusion = CkbTestFactory.createVariant("TP53 del exon27", "del exon27", null);
        assertEquals(EventType.FUSION_PAIR, CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(exonicFusion)));
    }

    @Test
    public void canClassifyExonicFusionPairs() {
        Variant exonicFusionPair =
                CkbTestFactory.createVariant("RET", "RUFY2 exon9::RET exon12", "RUFY2 exon9::RET exon12", "fusion");
        assertEquals(EventType.FUSION_PAIR, CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(exonicFusionPair)));

        Variant exonicFusionPairFullySpacedOut =
                CkbTestFactory.createVariant("RET", "RUFY2 exon 9::RET exon 12", "RUFY2 exon 9::RET exon 12", "fusion");
        assertEquals(EventType.FUSION_PAIR, CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(exonicFusionPairFullySpacedOut)));

        Variant exonicFusionPairWithoutFusionImpact =
                CkbTestFactory.createVariant("RET", "RUFY2 exon9::RET exon12", "RUFY2 exon9::RET exon12", null);
        assertEquals(EventType.FUSION_PAIR,
                CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(exonicFusionPairWithoutFusionImpact)));
    }

    @Test
    public void canClassifyFusionPairsWithColonSeparator() {
        Variant fusionPair = CkbTestFactory.createVariant("EGFR - MET", "EGFR::MET", "EGFR::MET", "fusion");
        assertEquals(EventType.FUSION_PAIR, CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(fusionPair)));
    }

    @Test
    public void canClassifyExonicDeletes() {
        Variant exonicDelete = CkbTestFactory.createVariant("TP53 exon 27 del", "exon 27 del", null);
        assertEquals(EventType.EXON, CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(exonicDelete)));
    }
    
}
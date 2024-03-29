package com.hartwig.serve.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import com.hartwig.serve.ServeAlgoTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;

import org.junit.Test;

public class ExtractionFunctionsTest {

    @Test
    public void canMergeExtractionResults() {
        Knowledgebase source1 = Knowledgebase.VICC_CIVIC;
        Knowledgebase source2 = Knowledgebase.VICC_CGI;
        ExtractionResult result1 = ServeAlgoTestFactory.createResultForSource(source1);
        ExtractionResult result2 = ServeAlgoTestFactory.createResultForSource(source2);

        ExtractionResult merged = ExtractionFunctions.merge(Lists.newArrayList(result1, result2));

        assertEquals(1, merged.knownHotspots().size());
        KnownHotspot hotspot = merged.knownHotspots().iterator().next();
        assertTrue(hotspot.sources().contains(source1));
        assertTrue(hotspot.sources().contains(source2));

        assertEquals(1, merged.knownCodons().size());
        KnownCodon codon = merged.knownCodons().iterator().next();
        assertTrue(codon.sources().contains(source1));
        assertTrue(codon.sources().contains(source2));

        assertEquals(1, merged.knownExons().size());
        KnownExon exon = merged.knownExons().iterator().next();
        assertTrue(exon.sources().contains(source1));
        assertTrue(exon.sources().contains(source2));

        assertEquals(1, merged.knownCopyNumbers().size());
        KnownCopyNumber copyNumber = merged.knownCopyNumbers().iterator().next();
        assertTrue(copyNumber.sources().contains(source1));
        assertTrue(copyNumber.sources().contains(source2));

        assertEquals(1, merged.knownFusions().size());
        KnownFusion fusion = merged.knownFusions().iterator().next();
        assertTrue(fusion.sources().contains(source1));
        assertTrue(fusion.sources().contains(source2));

        assertEquals(2, merged.actionableHotspots().size());
        assertEquals(2, merged.actionableCodons().size());
        assertEquals(2, merged.actionableExons().size());
        assertEquals(2, merged.actionableGenes().size());
        assertEquals(2, merged.actionableFusions().size());
        assertEquals(2, merged.actionableCharacteristics().size());
        assertEquals(2, merged.actionableHLA().size());
    }
}
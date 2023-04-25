package com.hartwig.serve.common.knownfusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownFusionCacheLoaderTest {

    private static final String KNOWN_FUSION_FILE = Resources.getResource("known_fusion_cache/known_fusion_data.csv").getPath();

    @Test
    public void canLoadKnownFusionCache() throws IOException {
        KnownFusionCache cache = KnownFusionCacheLoader.load(KNOWN_FUSION_FILE);

        assertEquals(3, cache.knownFusions().size());

        KnownFusionData fusion1 = findByThreeGene(cache.knownFusions(), "NTRK3");
        assertEquals(KnownFusionType.PROMISCUOUS_3, fusion1.type());
        assertEquals(Strings.EMPTY, fusion1.fiveGene());
        assertEquals("<multiple>", fusion1.cancerTypes());
        assertEquals("pubmed1", fusion1.pubMedId());
        assertTrue(fusion1.highImpactPromiscuous());

        KnownFusionData fusion2 = findByThreeGene(cache.knownFusions(), "ALK");
        assertEquals(KnownFusionType.KNOWN_PAIR, fusion2.type());
        assertEquals("EML4", fusion2.fiveGene());
        assertEquals("<multiple>", fusion2.cancerTypes());
        assertEquals("pubmed2", fusion2.pubMedId());
        assertTrue(fusion2.highImpactPromiscuous());

        KnownFusionData fusion3 = findByThreeGene(cache.knownFusions(), "AHR");
        assertEquals(KnownFusionType.EXON_DEL_DUP, fusion3.type());
        assertEquals("AHR", fusion3.fiveGene());
        assertEquals("bladder", fusion3.cancerTypes());
        assertEquals(Strings.EMPTY, fusion3.pubMedId());
        assertFalse(fusion3.highImpactPromiscuous());
    }

    private static KnownFusionData findByThreeGene(@NotNull List<KnownFusionData> knownFusions, @NotNull String threeGeneToFind) {
        return knownFusions.stream().filter(fusion -> fusion.threeGene().equals(threeGeneToFind)).findFirst().orElseThrow();
    }
}
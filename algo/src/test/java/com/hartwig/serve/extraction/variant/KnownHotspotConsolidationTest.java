package com.hartwig.serve.extraction.variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableVariantAnnotation;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspot;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownHotspotConsolidationTest {

    @Test
    public void canConsolidateEmptyHotspots() {
        assertTrue(KnownHotspotConsolidation.consolidate(Lists.newArrayList()).isEmpty());
    }

    @Test
    public void canConsolidateHotspotsFromOneSource() {
        Knowledgebase source = Knowledgebase.HARTWIG_HOTSPOT_CURATED;
        Set<KnownHotspot> knownHotspots = Sets.newHashSet();
        knownHotspots.add(HotspotTestFactory.knownHotspotBuilder()
                .from(hotspot1())
                .addSources(source)
                .gene("gene1")
                .inputTranscript("trans1")
                .inputProteinAnnotation("prot1")
                .proteinEffect(ProteinEffect.NO_EFFECT)
                .build());

        knownHotspots.add(HotspotTestFactory.knownHotspotBuilder()
                .from(hotspot1())
                .addSources(source)
                .gene("gene1")
                .inputTranscript(null)
                .inputProteinAnnotation("prot2")
                .proteinEffect(ProteinEffect.NO_EFFECT_PREDICTED)
                .build());

        knownHotspots.add(HotspotTestFactory.knownHotspotBuilder()
                .from(hotspot2())
                .addSources(source)
                .gene("gene2")
                .inputTranscript("trans2")
                .inputProteinAnnotation("prot3")
                .build());

        Set<KnownHotspot> consolidateHotspots = KnownHotspotConsolidation.consolidate(knownHotspots);
        assertEquals(2, consolidateHotspots.size());

        KnownHotspot gene1 = findByGene(consolidateHotspots, "gene1");
        assertEquals(Sets.newHashSet(source), gene1.sources());
        assertEquals("trans1", gene1.inputTranscript());
        assertEquals("prot1", gene1.inputProteinAnnotation());
        assertEquals(ProteinEffect.NO_EFFECT, gene1.proteinEffect());

        KnownHotspot gene2 = findByGene(consolidateHotspots, "gene2");
        assertEquals(Sets.newHashSet(source), gene2.sources());
        assertEquals("trans2", gene2.inputTranscript());
        assertEquals("prot3", gene2.inputProteinAnnotation());
    }

    @Test
    public void canConsolidateHotspotsFromTwoSources() {
        String gene = "gene1";
        Knowledgebase source1 = Knowledgebase.HARTWIG_HOTSPOT_CURATED;
        Knowledgebase source2 = Knowledgebase.HARTWIG_HOTSPOT_COHORT;
        Set<KnownHotspot> knownHotspots = Sets.newHashSet();
        knownHotspots.add(HotspotTestFactory.knownHotspotBuilder()
                .from(hotspot1())
                .addSources(source1)
                .gene(gene)
                .inputTranscript(null)
                .inputProteinAnnotation("prot1")
                .build());

        knownHotspots.add(HotspotTestFactory.knownHotspotBuilder()
                .from(hotspot1())
                .addSources(source2)
                .gene(gene)
                .inputTranscript("trans2")
                .inputProteinAnnotation("prot2")
                .build());

        Set<KnownHotspot> consolidateHotspots = KnownHotspotConsolidation.consolidate(knownHotspots);
        assertEquals(1, consolidateHotspots.size());

        KnownHotspot hotspot = findByGene(consolidateHotspots, gene);
        assertEquals(Sets.newHashSet(source1, source2), hotspot.sources());
        assertEquals("trans2", hotspot.inputTranscript());
        assertEquals("prot2", hotspot.inputProteinAnnotation());
    }

    @NotNull
    private static VariantHotspot hotspot1() {
        return ImmutableVariantAnnotation.builder()
                .from(HotspotTestFactory.createTestVariantAnnotation())
                .chromosome("1")
                .position(10)
                .ref("A")
                .alt("T")
                .build();
    }

    @NotNull
    private static VariantHotspot hotspot2() {
        return ImmutableVariantAnnotation.builder()
                .from(HotspotTestFactory.createTestVariantAnnotation())
                .chromosome("1")
                .position(20)
                .ref("A")
                .alt("T")
                .build();
    }

    @NotNull
    private static KnownHotspot findByGene(@NotNull Iterable<KnownHotspot> hotspots, @NotNull String gene) {
        for (KnownHotspot hotspot : hotspots) {
            if (hotspot.gene().equals(gene)) {
                return hotspot;
            }
        }

        throw new IllegalStateException("Could not find gene in hotspots: " + gene);
    }
}
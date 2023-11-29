package com.hartwig.serve.datamodel.serialization.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.CharacteristicTestFactory;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmunoTestFactory;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.junit.Test;

public class BackwardsCompatibilityUtilTest {

    @Test(expected = IllegalStateException.class)
    public void crashOnWritingCKBActionableEvents() {
        List<ActionableCharacteristic> characteristics =
                Lists.newArrayList(CharacteristicTestFactory.createTestActionableCharacteristicForSource(Knowledgebase.CKB));
        BackwardsCompatibilityUtil.verifyActionableEventsBeforeWrite(characteristics);
    }

    @Test(expected = IllegalStateException.class)
    public void crashOnWritingCKBKnownEvents() {
        List<KnownHotspot> hotspots = Lists.newArrayList(HotspotTestFactory.createTestKnownHotspotForSource(Knowledgebase.CKB));
        BackwardsCompatibilityUtil.verifyKnownEventsBeforeWrite(hotspots);
    }

    @Test
    public void expandsActionableCharacteristicsForCKBEvidence() {
        ActionableCharacteristic characteristic =
                CharacteristicTestFactory.createTestActionableCharacteristicForSource(Knowledgebase.CKB_EVIDENCE);

        List<ActionableCharacteristic> expanded =
                BackwardsCompatibilityUtil.expandActionableCharacteristics(Lists.newArrayList(characteristic));
        assertEquals(2, expanded.size());
        assertTrue(expanded.contains(characteristic));
        assertTrue(expanded.contains(ImmutableActionableCharacteristic.builder().from(characteristic).source(Knowledgebase.CKB).build()));
    }

    @Test
    public void expandsActionableFusionsForCKBEvidence() {
        ActionableFusion fusion = FusionTestFactory.createTestActionableFusionForSource(Knowledgebase.CKB_EVIDENCE);

        List<ActionableFusion> expanded = BackwardsCompatibilityUtil.expandActionableFusions(Lists.newArrayList(fusion));
        assertEquals(2, expanded.size());
        assertTrue(expanded.contains(fusion));
        assertTrue(expanded.contains(ImmutableActionableFusion.builder().from(fusion).source(Knowledgebase.CKB).build()));
    }

    @Test
    public void expandsActionableGenesForCKBEvidence() {
        ActionableGene gene = GeneTestFactory.createTestActionableGeneForSource(Knowledgebase.CKB_EVIDENCE);

        List<ActionableGene> expanded = BackwardsCompatibilityUtil.expandActionableGenes(Lists.newArrayList(gene));
        assertEquals(2, expanded.size());
        assertTrue(expanded.contains(gene));
        assertTrue(expanded.contains(ImmutableActionableGene.builder().from(gene).source(Knowledgebase.CKB).build()));
    }

    @Test
    public void expandsActionableHLAForCKBEvidence() {
        ActionableHLA hla = ImmunoTestFactory.createTestActionableHLAForSource(Knowledgebase.CKB_EVIDENCE);

        List<ActionableHLA> expanded = BackwardsCompatibilityUtil.expandActionableHLA(Lists.newArrayList(hla));
        assertEquals(2, expanded.size());
        assertTrue(expanded.contains(hla));
        assertTrue(expanded.contains(ImmutableActionableHLA.builder().from(hla).source(Knowledgebase.CKB).build()));
    }

    @Test
    public void expandsActionableHotspotsForCKBEvidence() {
        ActionableHotspot hotspot = HotspotTestFactory.createTestActionableHotspotForSource(Knowledgebase.CKB_EVIDENCE);

        List<ActionableHotspot> expanded = BackwardsCompatibilityUtil.expandActionableHotspots(Lists.newArrayList(hotspot));
        assertEquals(2, expanded.size());
        assertTrue(expanded.contains(hotspot));
        assertTrue(expanded.contains(ImmutableActionableHotspot.builder().from(hotspot).source(Knowledgebase.CKB).build()));
    }

    @Test
    public void expandsActionableRangesForCKBEvidence() {
        ActionableRange range = RangeTestFactory.createTestActionableRangeForSource(Knowledgebase.CKB_EVIDENCE);

        List<ActionableRange> expanded = BackwardsCompatibilityUtil.expandActionableRanges(Lists.newArrayList(range));
        assertEquals(2, expanded.size());
        assertTrue(expanded.contains(range));
        assertTrue(expanded.contains(ImmutableActionableRange.builder().from(range).source(Knowledgebase.CKB).build()));
    }

    @Test
    public void patchesKnownCodonsForCKBEvidence() {
        KnownCodon codon = RangeTestFactory.createTestKnownCodonForSource(Knowledgebase.CKB_EVIDENCE);

        List<KnownCodon> patched = BackwardsCompatibilityUtil.patchKnownCodons(Lists.newArrayList(codon));
        assertEquals(1, patched.size());
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB_EVIDENCE));
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB));
    }

    @Test
    public void patchesKnownCopyNumbersForCKBEvidence() {
        KnownCopyNumber copyNumber = GeneTestFactory.createTestKnownCopyNumberForSource(Knowledgebase.CKB_EVIDENCE);

        List<KnownCopyNumber> patched = BackwardsCompatibilityUtil.patchKnownCopyNumbers(Lists.newArrayList(copyNumber));
        assertEquals(1, patched.size());
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB_EVIDENCE));
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB));
    }

    @Test
    public void patchesKnownExonsForCKBEvidence() {
        KnownExon exon = RangeTestFactory.createTestKnownExonForSource(Knowledgebase.CKB_EVIDENCE);

        List<KnownExon> patched = BackwardsCompatibilityUtil.patchKnownExons(Lists.newArrayList(exon));
        assertEquals(1, patched.size());
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB_EVIDENCE));
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB));
    }

    @Test
    public void patchesKnownFusionForCKBEvidence() {
        KnownFusion fusion = FusionTestFactory.createTestKnownFusionForSource(Knowledgebase.CKB_EVIDENCE);

        List<KnownFusion> patched = BackwardsCompatibilityUtil.patchKnownFusions(Lists.newArrayList(fusion));
        assertEquals(1, patched.size());
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB_EVIDENCE));
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB));
    }

    @Test
    public void patchesKnownGenesForCKBEvidence() {
        KnownGene gene = GeneTestFactory.createTestKnownGeneForSource(Knowledgebase.CKB_EVIDENCE);

        List<KnownGene> patched = BackwardsCompatibilityUtil.patchKnownGenes(Lists.newArrayList(gene));
        assertEquals(1, patched.size());
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB_EVIDENCE));
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB));
    }

    @Test
    public void patchesKnownHotspotsForCKBEvidence() {
        KnownHotspot hotspot = HotspotTestFactory.createTestKnownHotspotForSource(Knowledgebase.CKB_EVIDENCE);

        List<KnownHotspot> patched = BackwardsCompatibilityUtil.patchKnownHotspots(Lists.newArrayList(hotspot));
        assertEquals(1, patched.size());
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB_EVIDENCE));
        assertTrue(patched.get(0).sources().contains(Knowledgebase.CKB));
    }
}
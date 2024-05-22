package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.Treatment;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistEvidenceEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistEvidenceType;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistTestFactory;
import com.hartwig.serve.sources.ckb.blacklist.CkbEvidenceBlacklistModel;
import com.hartwig.serve.sources.ckb.blacklist.ImmutableCkbBlacklistEvidenceEntry;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class ActionableEvidenceFactoryTest {

    private static final TreatmentApproachCurator TREATMENT_APPROACH_CURATOR = TreatmentApproachTestFactory.createTestCurator();
    private static final CkbEvidenceBlacklistModel BLACKLIST_MODEL = CkbBlacklistTestFactory.createProperEvidenceBlacklist();

    @Test
    public void shouldIgnoreNonActionableKrasDeletion() {
        CkbEntry entryDeletion =
                CkbTestFactory.createEntry("KRAS", "deletion", "KRAS deletion", "sensitive", "Emerging", "AB", "AB", "A", "DOID:162");
        ActionableEvidenceFactory actionableEvidenceFactory = new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, BLACKLIST_MODEL);
        Set<ActionableEntry> entryDeletionSet = actionableEvidenceFactory.create(entryDeletion, "KRAS", "gene");
        assertEquals(0, entryDeletionSet.size());
    }

    @Test
    public void shouldCreateActionableMSIEntry() {
        CkbEntry entryCharacteristics =
                CkbTestFactory.createEntry("-", "MSI neg", "MSI neg", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        ActionableEvidenceFactory actionableEvidenceFactoryCharacteristic =
                new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, BLACKLIST_MODEL);
        Set<ActionableEntry> entryCharacteristicsSet =
                actionableEvidenceFactoryCharacteristic.create(entryCharacteristics, Strings.EMPTY, "-");
        assertEquals(1, entryCharacteristicsSet.size());
        ActionableEntry characteristics = entryCharacteristicsSet.iterator().next();
        Treatment treatment = DatamodelTestFactory.extractTreatment(characteristics);

        assertEquals(Strings.EMPTY, characteristics.sourceEvent());
        assertEquals(Knowledgebase.CKB_EVIDENCE, characteristics.source());
        assertEquals("AB", treatment.name());
        assertEquals("AB", characteristics.applicableCancerType().name());
        assertEquals("162", characteristics.applicableCancerType().doid());
        assertEquals(Sets.newHashSet(), characteristics.blacklistCancerTypes());
        assertEquals(EvidenceLevel.A, characteristics.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());
    }

    @Test
    public void shouldCreateActionableKrasAmplificationEntry() {
        CkbEntry entryAmplification = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "AB",
                "AB",
                "A",
                "DOID:163");
        ActionableEvidenceFactory actionableEvidenceFactoryAmplification =
                new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, BLACKLIST_MODEL);
        Set<ActionableEntry> entryAmplificationSet = actionableEvidenceFactoryAmplification.create(entryAmplification, "KRAS", "KRAS");
        assertEquals(1, entryAmplificationSet.size());
        ActionableEntry amplification = entryAmplificationSet.iterator().next();
        Treatment treatment = DatamodelTestFactory.extractTreatment(amplification);
        assertEquals("KRAS", amplification.sourceEvent());
        assertEquals(Knowledgebase.CKB_EVIDENCE, amplification.source());
        assertEquals("AB", treatment.name());
        assertEquals("AB", amplification.applicableCancerType().name());
        assertEquals("163", amplification.applicableCancerType().doid());
        assertTrue(amplification.blacklistCancerTypes().isEmpty());
        assertEquals(EvidenceLevel.A, amplification.level());
        assertEquals(EvidenceDirection.RESPONSIVE, amplification.direction());
    }

    @Test
    public void shouldCreateActionableBrafHotspotEntry() {
        CkbEntry entryHotspot =
                CkbTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        ActionableEvidenceFactory actionableEvidenceFactoryHotspot =
                new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, BLACKLIST_MODEL);
        Set<ActionableEntry> entryHotspotSet = actionableEvidenceFactoryHotspot.create(entryHotspot, "BRAF", "BRAF");
        assertEquals(1, entryHotspotSet.size());
        ActionableEntry hotspot = entryHotspotSet.iterator().next();
        Treatment treatment = DatamodelTestFactory.extractTreatment(hotspot);
        assertEquals("BRAF", hotspot.sourceEvent());
        assertEquals(Knowledgebase.CKB_EVIDENCE, hotspot.source());
        assertEquals("AB", treatment.name());
        assertEquals("AB", hotspot.applicableCancerType().name());
        assertEquals("162", hotspot.applicableCancerType().doid());
        assertEquals(Sets.newHashSet(), hotspot.blacklistCancerTypes());
        assertEquals(EvidenceLevel.A, hotspot.level());
        assertEquals(EvidenceDirection.RESPONSIVE, hotspot.direction());
    }

    @Test
    public void canConvertToUrlString() {
        assertEquals("predicted+-+sensitive", ActionableEvidenceFactory.toUrlString("predicted - sensitive"));
        assertEquals("predicted+-+resistant", ActionableEvidenceFactory.toUrlString("predicted - resistant"));
        assertEquals("resistant", ActionableEvidenceFactory.toUrlString("resistant"));
        assertEquals("sensitive", ActionableEvidenceFactory.toUrlString("sensitive"));
    }

    @Test
    public void canDetermineIfHasUsableEvidenceType() {
        assertTrue(ActionableEvidenceFactory.hasUsableEvidenceType("Actionable"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Prognostic"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Emerging"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Risk Factor"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Diagnostic"));
    }

    @Test
    public void canResolveLevels() {
        assertNull(ActionableEvidenceFactory.resolveLevel("NA"));
        assertEquals(EvidenceLevel.A, ActionableEvidenceFactory.resolveLevel("A"));
        assertEquals(EvidenceLevel.B, ActionableEvidenceFactory.resolveLevel("B"));
        assertEquals(EvidenceLevel.C, ActionableEvidenceFactory.resolveLevel("C"));
        assertEquals(EvidenceLevel.D, ActionableEvidenceFactory.resolveLevel("D"));
    }

    @Test
    public void canResolveDirections() {
        assertNull(ActionableEvidenceFactory.resolveDirection(null));
        assertNull(ActionableEvidenceFactory.resolveDirection("unknown"));
        assertNull(ActionableEvidenceFactory.resolveDirection("not applicable"));
        assertNull(ActionableEvidenceFactory.resolveDirection("conflicting"));
        assertNull(ActionableEvidenceFactory.resolveDirection("no benefit"));
        assertNull(ActionableEvidenceFactory.resolveDirection("not predictive"));
        assertNull(ActionableEvidenceFactory.resolveDirection("decreased response"));

        assertEquals(EvidenceDirection.RESPONSIVE, ActionableEvidenceFactory.resolveDirection("sensitive"));
        assertEquals(EvidenceDirection.PREDICTED_RESPONSIVE, ActionableEvidenceFactory.resolveDirection("predicted - sensitive"));
        assertEquals(EvidenceDirection.RESISTANT, ActionableEvidenceFactory.resolveDirection("resistant"));
        assertEquals(EvidenceDirection.PREDICTED_RESISTANT, ActionableEvidenceFactory.resolveDirection("predicted - resistant"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapy() {
        CkbEvidenceBlacklistModel model =
                createBlacklistModel(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
        CkbEntry entryBlacklist = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "Nivolumab",
                "AB",
                "B",
                "DOID:163");
        ActionableEvidenceFactory evidence = new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<ActionableEntry> interpretEvidenceEntry = evidence.create(entryBlacklist, "KRAS", "KRAS");
        assertEquals(0, interpretEvidenceEntry.size());
    }

    @Test
    public void doesNotBlacklistEvidenceOnOtherTherapy() {
        CkbEvidenceBlacklistModel model =
                createBlacklistModel(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
        CkbEntry entryBlacklist = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "AB",
                "AB",
                "B",
                "DOID:163");
        ActionableEvidenceFactory evidence = new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<ActionableEntry> interpretEvidenceEntry = evidence.create(entryBlacklist, "BRAF", "BRAF");
        assertEquals(1, interpretEvidenceEntry.size());
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndLevel() {
        CkbEvidenceBlacklistModel model =
                createBlacklistModel(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY, "Immuno", null, null, null, EvidenceLevel.A);
        CkbEntry entry = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "Immuno",
                "AB",
                "A",
                "DOID:163");
        ActionableEvidenceFactory evidence = new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<ActionableEntry> interpretEvidenceEntry = evidence.create(entry, "KRAS", "KRAS");
        assertEquals(0, interpretEvidenceEntry.size());
    }

    @Test
    public void canBlacklistAllEvidenceOnGene() {
        CkbEvidenceBlacklistModel model =
                createBlacklistModel(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE, null, null, "KRAS", null, null);
        CkbEntry entry = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "Nivolumab",
                "AB",
                "B",
                "DOID:163");
        ActionableEvidenceFactory evidence = new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<ActionableEntry> interpretEvidenceEntry = evidence.create(entry, "KRAS", "KRAS");
        assertEquals(0, interpretEvidenceEntry.size());
    }

    @Test
    public void doesNotBlacklistAllEvidenceOnGene() {
        CkbEvidenceBlacklistModel model =
                createBlacklistModel(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE, null, null, "BRAF", null, null);
        CkbEntry entry = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "Nivolumab",
                "AB",
                "B",
                "DOID:163");
        ActionableEvidenceFactory evidence = new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<ActionableEntry> interpretEvidenceEntry = evidence.create(entry, "KRAS", "KRAS");
        assertEquals(1, interpretEvidenceEntry.size());
    }

    @NotNull
    private static CkbEvidenceBlacklistModel createBlacklistModel(@NotNull CkbBlacklistEvidenceType type, @Nullable String therapy,
            @Nullable String cancerType, @Nullable String gene, @Nullable String event, @Nullable EvidenceLevel level) {
        CkbBlacklistEvidenceEntry entry = ImmutableCkbBlacklistEvidenceEntry.builder()
                .type(type)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();

        return CkbBlacklistTestFactory.createSpecificEvidenceBlacklist(entry);
    }
}
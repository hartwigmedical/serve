package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.EvidenceLevelDetails;
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
        CkbEntry entryDeletion = CkbTestFactory.createEntry("KRAS",
                "deletion",
                "KRAS deletion",
                "sensitive",
                "Emerging",
                "AB",
                "AB",
                "A",
                "Guideline",
                "DOID:162");
        ActionableEvidenceFactory actionableEvidenceFactory = new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, BLACKLIST_MODEL);
        Set<ActionableEntry> entryDeletionSet = actionableEvidenceFactory.create(entryDeletion, "KRAS", "gene");
        assertEquals(0, entryDeletionSet.size());
    }

    @Test
    public void shouldCreateActionableMSIEntry() {
        CkbEntry entryCharacteristics =
                CkbTestFactory.createEntry("-", "MSI neg", "MSI neg", "sensitive", "Actionable", "AB", "AB", "A", "Guideline", "DOID:162");
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
        assertEquals(EvidenceLevel.A, characteristics.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE, characteristics.entryDate());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, characteristics.efficacyDescription());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), characteristics.evidenceYear());
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
                "Guideline",
                "DOID:163");
        ActionableEvidenceFactory actionableEvidenceFactoryAmplification =
                new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, BLACKLIST_MODEL);
        Set<ActionableEntry> entryAmplificationSet = actionableEvidenceFactoryAmplification.create(entryAmplification, "KRAS", "KRAS");
        assertEquals(1, entryAmplificationSet.size());
        ActionableEntry amplification = entryAmplificationSet.iterator().next();
        Treatment treatment = DatamodelTestFactory.extractTreatment(amplification);
        assertEquals("KRAS", amplification.sourceEvent());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE, amplification.entryDate());
        assertEquals(Knowledgebase.CKB_EVIDENCE, amplification.source());
        assertEquals("AB", treatment.name());
        assertEquals("AB", amplification.applicableCancerType().name());
        assertEquals("163", amplification.applicableCancerType().doid());
        assertTrue(amplification.blacklistCancerTypes().isEmpty());
        assertEquals(EvidenceLevel.A, amplification.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, amplification.direction());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, amplification.efficacyDescription());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), amplification.evidenceYear());
    }

    @Test
    public void shouldCreateActionableBrafHotspotEntry() {
        CkbEntry entryHotspot = CkbTestFactory.createEntry("BRAF",
                "BRAF V600E",
                "BRAF V600E",
                "sensitive",
                "Actionable",
                "AB",
                "AB",
                "A",
                "Guideline",
                "DOID:162");
        ActionableEvidenceFactory actionableEvidenceFactoryHotspot =
                new ActionableEvidenceFactory(TREATMENT_APPROACH_CURATOR, BLACKLIST_MODEL);
        Set<ActionableEntry> entryHotspotSet = actionableEvidenceFactoryHotspot.create(entryHotspot, "BRAF", "BRAF");
        assertEquals(1, entryHotspotSet.size());
        ActionableEntry hotspot = entryHotspotSet.iterator().next();
        Treatment treatment = DatamodelTestFactory.extractTreatment(hotspot);
        assertEquals("BRAF", hotspot.sourceEvent());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE, hotspot.entryDate());
        assertEquals(Knowledgebase.CKB_EVIDENCE, hotspot.source());
        assertEquals("AB", treatment.name());
        assertEquals("AB", hotspot.applicableCancerType().name());
        assertEquals("162", hotspot.applicableCancerType().doid());
        assertEquals(Sets.newHashSet(), hotspot.blacklistCancerTypes());
        assertEquals(EvidenceLevel.A, hotspot.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, hotspot.direction());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, hotspot.efficacyDescription());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), hotspot.evidenceYear());
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
    public void canResolveEvidenceLevelDetails() {
        assertEquals(EvidenceLevelDetails.UNKNOWN, ActionableEvidenceFactory.resolveEvidenceLevelDetails("Unknown"));
        assertEquals(EvidenceLevelDetails.CLINICAL_STUDY, ActionableEvidenceFactory.resolveEvidenceLevelDetails("Clinical study"));
        assertEquals(EvidenceLevelDetails.GUIDELINE, ActionableEvidenceFactory.resolveEvidenceLevelDetails("Guideline"));
        assertEquals(EvidenceLevelDetails.CASE_REPORTS_SERIES,
                ActionableEvidenceFactory.resolveEvidenceLevelDetails("Case Reports/Case Series"));
    }

    @Test
    public void canResolveDirections() {
        assertNull(ActionableEvidenceFactory.resolveDirection(null));
        assertNull(ActionableEvidenceFactory.resolveDirection("unknown"));
        assertNull(ActionableEvidenceFactory.resolveDirection("not applicable"));
        assertNull(ActionableEvidenceFactory.resolveDirection("conflicting"));
        assertNull(ActionableEvidenceFactory.resolveDirection("not predictive"));

        assertEquals(EvidenceDirection.RESPONSIVE, ActionableEvidenceFactory.resolveDirection("sensitive"));
        assertEquals(EvidenceDirection.PREDICTED_RESPONSIVE, ActionableEvidenceFactory.resolveDirection("predicted - sensitive"));
        assertEquals(EvidenceDirection.RESISTANT, ActionableEvidenceFactory.resolveDirection("resistant"));
        assertEquals(EvidenceDirection.PREDICTED_RESISTANT, ActionableEvidenceFactory.resolveDirection("predicted - resistant"));
        assertEquals(EvidenceDirection.NO_BENEFIT, ActionableEvidenceFactory.resolveDirection("no benefit"));
        assertEquals(EvidenceDirection.DECREASED_RESPONSE, ActionableEvidenceFactory.resolveDirection("decreased response"));
    }

    @Test
    public void canExtractEvidenceYear() {
        LocalDate entryDate = LocalDate.of(2015, 1, 1);
        List<Reference> referencesWithDate = List.of(CkbTestFactory.createReference("2021"), CkbTestFactory.createReference("2023"));
        List<Reference> referencesWithoutDate = List.of(CkbTestFactory.createReference(null));
        Therapy therapy = CkbTestFactory.createTherapy(2018);
        LocalDate mostRecentDate = LocalDate.of(2024, 1, 1);

        assertEquals(2023, ActionableEvidenceFactory.extractEvidenceYear(entryDate, referencesWithDate, therapy));
        assertEquals(2023, ActionableEvidenceFactory.extractEvidenceYear(mostRecentDate, referencesWithDate, therapy));
        assertEquals(2018, ActionableEvidenceFactory.extractEvidenceYear(entryDate, referencesWithoutDate, therapy));
        assertEquals(2024, ActionableEvidenceFactory.extractEvidenceYear(mostRecentDate, referencesWithoutDate, therapy));
        assertEquals(2015, ActionableEvidenceFactory.extractEvidenceYear(entryDate, referencesWithoutDate, null));
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
                "Guideline",
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
                "Guideline",
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
                "Guideline",
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
                "Guideline",
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
                "Guideline",
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
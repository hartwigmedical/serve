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
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MolecularCriterium;
import com.hartwig.serve.datamodel.MolecularCriteriumTestFactory;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterEntry;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterModel;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterType;
import com.hartwig.serve.sources.ckb.filter.CkbFilteringTestFactory;
import com.hartwig.serve.sources.ckb.filter.ImmutableCkbEvidenceFilterEntry;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class EfficacyEvidenceFactoryTest {

    private static final TreatmentApproachCurator TREATMENT_APPROACH_CURATOR = TreatmentApproachTestFactory.createTestCurator();
    private static final CkbEvidenceFilterModel EVIDENCE_FILTER_MODEL = CkbFilteringTestFactory.createProperEvidenceFilterModel();

    @Test
    public void shouldIgnoreNonActionableKrasDeletion() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();

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
        EfficacyEvidenceFactory efficacyEvidenceFactory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<EfficacyEvidence> entryDeletionSet = efficacyEvidenceFactory.create(entryDeletion, molecularCriterium, "KRAS", "gene");
        assertEquals(0, entryDeletionSet.size());
    }

    @Test
    public void shouldCreateActionableMSIEntry() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableCharacteristic();

        CkbEntry entryCharacteristics =
                CkbTestFactory.createEntry("-", "MSI neg", "MSI neg", "sensitive", "Actionable", "AB", "AB", "A", "Guideline", "DOID:162");
        EfficacyEvidenceFactory efficacyEvidenceFactoryCharacteristic =
                new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<EfficacyEvidence> entryCharacteristicsSet =
                efficacyEvidenceFactoryCharacteristic.create(entryCharacteristics, molecularCriterium, Strings.EMPTY, "-");
        assertEquals(1, entryCharacteristicsSet.size());
        EfficacyEvidence characteristics = entryCharacteristicsSet.iterator().next();

        assertEquals(Knowledgebase.CKB, characteristics.source());
        assertEquals("AB", characteristics.treatment().name());
        assertEquals("AB", characteristics.indication().applicableType().name());
        assertEquals("162", characteristics.indication().applicableType().doid());
        assertEquals(Sets.newHashSet(), characteristics.indication().excludedSubTypes());
        assertEquals(molecularCriterium, characteristics.molecularCriterium());
        assertEquals(EvidenceLevel.A, characteristics.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.evidenceDirection());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, characteristics.efficacyDescription());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), characteristics.evidenceYear());
    }

    @Test
    public void shouldCreateActionableKrasAmplificationEntry() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();

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
        EfficacyEvidenceFactory efficacyEvidenceFactoryAmplification =
                new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<EfficacyEvidence> entryAmplificationSet =
                efficacyEvidenceFactoryAmplification.create(entryAmplification, molecularCriterium, "KRAS", "KRAS");
        assertEquals(1, entryAmplificationSet.size());
        EfficacyEvidence amplification = entryAmplificationSet.iterator().next();
        assertEquals(Knowledgebase.CKB, amplification.source());
        assertEquals("AB", amplification.treatment().name());
        assertEquals("AB", amplification.indication().applicableType().name());
        assertEquals("163", amplification.indication().applicableType().doid());
        assertTrue(amplification.indication().excludedSubTypes().isEmpty());
        assertEquals(molecularCriterium, amplification.molecularCriterium());
        assertEquals(EvidenceLevel.A, amplification.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, amplification.evidenceDirection());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, amplification.efficacyDescription());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), amplification.evidenceYear());
    }

    @Test
    public void shouldCreateActionableBrafHotspotEntry() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableHotspot();

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
        EfficacyEvidenceFactory efficacyEvidenceFactoryHotspot =
                new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<EfficacyEvidence> entryHotspotSet = efficacyEvidenceFactoryHotspot.create(entryHotspot, molecularCriterium, "BRAF", "BRAF");
        assertEquals(1, entryHotspotSet.size());
        EfficacyEvidence hotspot = entryHotspotSet.iterator().next();
        assertEquals(Knowledgebase.CKB, hotspot.source());
        assertEquals("AB", hotspot.treatment().name());
        assertEquals("AB", hotspot.indication().applicableType().name());
        assertEquals("162", hotspot.indication().applicableType().doid());
        assertEquals(Sets.newHashSet(), hotspot.indication().excludedSubTypes());
        assertEquals(molecularCriterium, hotspot.molecularCriterium());
        assertEquals(EvidenceLevel.A, hotspot.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, hotspot.evidenceDirection());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, hotspot.efficacyDescription());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), hotspot.evidenceYear());
    }

    @Test
    public void canConvertToUrlString() {
        assertEquals("predicted+-+sensitive", EfficacyEvidenceFactory.toUrlString("predicted - sensitive"));
        assertEquals("predicted+-+resistant", EfficacyEvidenceFactory.toUrlString("predicted - resistant"));
        assertEquals("resistant", EfficacyEvidenceFactory.toUrlString("resistant"));
        assertEquals("sensitive", EfficacyEvidenceFactory.toUrlString("sensitive"));
    }

    @Test
    public void canDetermineIfHasUsableEvidenceType() {
        assertTrue(EfficacyEvidenceFactory.hasUsableEvidenceType("Actionable"));
        assertFalse(EfficacyEvidenceFactory.hasUsableEvidenceType("Prognostic"));
        assertFalse(EfficacyEvidenceFactory.hasUsableEvidenceType("Emerging"));
        assertFalse(EfficacyEvidenceFactory.hasUsableEvidenceType("Risk Factor"));
        assertFalse(EfficacyEvidenceFactory.hasUsableEvidenceType("Diagnostic"));
    }

    @Test
    public void canResolveLevels() {
        assertNull(EfficacyEvidenceFactory.resolveLevel("NA"));
        assertEquals(EvidenceLevel.A, EfficacyEvidenceFactory.resolveLevel("A"));
        assertEquals(EvidenceLevel.B, EfficacyEvidenceFactory.resolveLevel("B"));
        assertEquals(EvidenceLevel.C, EfficacyEvidenceFactory.resolveLevel("C"));
        assertEquals(EvidenceLevel.D, EfficacyEvidenceFactory.resolveLevel("D"));
    }

    @Test
    public void canResolveEvidenceLevelDetails() {
        assertEquals(EvidenceLevelDetails.UNKNOWN, EfficacyEvidenceFactory.resolveEvidenceLevelDetails("Unknown"));
        assertEquals(EvidenceLevelDetails.CLINICAL_STUDY, EfficacyEvidenceFactory.resolveEvidenceLevelDetails("Clinical study"));
        assertEquals(EvidenceLevelDetails.GUIDELINE, EfficacyEvidenceFactory.resolveEvidenceLevelDetails("Guideline"));
        assertEquals(EvidenceLevelDetails.CASE_REPORTS_SERIES,
                EfficacyEvidenceFactory.resolveEvidenceLevelDetails("Case Reports/Case Series"));
    }

    @Test
    public void canResolveDirections() {
        assertNull(EfficacyEvidenceFactory.resolveDirection(null));
        assertNull(EfficacyEvidenceFactory.resolveDirection("unknown"));
        assertNull(EfficacyEvidenceFactory.resolveDirection("not applicable"));
        assertNull(EfficacyEvidenceFactory.resolveDirection("conflicting"));
        assertNull(EfficacyEvidenceFactory.resolveDirection("not predictive"));

        assertEquals(EvidenceDirection.RESPONSIVE, EfficacyEvidenceFactory.resolveDirection("sensitive"));
        assertEquals(EvidenceDirection.PREDICTED_RESPONSIVE, EfficacyEvidenceFactory.resolveDirection("predicted - sensitive"));
        assertEquals(EvidenceDirection.RESISTANT, EfficacyEvidenceFactory.resolveDirection("resistant"));
        assertEquals(EvidenceDirection.PREDICTED_RESISTANT, EfficacyEvidenceFactory.resolveDirection("predicted - resistant"));
        assertEquals(EvidenceDirection.NO_BENEFIT, EfficacyEvidenceFactory.resolveDirection("no benefit"));
        assertEquals(EvidenceDirection.DECREASED_RESPONSE, EfficacyEvidenceFactory.resolveDirection("decreased response"));
    }

    @Test
    public void canExtractEvidenceYear() {
        LocalDate entryDate = LocalDate.of(2015, 1, 1);
        List<Reference> referencesWithDate = List.of(CkbTestFactory.createReference("2021"), CkbTestFactory.createReference("2023"));
        List<Reference> referencesWithoutDate = List.of(CkbTestFactory.createReference(null));
        Therapy therapy = CkbTestFactory.createTherapy(2018);
        LocalDate mostRecentDate = LocalDate.of(2024, 1, 1);

        assertEquals(2023, EfficacyEvidenceFactory.extractEvidenceYear(entryDate, referencesWithDate, therapy));
        assertEquals(2023, EfficacyEvidenceFactory.extractEvidenceYear(mostRecentDate, referencesWithDate, therapy));
        assertEquals(2018, EfficacyEvidenceFactory.extractEvidenceYear(entryDate, referencesWithoutDate, therapy));
        assertEquals(2024, EfficacyEvidenceFactory.extractEvidenceYear(mostRecentDate, referencesWithoutDate, therapy));
        assertEquals(2015, EfficacyEvidenceFactory.extractEvidenceYear(entryDate, referencesWithoutDate, null));
    }

    @Test
    public void canBlacklistEvidenceOnTherapy() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createBlacklistModel(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
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
        EfficacyEvidenceFactory evidence = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> interpretEvidenceEntry = evidence.create(entryBlacklist, molecularCriterium, "KRAS", "KRAS");
        assertEquals(0, interpretEvidenceEntry.size());
    }

    @Test
    public void doesNotBlacklistEvidenceOnOtherTherapy() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createBlacklistModel(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
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
        EfficacyEvidenceFactory evidence = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> interpretEvidenceEntry = evidence.create(entryBlacklist, molecularCriterium, "BRAF", "BRAF");
        assertEquals(1, interpretEvidenceEntry.size());
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndLevel() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createBlacklistModel(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, "Immuno", null, null, null, EvidenceLevel.A);
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
        EfficacyEvidenceFactory evidence = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> interpretEvidenceEntry = evidence.create(entry, molecularCriterium, "KRAS", "KRAS");
        assertEquals(0, interpretEvidenceEntry.size());
    }

    @Test
    public void canBlacklistAllEvidenceOnGene() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createBlacklistModel(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE, null, null, "KRAS", null, null);
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
        EfficacyEvidenceFactory evidence = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> interpretEvidenceEntry = evidence.create(entry, molecularCriterium, "KRAS", "KRAS");
        assertEquals(0, interpretEvidenceEntry.size());
    }

    @Test
    public void doesNotBlacklistAllEvidenceOnGene() {
        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createBlacklistModel(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE, null, null, "BRAF", null, null);
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
        EfficacyEvidenceFactory evidence = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> interpretEvidenceEntry = evidence.create(entry, molecularCriterium, "KRAS", "KRAS");
        assertEquals(1, interpretEvidenceEntry.size());
    }

    @NotNull
    private static CkbEvidenceFilterModel createBlacklistModel(@NotNull CkbEvidenceFilterType type, @Nullable String therapy,
            @Nullable String cancerType, @Nullable String gene, @Nullable String event, @Nullable EvidenceLevel level) {
        CkbEvidenceFilterEntry entry = ImmutableCkbEvidenceFilterEntry.builder()
                .type(type)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();

        return CkbFilteringTestFactory.createSpecificEvidenceFilterModel(entry);
    }
}
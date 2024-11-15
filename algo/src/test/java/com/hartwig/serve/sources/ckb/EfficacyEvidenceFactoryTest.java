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
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.EvidenceDirection;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevel;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriteriumTestFactory;
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
    public void shouldCreateEvidenceForMSIEntry() {
        CkbEntry msiEntry = CkbTestFactory.createEntry("-", "MSI neg", "MSI neg", "sensitive",
                "Actionable",
                "Therapy",
                "Indication",
                "A",
                "Guideline",
                "DOID:162");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableCharacteristic();
        Set<EfficacyEvidence> evidences = factory.create(msiEntry, molecularCriterium, Strings.EMPTY, "-");
        assertEquals(1, evidences.size());
        EfficacyEvidence evidence = evidences.iterator().next();

        assertEquals(Knowledgebase.CKB, evidence.source());
        assertEquals("Therapy", evidence.treatment().name());
        assertEquals("Indication", evidence.indication().applicableType().name());
        assertEquals("162", evidence.indication().applicableType().doid());
        assertEquals(Sets.newHashSet(), evidence.indication().excludedSubTypes());
        assertEquals(molecularCriterium, Set.of(evidence.molecularCriterium()));
        assertEquals(EvidenceLevel.A, evidence.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, evidence.evidenceDirection());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, evidence.efficacyDescription());
        assertEquals(EvidenceLevelDetails.GUIDELINE, evidence.evidenceLevelDetails());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), evidence.evidenceYear());
        assertEquals(Sets.newHashSet(), evidence.urls());
    }

    @Test
    public void shouldCreateEvidenceForKrasAmplificationEntry() {
        CkbEntry entryAmplification = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "Therapy",
                "Indication",
                "A",
                "Guideline",
                "DOID:163");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        Set<EfficacyEvidence> evidences = factory.create(entryAmplification, molecularCriterium, "KRAS", "KRAS");
        assertEquals(1, evidences.size());

        EfficacyEvidence evidence = evidences.iterator().next();
        assertEquals(Knowledgebase.CKB, evidence.source());
        assertEquals("Therapy", evidence.treatment().name());
        assertEquals("Indication", evidence.indication().applicableType().name());
        assertEquals("163", evidence.indication().applicableType().doid());
        assertTrue(evidence.indication().excludedSubTypes().isEmpty());
        assertEquals(molecularCriterium, Set.of(evidence.molecularCriterium()));
        assertEquals(EvidenceLevel.A, evidence.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, evidence.evidenceDirection());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, evidence.efficacyDescription());
        assertEquals(EvidenceLevelDetails.GUIDELINE, evidence.evidenceLevelDetails());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), evidence.evidenceYear());
        assertEquals(Sets.newHashSet(), evidence.urls());
    }

    @Test
    public void shouldCreateEvidenceForBrafHotspotEntry() {
        CkbEntry entryHotspot = CkbTestFactory.createEntry("BRAF",
                "BRAF V600E",
                "BRAF V600E",
                "sensitive",
                "Actionable",
                "Therapy",
                "Indication",
                "A",
                "Guideline",
                "DOID:162");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableHotspot();
        Set<EfficacyEvidence> evidences = factory.create(entryHotspot, molecularCriterium, "BRAF", "BRAF");
        assertEquals(1, evidences.size());

        EfficacyEvidence evidence = evidences.iterator().next();
        assertEquals(Knowledgebase.CKB, evidence.source());
        assertEquals("Therapy", evidence.treatment().name());
        assertEquals("Indication", evidence.indication().applicableType().name());
        assertEquals("162", evidence.indication().applicableType().doid());
        assertEquals(Sets.newHashSet(), evidence.indication().excludedSubTypes());
        assertEquals(molecularCriterium, Set.of(evidence.molecularCriterium()));
        assertEquals(EvidenceLevel.A, evidence.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, evidence.evidenceDirection());
        assertEquals(CkbTestFactory.EFFICACY_EVIDENCE, evidence.efficacyDescription());
        assertEquals(EvidenceLevelDetails.GUIDELINE, evidence.evidenceLevelDetails());
        assertEquals(CkbTestFactory.TEST_CREATE_DATE.getYear(), evidence.evidenceYear());
        assertEquals(Sets.newHashSet(), evidence.urls());
    }

    @Test
    public void shouldIgnoreKrasDeletionWithEmergingEvidence() {
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEntry entryDeletion = create("KRAS", "deletion", "KRAS deletion", "sensitive", "Emerging");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, EVIDENCE_FILTER_MODEL);
        Set<EfficacyEvidence> evidences = factory.create(entryDeletion, molecularCriterium, "KRAS", "gene");
        assertEquals(0, evidences.size());
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
    public void canFilterEvidenceOnTherapy() {
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        String therapy = "Nivolumab";
        CkbEvidenceFilterModel model =
                createEvidenceFilterModel(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, therapy, null, null, null, null);

        CkbEntry entry = createForTherapy(therapy);

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> evidences = factory.create(entry, molecularCriterium, "KRAS", "KRAS");
        assertEquals(0, evidences.size());
    }

    @Test
    public void doesNotFilterEvidenceOnOtherTherapy() {
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createEvidenceFilterModel(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);

        CkbEntry entry = createForTherapyAndLevel("Immuno", "A");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> evidences = factory.create(entry, molecularCriterium, "BRAF", "BRAF");
        assertEquals(1, evidences.size());
    }

    @Test
    public void canFilterEvidenceOnTherapyAndLevel() {
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createEvidenceFilterModel(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, "Immuno", null, null, null, EvidenceLevel.A);
        CkbEntry entry = createForTherapyAndLevel("Immuno", "A");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> evidences = factory.create(entry, molecularCriterium, "KRAS", "KRAS");
        assertEquals(0, evidences.size());
    }

    @Test
    public void canFilterAllEvidenceOnGene() {
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createEvidenceFilterModel(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE, null, null, "KRAS", null, null);
        CkbEntry entry = create("KRAS", "KRAS amplification", "KRAS amplification", "sensitive", "Actionable");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> evidences = factory.create(entry, molecularCriterium, "KRAS", "KRAS");
        assertEquals(0, evidences.size());
    }

    @Test
    public void doesNotFilterEvidenceOnOtherGene() {
        Set<MolecularCriterium> molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableGene();
        CkbEvidenceFilterModel model =
                createEvidenceFilterModel(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE, null, null, "BRAF", null, null);
        CkbEntry entry = create("KRAS", "KRAS amplification", "KRAS amplification", "sensitive", "Actionable");

        EfficacyEvidenceFactory factory = new EfficacyEvidenceFactory(TREATMENT_APPROACH_CURATOR, model);
        Set<EfficacyEvidence> evidences = factory.create(entry, molecularCriterium, "KRAS", "KRAS");
        assertEquals(1, evidences.size());
    }

    @NotNull
    private static CkbEntry createForTherapy(@NotNull String therapy) {
        return createForTherapyAndLevel(therapy, "A");
    }

    @NotNull
    private static CkbEntry createForTherapyAndLevel(@NotNull String therapy, @NotNull String level) {
        return CkbTestFactory.createEntry("any gene",
                "any variant",
                "any full name",
                "sensitive",
                "Actionable",
                therapy,
                "Indication",
                level,
                "Guideline",
                "DOID:162");
    }

    @NotNull
    private static CkbEntry create(@NotNull String geneSymbol, @NotNull String variant, @NotNull String fullName,
            @NotNull String responseType, @NotNull String evidenceType) {
        return CkbTestFactory.createEntry(geneSymbol,
                variant,
                fullName,
                responseType,
                evidenceType,
                "Therapy",
                "Indication",
                "A",
                "Guideline",
                "DOID:162");
    }

    @NotNull
    private static CkbEvidenceFilterModel createEvidenceFilterModel(@NotNull CkbEvidenceFilterType type, @Nullable String therapy,
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
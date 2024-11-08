package com.hartwig.serve.sources.vicc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.MolecularCriterium;
import com.hartwig.serve.datamodel.MolecularCriteriumTestFactory;
import com.hartwig.serve.sources.vicc.curation.DrugCurator;
import com.hartwig.serve.sources.vicc.curation.EvidenceLevelCurator;
import com.hartwig.serve.sources.vicc.doid.DoidLookupTestFactory;
import com.hartwig.serve.vicc.datamodel.Association;
import com.hartwig.serve.vicc.datamodel.ImmutableViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccEntry;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EfficacyEvidenceFactoryTest {

    @Test
    public void canResolveActionableEventWithMultipleCancerTypes() {
        String cancerTypeA = "cancerTypeA";
        String cancerTypeB = "cancerTypeB";

        Map<String, Set<String>> doidLookupMap = Maps.newHashMap();
        doidLookupMap.put(cancerTypeA, Sets.newHashSet("1"));
        doidLookupMap.put(cancerTypeB, Sets.newHashSet(CancerTypeConstants.CANCER_DOID));
        ActionableEvidenceFactory factory =
                new ActionableEvidenceFactory(DoidLookupTestFactory.create(doidLookupMap), new DrugCurator(), new EvidenceLevelCurator());

        Association actionable = ViccTestFactory.testActionableAssociation("Treatment",
                cancerTypeA + ";" + cancerTypeB,
                "DOID:162",
                "A",
                "Responsive",
                "url");

        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithTestActionableHotspot();

        ViccEntry entry = ViccTestFactory.testEntryWithGeneEventAndAssociation("gene", "event", actionable);

        Set<EfficacyEvidence> evidences = factory.toActionableEvidence(entry, List.of(molecularCriterium));
        assertEquals(2, evidences.size());

        EfficacyEvidence evidenceA = findByCancerType(evidences, cancerTypeA);
        assertEquals("Treatment", evidenceA.treatment().name());
        assertEquals(cancerTypeA, evidenceA.indication().applicableType().name());
        assertEquals("1", evidenceA.indication().applicableType().doid());
        assertTrue(evidenceA.indication().excludedSubTypes().isEmpty());
        assertEquals(molecularCriterium, evidenceA.molecularCriterium());
        assertEquals(EvidenceLevel.A, evidenceA.evidenceLevel());
        assertEquals(EvidenceLevelDetails.UNKNOWN, evidenceA.evidenceLevelDetails());
        assertEquals(EvidenceDirection.RESPONSIVE, evidenceA.evidenceDirection());
        assertEquals(Sets.newHashSet("url"), evidenceA.urls());

        EfficacyEvidence evidenceB = findByCancerType(evidences, cancerTypeB);
        assertEquals("Treatment", evidenceB.treatment().name());
        assertEquals(cancerTypeB, evidenceB.indication().applicableType().name());
        assertEquals(CancerTypeConstants.CANCER_DOID, evidenceB.indication().applicableType().doid());
        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                CancerTypeConstants.BONE_MARROW_TYPE,
                CancerTypeConstants.LEUKEMIA_TYPE), evidenceB.indication().excludedSubTypes());
        assertEquals(molecularCriterium, evidenceB.molecularCriterium());
        assertEquals(EvidenceLevel.A, evidenceB.evidenceLevel());
        assertEquals(EvidenceLevelDetails.UNKNOWN, evidenceB.evidenceLevelDetails());
        assertEquals(EvidenceDirection.RESPONSIVE, evidenceB.evidenceDirection());
        assertEquals(Sets.newHashSet("url"), evidenceB.urls());

        factory.evaluateCuration();
    }

    @Test
    public void canReformatDrugs() {
        assertEquals("Imatinib,Imatinib", ActionableEvidenceFactory.reformatDrugLabels("IMATINIB,IMATINIB"));
        assertEquals("Fluorouracil,Irinotecan,Bevacizumab,Lysergide",
                ActionableEvidenceFactory.reformatDrugLabels("FLUOROURACIL,Irinotecan,BEVACIZUMAB,Lysergide"));

        assertNull(ActionableEvidenceFactory.reformatDrugLabels(null));
    }

    @Test
    public void canReformatField() {
        assertEquals("Field", ActionableEvidenceFactory.reformatField("Field"));
        assertEquals("Field", ActionableEvidenceFactory.reformatField("field"));
        assertEquals("Field", ActionableEvidenceFactory.reformatField("FIELD"));

        assertEquals("F", ActionableEvidenceFactory.reformatField("F"));
        assertEquals("F", ActionableEvidenceFactory.reformatField("f"));
        assertEquals("", ActionableEvidenceFactory.reformatField(""));
        assertNull(ActionableEvidenceFactory.reformatField(null));
    }

    @Test
    public void canResolveDirection() {
        assertEquals(EvidenceDirection.RESPONSIVE, ActionableEvidenceFactory.resolveDirection("Responsive"));
        assertEquals(EvidenceDirection.RESPONSIVE, ActionableEvidenceFactory.resolveDirection("Sensitive"));
        assertEquals(EvidenceDirection.RESISTANT, ActionableEvidenceFactory.resolveDirection("Resistant"));

        assertNull(ActionableEvidenceFactory.resolveDirection(null));
        assertNull(ActionableEvidenceFactory.resolveDirection("Conflicting"));
        assertNull(ActionableEvidenceFactory.resolveDirection("This is no direction"));
    }

    @Test
    public void canResolveLevel() {
        assertEquals(EvidenceLevel.A, ActionableEvidenceFactory.resolveLevel("A"));

        assertNull(ActionableEvidenceFactory.resolveLevel(null));
        assertNull(ActionableEvidenceFactory.resolveLevel("XXX"));
    }

    @Test
    public void canExtractDoid() {
        assertEquals("123", ActionableEvidenceFactory.extractDoid("DOID:123"));
        assertNull(ActionableEvidenceFactory.extractDoid("SNOMED:123"));
        assertNull(ActionableEvidenceFactory.extractDoid("DOID"));

        assertNull(ActionableEvidenceFactory.extractDoid(null));
    }

    @Test
    public void canFilterNonSupportiveEvidence() {
        ViccEntry actionable = ViccTestFactory.testEntryWithGeneEventAndAssociation("gene",
                "event",
                ViccTestFactory.testActionableAssociation("Treatment", "Cancer", "DOID:162", "A", "Responsive", "url"));

        Map<String, Set<String>> doidLookupMap = Maps.newHashMap();
        doidLookupMap.put("Cancer", Sets.newHashSet(CancerTypeConstants.CANCER_DOID));
        ActionableEvidenceFactory factory =
                new ActionableEvidenceFactory(DoidLookupTestFactory.create(doidLookupMap), new DrugCurator(), new EvidenceLevelCurator());

        List<MolecularCriterium> molecularCriteria = List.of(MolecularCriteriumTestFactory.createWithTestActionableHotspot());

        ViccEntry doesNotSupport = ImmutableViccEntry.builder()
                .from(actionable)
                .kbSpecificObject(ViccTestFactory.testEntryWithCivicEvidenceDirection("Does Not Support").kbSpecificObject())
                .build();

        assertEquals(0, factory.toActionableEvidence(doesNotSupport, molecularCriteria).size());

        ViccEntry supports = ImmutableViccEntry.builder()
                .from(actionable)
                .kbSpecificObject(ViccTestFactory.testEntryWithCivicEvidenceDirection("Supports").kbSpecificObject())
                .build();

        assertEquals(1, factory.toActionableEvidence(supports, molecularCriteria).size());

        ViccEntry undefined = ImmutableViccEntry.builder()
                .from(actionable)
                .kbSpecificObject(ViccTestFactory.testEntryWithCivicEvidenceDirection(null).kbSpecificObject())
                .build();

        assertEquals(1, factory.toActionableEvidence(undefined, molecularCriteria).size());

        ViccEntry notRecognized = ImmutableViccEntry.builder()
                .from(actionable)
                .kbSpecificObject(ViccTestFactory.testEntryWithCivicEvidenceDirection("Not a direction").kbSpecificObject())
                .build();

        assertEquals(1, factory.toActionableEvidence(notRecognized, molecularCriteria).size());
    }

    @NotNull
    private static EfficacyEvidence findByCancerType(@NotNull Iterable<EfficacyEvidence> evidences, @NotNull String cancerType) {
        for (EfficacyEvidence event : evidences) {
            if (event.indication().applicableType().name().equals(cancerType)) {
                return event;
            }
        }
        throw new IllegalStateException("Could not resolve evidence with cancer type: " + cancerType);
    }
}
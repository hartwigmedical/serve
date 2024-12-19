package com.hartwig.serve.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.DatamodelTestFactory;
import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.molecular.KnownEvent;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriteriumTestFactory;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.TrialTestFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ExtractionFunctionsTest {

    private static final Knowledgebase SOURCE_1 = Knowledgebase.VICC_CIVIC;
    private static final Knowledgebase SOURCE_2 = Knowledgebase.VICC_CGI;

    @Test
    public void canMergeProperExtractionResults() {
        ExtractionResult result1 = ExtractionResultTestFactory.createProperResultForSource(SOURCE_1);
        ExtractionResult result2 = ExtractionResultTestFactory.createProperResultForSource(SOURCE_2);

        ExtractionResult merged = ExtractionFunctions.merge(Lists.newArrayList(result1, result2));

        assertMergedKnownEvents(merged.knownEvents().hotspots());
        assertMergedKnownEvents(merged.knownEvents().codons());
        assertMergedKnownEvents(merged.knownEvents().exons());
        assertMergedKnownEvents(merged.knownEvents().genes());
        assertMergedKnownEvents(merged.knownEvents().copyNumbers());
        assertMergedKnownEvents(merged.knownEvents().fusions());

        assertEquals(2, merged.eventInterpretations().size());
        assertEquals(2, merged.evidences().size());
        assertEquals(2, merged.trials().size());
    }

    @Test
    public void shouldConsolidateTrialsFromSingleSourceOnNctId() {
        MolecularCriterium molecularCriterium1 = MolecularCriteriumTestFactory.createWithTestActionableCharacteristic();
        MolecularCriterium molecularCriterium2 = MolecularCriteriumTestFactory.createWithTestActionableGene();
        MolecularCriterium molecularCriterium3 = MolecularCriteriumTestFactory.createWithTestActionableHotspot();

        Indication indication1 = DatamodelTestFactory.createTestIndication("applicable 1", "excluded 1");
        Indication indication2 = DatamodelTestFactory.createTestIndication("applicable 2", "excluded 2");
        Indication indication3 = DatamodelTestFactory.createTestIndication("applicable 3", "excluded 3");

        ExtractionResult result1 = ImmutableExtractionResult.builder()
                .from(ExtractionResultTestFactory.createMinimalResultForSource(SOURCE_1))
                .trials(List.of(TrialTestFactory.builder()
                        .source(SOURCE_1)
                        .nctId("nct 1")
                        .addIndications(indication1)
                        .addAnyMolecularCriteria(molecularCriterium1)
                        .build()))
                .build();

        ExtractionResult result2 = ImmutableExtractionResult.builder()
                .from(ExtractionResultTestFactory.createMinimalResultForSource(SOURCE_1))
                .trials(List.of(TrialTestFactory.builder()
                        .source(SOURCE_1)
                        .nctId("nct 1")
                        .addIndications(indication2)
                        .addAnyMolecularCriteria(molecularCriterium2)
                        .build()))
                .build();

        ExtractionResult result3 = ImmutableExtractionResult.builder()
                .from(ExtractionResultTestFactory.createMinimalResultForSource(SOURCE_1))
                .trials(List.of(TrialTestFactory.builder()
                        .source(SOURCE_1)
                        .nctId("nct 2")
                        .addIndications(indication3)
                        .addAnyMolecularCriteria(molecularCriterium3)
                        .build()))
                .build();

        ExtractionResult consolidated = ExtractionFunctions.merge(List.of(result1, result2, result3));

        assertEquals(2, consolidated.trials().size());

        ActionableTrial trial1 = findByNctId(consolidated.trials(), "nct 1");
        assertEquals(2, trial1.indications().size());
        assertTrue(trial1.indications().contains(indication1));
        assertTrue(trial1.indications().contains(indication2));
        assertEquals(2, trial1.anyMolecularCriteria().size());
        assertTrue(trial1.anyMolecularCriteria().contains(molecularCriterium1));
        assertTrue(trial1.anyMolecularCriteria().contains(molecularCriterium2));

        ActionableTrial trial2 = findByNctId(consolidated.trials(), "nct 2");
        assertEquals(1, trial2.indications().size());
        assertTrue(trial2.indications().contains(indication3));
        assertEquals(1, trial2.anyMolecularCriteria().size());
        assertTrue(trial2.anyMolecularCriteria().contains(molecularCriterium3));
    }

    @Test (expected = IllegalStateException.class)
    public void shouldThrowInCaseOfDifferentCountriesWithSameNctId() {
        ExtractionResult result1 = ImmutableExtractionResult.builder()
                .from(ExtractionResultTestFactory.createMinimalResultForSource(SOURCE_1))
                .trials(List.of(TrialTestFactory.builder()
                        .source(SOURCE_1)
                        .nctId("nct 1")
                        .addCountries(TrialTestFactory.countryBuilder().name("country 1").build())
                        .build()))
                .build();

        ExtractionResult result2 = ImmutableExtractionResult.builder()
                .from(ExtractionResultTestFactory.createMinimalResultForSource(SOURCE_1))
                .trials(List.of(TrialTestFactory.builder()
                        .source(SOURCE_1)
                        .nctId("nct 1")
                        .addCountries(TrialTestFactory.countryBuilder().name("country 2").build())
                        .build()))
                .build();

        ExtractionFunctions.merge(List.of(result1, result2));
    }

    @Test
    public void retainsNullOnMinimalExtractionResults() {
        ExtractionResult result1 = ExtractionResultTestFactory.createMinimalResultForSource(SOURCE_1);
        ExtractionResult result2 = ExtractionResultTestFactory.createMinimalResultForSource(SOURCE_2);
        ExtractionResult merged = ExtractionFunctions.merge(Lists.newArrayList(result1, result2));

        assertNull(merged.knownEvents());
        assertNull(merged.evidences());
        assertNull(merged.trials());
    }

    private static void assertMergedKnownEvents(@NotNull Set<? extends KnownEvent> known) {
        assertEquals(1, known.size());
        KnownEvent first = known.iterator().next();
        assertTrue(first.sources().contains(SOURCE_1));
        assertTrue(first.sources().contains(SOURCE_2));
    }

    @NotNull
    private static ActionableTrial findByNctId(@NotNull List<ActionableTrial> trials, @NotNull String nctIdToFind) {
        return trials.stream().filter(trial -> trial.nctId().equals(nctIdToFind)).findFirst().orElseThrow();
    }
}
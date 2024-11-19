package com.hartwig.serve.datamodel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidenceTestFactory;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.molecular.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.molecular.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.molecular.range.RangeTestFactory;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.Country;
import com.hartwig.serve.datamodel.trial.TrialTestFactory;

import org.jetbrains.annotations.NotNull;

public final class TestServeDatabaseFactory {

    private TestServeDatabaseFactory() {
    }

    private static final Knowledgebase TEST_SOURCE = Knowledgebase.UNKNOWN;

    @NotNull
    public static ServeDatabase createEmptyDatabase() {
        Map<RefGenome, ServeRecord> records = Map.of(RefGenome.V37, createEmptyServeRecord());
        return ImmutableServeDatabase.builder().version("empty").records(records).build();
    }

    @NotNull
    public static ServeDatabase createMinimalDatabase() {
        Map<RefGenome, ServeRecord> records = Map.of(RefGenome.V37, createMinimalServeRecord());
        return ImmutableServeDatabase.builder().version("minimal").records(records).build();
    }

    @NotNull
    public static ServeDatabase createExhaustiveDatabase() {
        Map<RefGenome, ServeRecord> records =
                Map.of(RefGenome.V37, createExhaustiveServeRecord(), RefGenome.V38, createExhaustiveServeRecord());
        return ImmutableServeDatabase.builder().version("exhaustive").records(records).build();
    }

    @NotNull
    private static ServeRecord createEmptyServeRecord() {
        return ImmutableServeRecord.builder().knownEvents(ImmutableKnownEvents.builder().build()).build();
    }

    @NotNull
    private static ServeRecord createMinimalServeRecord() {
        return ImmutableServeRecord.builder()
                .knownEvents(createMinimalKnownEvents())
                .evidences(createMinimalEfficacyEvidences())
                .trials(createMinimalActionableTrials())
                .build();
    }

    @NotNull
    private static KnownEvents createMinimalKnownEvents() {
        return ImmutableKnownEvents.builder()
                .addHotspots(HotspotTestFactory.createTestKnownHotspotForSource(TEST_SOURCE))
                .addCodons(RangeTestFactory.createTestKnownCodonForSource(TEST_SOURCE))
                .addExons(RangeTestFactory.createTestKnownExonForSource(TEST_SOURCE))
                .addGenes(GeneTestFactory.createTestKnownGeneForSource(TEST_SOURCE))
                .addCopyNumbers(GeneTestFactory.createTestKnownCopyNumberForSource(TEST_SOURCE))
                .addFusions(FusionTestFactory.createTestKnownFusionForSource(TEST_SOURCE))
                .build();
    }

    @NotNull
    private static List<EfficacyEvidence> createMinimalEfficacyEvidences() {
        return List.of(EfficacyEvidenceTestFactory.builder().build());
    }

    @NotNull
    private static List<ActionableTrial> createMinimalActionableTrials() {
        return List.of(TrialTestFactory.builder().build());
    }

    @NotNull
    private static ServeRecord createExhaustiveServeRecord() {
        return ImmutableServeRecord.builder()
                .knownEvents(createMinimalKnownEvents())
                .evidences(createMinimalEfficacyEvidences())
                .trials(createExhaustiveTrials())
                .build();
    }

    @NotNull
    private static List<ActionableTrial> createExhaustiveTrials() {
        Country country1 = TrialTestFactory.countryBuilder()
                .name("country 1")
                .putHospitalsPerCity("city 1",
                        Set.of(TrialTestFactory.createTestHospital("hospital 1"), TrialTestFactory.createTestHospital("hospital 2")))
                .build();

        Country country2 = TrialTestFactory.countryBuilder()
                .name("country 2")
                .putHospitalsPerCity("city 1",
                        Set.of(TrialTestFactory.createTestHospital("hospital 1"), TrialTestFactory.createTestHospital("hospital 2")))
                .build();

        return List.of(TrialTestFactory.builder().addCountries(country2, country1).build());
    }
}

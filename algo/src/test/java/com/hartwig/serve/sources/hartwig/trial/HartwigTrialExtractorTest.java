package com.hartwig.serve.sources.hartwig.trial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ImmutableCancerType;
import com.hartwig.serve.datamodel.common.ImmutableIndication;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristicType;
import com.hartwig.serve.datamodel.molecular.gene.GeneEvent;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableVariantAnnotation;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.GenderCriterium;
import com.hartwig.serve.datamodel.trial.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.trial.ImmutableCountry;
import com.hartwig.serve.datamodel.trial.Phase;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class HartwigTrialExtractorTest {

    private final HartwigTrialExtractor extractor =
            HartwigTrialExtractorFactory.createCkbEmulatedExtractor(RefGenomeResourceTestFactory.buildTestResource37());

    @Test
    public void canExtractZeroEntries() {
        ExtractionResult result = extractor.extract(Lists.newArrayList());
        assertEquals(0, Objects.requireNonNull(result.trials()).size());
        assertNull(result.knownEvents());
        assertNull(result.evidences());
    }

    @Test
    public void canExtractRealisticEntrySet() {
        List<HartwigTrialEntry> entries = createRealisticInputSet();

        ExtractionResult result = extractor.extract(entries);
        List<ActionableTrial> actualTrials = result.trials();
        assertNotNull(actualTrials);

        List<ActionableTrial> expectedTrials = createExpectedOutput();
        assertTrue(actualTrials.containsAll(expectedTrials));
        assertEquals(expectedTrials.size(), actualTrials.size());
    }

    @NotNull
    private static List<HartwigTrialEntry> createRealisticInputSet() {
        List<HartwigTrialEntry> entries = Lists.newArrayList();

        ImmutableHartwigTrialEntry.Builder trial1Builder = ImmutableHartwigTrialEntry.builder();
        trial1Builder.date(LocalDate.of(2025, 2, 21));
        trial1Builder.nctId("NCT-01");
        trial1Builder.title("This is trial 1");
        trial1Builder.acronym("TRIAL-01");
        trial1Builder.country("Netherlands");
        trial1Builder.genderCriterium(GenderCriterium.MALE);
        trial1Builder.cancerType("Cancer 1");
        trial1Builder.cancerTypeDoid("1");
        trial1Builder.url("https://url.com");

        entries.add(trial1Builder.actionableGene("EGFR").actionableEvent("amp").build());
        entries.add(trial1Builder.actionableGene("-").actionableEvent("MSI high").build());

        ImmutableHartwigTrialEntry.Builder trial2Builder = ImmutableHartwigTrialEntry.builder();
        trial2Builder.date(LocalDate.of(2025, 4, 18));
        trial2Builder.nctId("NCT-02");
        trial2Builder.title("This is trial 2");
        trial2Builder.acronym(null);
        trial2Builder.country("Germany");
        trial2Builder.genderCriterium(null);
        trial2Builder.actionableGene("BRAF");
        trial2Builder.actionableEvent("V600E");
        trial2Builder.url("https://url.com");

        entries.add(trial2Builder.cancerType("Cancer 1").cancerTypeDoid("1").build());
        entries.add(trial2Builder.cancerType("Cancer 2").cancerTypeDoid("2").build());
        entries.add(trial2Builder.cancerType("Cancer 3").cancerTypeDoid("3").build());

        return entries;
    }

    @NotNull
    private static List<ActionableTrial> createExpectedOutput() {
        MolecularCriterium expectedTrial1Criterium1 = ImmutableMolecularCriterium.builder()
                .genes(Set.of(ImmutableActionableGene.builder()
                        .sourceDate(LocalDate.of(2025, 2, 21))
                        .sourceEvent("EGFR amp")
                        .sourceUrls(Set.of("https://url.com"))
                        .gene("EGFR")
                        .event(GeneEvent.AMPLIFICATION)
                        .build()))
                .build();

        MolecularCriterium expectedTrial1Criterium2 = ImmutableMolecularCriterium.builder()
                .characteristics(Set.of(ImmutableActionableCharacteristic.builder()
                        .sourceDate(LocalDate.of(2025, 2, 21))
                        .sourceEvent("MSI high")
                        .sourceUrls(Set.of("https://url.com"))
                        .type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE)
                        .build()))
                .build();

        MolecularCriterium expectedTrial2Criterium1 = ImmutableMolecularCriterium.builder()
                .hotspots(Set.of(ImmutableActionableHotspot.builder()
                        .sourceDate(LocalDate.of(2025, 4, 18))
                        .sourceEvent("BRAF V600E")
                        .sourceUrls(Set.of("https://url.com"))
                        .variants(Set.of(ImmutableVariantAnnotation.builder()
                                .gene("BRAF")
                                .chromosome("1")
                                .position(10)
                                .ref("A")
                                .alt("T")
                                .build()))
                        .build()))
                .build();

        ActionableTrial expectedTrial1 = ImmutableActionableTrial.builder()
                .source(Knowledgebase.HARTWIG_TRIAL_CURATED)
                .nctId("NCT-01")
                .title("This is trial 1")
                .acronym("TRIAL-01")
                .phase(Phase.UNKNOWN)
                .countries(Set.of(ImmutableCountry.builder().name("Netherlands").build()))
                .therapyNames(Sets.newHashSet())
                .genderCriterium(GenderCriterium.MALE)
                .indications(Set.of(ImmutableIndication.builder()
                        .applicableType(ImmutableCancerType.builder().name("Cancer 1").doid("1").build())
                        .build()))
                .anyMolecularCriteria(Set.of(expectedTrial1Criterium1, expectedTrial1Criterium2))
                .urls(Set.of("https://url.com"))
                .build();

        ActionableTrial expectedTrial2 = ImmutableActionableTrial.builder()
                .source(Knowledgebase.HARTWIG_TRIAL_CURATED)
                .nctId("NCT-02")
                .title("This is trial 2")
                .phase(Phase.UNKNOWN)
                .countries(Set.of(ImmutableCountry.builder().name("Germany").build()))
                .therapyNames(Sets.newHashSet())
                .indications(Set.of(ImmutableIndication.builder()
                                .applicableType(ImmutableCancerType.builder().name("Cancer 1").doid("1").build())
                                .build(),
                        ImmutableIndication.builder()
                                .applicableType(ImmutableCancerType.builder().name("Cancer 2").doid("2").build())
                                .build(),
                        ImmutableIndication.builder()
                                .applicableType(ImmutableCancerType.builder().name("Cancer 3").doid("3").build())
                                .build()))
                .anyMolecularCriteria(Set.of(expectedTrial2Criterium1))
                .urls(Set.of("https://url.com"))
                .build();

        return List.of(expectedTrial1, expectedTrial2);
    }
}
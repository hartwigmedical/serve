package com.hartwig.serve.refgenome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.ActionableTrial;
import com.hartwig.serve.datamodel.ActionableTrialTestFactory;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.EfficacyEvidenceTestFactory;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.MolecularCriterium;
import com.hartwig.serve.datamodel.MolecularCriteriumTestFactory;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.refgenome.liftover.ImmutableLiftOverResult;
import com.hartwig.serve.refgenome.liftover.LiftOverAlgo;
import com.hartwig.serve.refgenome.liftover.LiftOverResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class RefGenomeConverterTest {

    private static final RefGenomeConverter DUMMY_CONVERTER = build37To38DummyConverter();
    private static final RefGenomeConverter NULL_CONVERTER = build37To38NullConverter();

    private static final String TEST_GENE = "BRAF";
    private static final String TEST_CHROMOSOME = "chr1";

    @Test
    public void canHandleMissingInputs() {
        assertNull(NULL_CONVERTER.convertKnownEvents(null));
        assertNull(NULL_CONVERTER.convertEfficacyEvidences(null));
        assertNull(NULL_CONVERTER.convertTrials(null));
    }

    @Test
    public void canConvertKnownHotspots() {
        KnownHotspot hotspot = HotspotTestFactory.knownHotspotBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .position(1)
                .ref("G")
                .alt("T")
                .addSources(Knowledgebase.HARTWIG_HOTSPOT_CURATED)
                .build();

        KnownEvents knownEvents = ImmutableKnownEvents.builder().addHotspots(hotspot).build();
        Set<KnownHotspot> convertedHotspots = DUMMY_CONVERTER.convertKnownEvents(knownEvents).hotspots();
        assertEquals(hotspot, convertedHotspots.iterator().next());

        assertTrue(NULL_CONVERTER.convertKnownEvents(knownEvents).hotspots().isEmpty());
    }

    @Test
    public void canConvertKnownCodons() {
        KnownCodon codon = RangeTestFactory.knownCodonBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .start(1)
                .end(3)
                .addSources(Knowledgebase.HARTWIG_HOTSPOT_CURATED)
                .build();

        KnownEvents knownEvents = ImmutableKnownEvents.builder().addCodons(codon).build();
        Set<KnownCodon> convertedCodons = DUMMY_CONVERTER.convertKnownEvents(knownEvents).codons();
        assertEquals(codon, convertedCodons.iterator().next());

        assertTrue(NULL_CONVERTER.convertKnownEvents(knownEvents).codons().isEmpty());
    }

    @Test
    public void canConvertKnownExons() {
        KnownExon exon = RangeTestFactory.knownExonBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .start(1)
                .end(7)
                .addSources(Knowledgebase.HARTWIG_HOTSPOT_CURATED)
                .build();

        KnownEvents knownEvents = ImmutableKnownEvents.builder().addExons(exon).build();
        Set<KnownExon> convertedExons = DUMMY_CONVERTER.convertKnownEvents(knownEvents).exons();
        assertEquals(exon, convertedExons.iterator().next());

        assertTrue(NULL_CONVERTER.convertKnownEvents(knownEvents).exons().isEmpty());
    }

    @Test
    public void canConvertEvidenceAndTrialsForHotspots() {
        ActionableHotspot actionableHotspot = HotspotTestFactory.actionableHotspotBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .position(1)
                .ref("G")
                .alt("C")
                .build();

        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithActionableHotspot(actionableHotspot);

        List<EfficacyEvidence> evidences = List.of(EfficacyEvidenceTestFactory.createWithMolecularCriterium(molecularCriterium));
        Set<ActionableHotspot> convertedActionableHotspotsFromEvidence =
                DUMMY_CONVERTER.convertEfficacyEvidences(evidences).iterator().next().molecularCriterium().hotspots();
        assertEquals(actionableHotspot, convertedActionableHotspotsFromEvidence.iterator().next());

        List<ActionableTrial> trials = List.of(ActionableTrialTestFactory.createWithMolecularCriterium(molecularCriterium));
        Set<ActionableHotspot> convertedActionableHotspotFromTrial =
                DUMMY_CONVERTER.convertTrials(trials).iterator().next().anyMolecularCriteria().iterator().next().hotspots();
        assertEquals(actionableHotspot, convertedActionableHotspotFromTrial.iterator().next());
    }

    @Test
    public void canConvertEvidenceAndTrialsForCodons() {
        ActionableRange actionableCodon =
                RangeTestFactory.actionableRangeBuilder().gene(TEST_GENE).chromosome(TEST_CHROMOSOME).start(3).end(4).build();

        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithActionableCodon(actionableCodon);

        List<EfficacyEvidence> evidences = List.of(EfficacyEvidenceTestFactory.createWithMolecularCriterium(molecularCriterium));
        Set<ActionableRange> convertedActionableCodonFromEvidence =
                DUMMY_CONVERTER.convertEfficacyEvidences(evidences).iterator().next().molecularCriterium().codons();
        assertEquals(actionableCodon, convertedActionableCodonFromEvidence.iterator().next());

        List<ActionableTrial> trials = List.of(ActionableTrialTestFactory.createWithMolecularCriterium(molecularCriterium));
        Set<ActionableRange> convertedActionableCodonFromTrial =
                DUMMY_CONVERTER.convertTrials(trials).iterator().next().anyMolecularCriteria().iterator().next().codons();
        assertEquals(actionableCodon, convertedActionableCodonFromTrial.iterator().next());
    }

    @Test
    public void canConvertEvidenceAndTrialsForExons() {
        ActionableRange actionableExon =
                RangeTestFactory.actionableRangeBuilder().gene(TEST_GENE).chromosome(TEST_CHROMOSOME).start(3).end(4).build();

        MolecularCriterium molecularCriterium = MolecularCriteriumTestFactory.createWithActionableExons(actionableExon);

        List<EfficacyEvidence> evidences = List.of(EfficacyEvidenceTestFactory.createWithMolecularCriterium(molecularCriterium));
        Set<ActionableRange> convertedActionableExonFromEvidence =
                DUMMY_CONVERTER.convertEfficacyEvidences(evidences).iterator().next().molecularCriterium().exons();
        assertEquals(actionableExon, convertedActionableExonFromEvidence.iterator().next());

        List<ActionableTrial> trials = List.of(ActionableTrialTestFactory.createWithMolecularCriterium(molecularCriterium));
        Set<ActionableRange> convertedActionableExonFromTrial =
                DUMMY_CONVERTER.convertTrials(trials).iterator().next().anyMolecularCriteria().iterator().next().exons();
        assertEquals(actionableExon, convertedActionableExonFromTrial.iterator().next());
    }

    @NotNull
    private static RefGenomeConverter build37To38DummyConverter() {
        return build37To38ConverterWithLiftOverAlgo(new DummyLiftOver());
    }

    @NotNull
    private static RefGenomeConverter build37To38NullConverter() {
        return build37To38ConverterWithLiftOverAlgo(new NullLiftOver());
    }

    @NotNull
    private static RefGenomeConverter build37To38ConverterWithLiftOverAlgo(@NotNull LiftOverAlgo algo) {
        return new RefGenomeConverter(RefGenome.V37, RefGenome.V38, RefGenomeResourceTestFactory.loadTestRefSequence38(), algo);
    }

    private static class DummyLiftOver implements LiftOverAlgo {

        @Nullable
        @Override
        public LiftOverResult liftOver(@NotNull final String chromosome, final int position) {
            return ImmutableLiftOverResult.builder().chromosome(chromosome).position(position).build();
        }
    }

    private static class NullLiftOver implements LiftOverAlgo {

        @Nullable
        @Override
        public LiftOverResult liftOver(@NotNull final String chromosome, final int position) {
            return null;
        }
    }
}
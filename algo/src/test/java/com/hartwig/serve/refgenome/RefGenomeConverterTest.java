package com.hartwig.serve.refgenome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
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
    public void canConvertKnownHotspots() {
        KnownHotspot hotspot = HotspotTestFactory.knownHotspotBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .position(1)
                .ref("G")
                .alt("T")
                .addSources(Knowledgebase.HARTWIG_HOTSPOT_CURATED)
                .build();

        Set<KnownHotspot> convertedHotspots = DUMMY_CONVERTER.convertKnownHotspots(Sets.newHashSet(hotspot));
        assertEquals(hotspot, convertedHotspots.iterator().next());

        assertTrue(NULL_CONVERTER.convertKnownHotspots(Sets.newHashSet(hotspot)).isEmpty());
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

        Set<KnownCodon> convertedCodons = DUMMY_CONVERTER.convertKnownCodons(Sets.newHashSet(codon));
        assertEquals(codon, convertedCodons.iterator().next());

        assertTrue(NULL_CONVERTER.convertKnownCodons(Sets.newHashSet(codon)).isEmpty());
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

        Set<KnownExon> convertedExons = DUMMY_CONVERTER.convertKnownExons(Sets.newHashSet(exon));
        assertEquals(exon, convertedExons.iterator().next());

        assertTrue(NULL_CONVERTER.convertKnownExons(Sets.newHashSet(exon)).isEmpty());
    }

    @Test
    public void canConvertActionableHotspots() {
        ActionableHotspot actionableHotspot = HotspotTestFactory.actionableHotspotBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .position(1)
                .ref("G")
                .alt("C")
                .source(Knowledgebase.HARTWIG_HOTSPOT_CURATED)
                .build();

        Set<ActionableHotspot> convertedActionableHotspots = DUMMY_CONVERTER.convertActionableHotspots(Sets.newHashSet(actionableHotspot));
        assertEquals(actionableHotspot, convertedActionableHotspots.iterator().next());
    }

    @Test
    public void canConvertActionableCodon() {
        ActionableRange actionableCodon = RangeTestFactory.actionableRangeBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .start(3)
                .end(4)
                .source(Knowledgebase.HARTWIG_HOTSPOT_CURATED)
                .build();

        Set<ActionableRange> convertedActionableCodon = DUMMY_CONVERTER.convertActionableRanges(Sets.newHashSet(actionableCodon));
        assertEquals(actionableCodon, convertedActionableCodon.iterator().next());
    }

    @Test
    public void canConvertActionableExons() {
        ActionableRange actionableExon = RangeTestFactory.actionableRangeBuilder()
                .gene(TEST_GENE)
                .chromosome(TEST_CHROMOSOME)
                .start(3)
                .end(4)
                .source(Knowledgebase.HARTWIG_HOTSPOT_CURATED)
                .build();

        Set<ActionableRange> convertedActionableExon = DUMMY_CONVERTER.convertActionableRanges(Sets.newHashSet(actionableExon));
        assertEquals(actionableExon, convertedActionableExon.iterator().next());
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
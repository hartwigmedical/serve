package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.MutationType;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.molecular.gene.GeneEvent;
import com.hartwig.serve.datamodel.molecular.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.molecular.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.molecular.range.RangeTestFactory;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.exon.ImmutableExonAnnotation;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbKnownEventsExtractorTest {

    @Test
    public void canFilterUnknownProteinEffectFromKnownEvents() {
        List<ExtractedEvent> extractedEvents = Lists.newArrayList();
        extractedEvents.add(createHotspot("BRAF", "V600E", "gain of function", null));
        extractedEvents.add(createHotspot("TP53", "R175H", "unknown", null));
        extractedEvents.add(createCodon("BRAF", "V600", "gain of function", null));
        extractedEvents.add(createCodon("TP53", "R175", "unknown", null));
        extractedEvents.add(createExon("BRAF", "exon 1 deletion", "loss of function", null));
        extractedEvents.add(createExon("APC", "exon 3 deletion", "unknown", null));
        extractedEvents.add(createAmplification("MET", "gain of function", null));
        extractedEvents.add(createAmplification("KIT", "unknown", null));
        extractedEvents.add(createFusion("ALK", "EML4", "gain of function", null));
        extractedEvents.add(createFusion("APC", "BRAF", "unknown", null));

        KnownEvents knownEvents = CkbKnownEventsExtractor.generateKnownEvents(extractedEvents);

        assertEquals(1, knownEvents.hotspots().size());
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, knownEvents.hotspots().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.codons().size());
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, knownEvents.codons().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.exons().size());
        assertEquals(ProteinEffect.LOSS_OF_FUNCTION, knownEvents.exons().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.copyNumbers().size());
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, knownEvents.copyNumbers().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.fusions().size());
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, knownEvents.fusions().iterator().next().proteinEffect());
    }

    @Test
    public void dontFilterAssociatedWithDrugResistanceFromKnownEvents() {
        List<ExtractedEvent> extractedEvents = Lists.newArrayList();
        extractedEvents.add(createHotspot("TP53", "R175H", "unknown", "Y"));
        extractedEvents.add(createCodon("TP53", "R175", "unknown", "Y"));
        extractedEvents.add(createExon("APC", "exon 3 deletion", "unknown", "Y"));
        extractedEvents.add(createAmplification("KIT", "unknown", "Y"));
        extractedEvents.add(createFusion("APC", "BRAF", "unknown", "Y"));

        KnownEvents knownEvents = CkbKnownEventsExtractor.generateKnownEvents(extractedEvents);

        assertEquals(1, knownEvents.hotspots().size());
        assertEquals(ProteinEffect.UNKNOWN, knownEvents.hotspots().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.codons().size());
        assertEquals(ProteinEffect.UNKNOWN, knownEvents.codons().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.exons().size());
        assertEquals(ProteinEffect.UNKNOWN, knownEvents.exons().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.copyNumbers().size());
        assertEquals(ProteinEffect.UNKNOWN, knownEvents.copyNumbers().iterator().next().proteinEffect());

        assertEquals(1, knownEvents.fusions().size());
        assertEquals(ProteinEffect.UNKNOWN, knownEvents.fusions().iterator().next().proteinEffect());
    }

    @NotNull
    private static ExtractedEvent createCodon(@NotNull String gene, @NotNull String codon, @Nullable String proteinEffect,
            @Nullable String associatedWithDrugResistance) {
        return ImmutableExtractedEvent.builder()
                .gene(gene)
                .event(codon)
                .variant(CkbTestFactory.createVariant(gene, codon, gene + " " + codon, proteinEffect, associatedWithDrugResistance))
                .eventType(EventType.CODON)
                .eventExtractorOutput(ImmutableEventExtractorOutput.builder()
                        .addCodons(ImmutableCodonAnnotation.builder()
                                .from(RangeTestFactory.createRangeAnnotation(gene, "1", 1, 1, MutationType.INFRAME_DELETION))
                                .inputTranscript("transcript")
                                .inputCodonRank(1)
                                .build())
                        .build())
                .build();
    }

    @NotNull
    private static ExtractedEvent createExon(@NotNull String gene, @NotNull String event, @Nullable String proteinEffect,
            @Nullable String associatedWithDrugResistance) {
        return ImmutableExtractedEvent.builder()
                .gene(gene)
                .event(event)
                .variant(CkbTestFactory.createVariant(gene, event, gene + " " + event, proteinEffect, associatedWithDrugResistance))
                .eventType(EventType.EXON)
                .eventExtractorOutput(ImmutableEventExtractorOutput.builder()
                        .addExons(ImmutableExonAnnotation.builder()
                                .from(RangeTestFactory.createRangeAnnotation(gene, "1", 1, 1, MutationType.INFRAME_DELETION))
                                .inputTranscript("transcript")
                                .inputExonRank(1)
                                .build())
                        .build())
                .build();
    }

    @NotNull
    private static ExtractedEvent createFusion(@NotNull String geneUp, @NotNull String geneDown, @Nullable String proteinEffect,
            @Nullable String associatedWithDrugResistance) {
        return ImmutableExtractedEvent.builder()
                .gene(geneUp)
                .event(geneDown + "-" + geneUp)
                .variant(CkbTestFactory.createVariant(geneUp,
                        geneDown + "-" + geneUp,
                        geneDown + "-" + geneUp + " fusion",
                        proteinEffect,
                        associatedWithDrugResistance))
                .eventType(EventType.FUSION_PAIR)
                .eventExtractorOutput(ImmutableEventExtractorOutput.builder()
                        .fusionPair(FusionTestFactory.createFusionPair(geneUp, geneDown, 1, 1))
                        .build())
                .build();
    }

    @NotNull
    private static ExtractedEvent createHotspot(@NotNull String gene, @NotNull String mutation, @Nullable String proteinEffect,
            @Nullable String associatedWithDrugResistance) {
        return ImmutableExtractedEvent.builder()
                .gene(gene)
                .event(gene + " " + mutation)
                .variant(CkbTestFactory.createVariant(gene, mutation, gene + " " + mutation, proteinEffect, associatedWithDrugResistance))
                .eventType(EventType.VARIANT)
                .eventExtractorOutput(ImmutableEventExtractorOutput.builder()
                        .addVariants(HotspotTestFactory.createVariantAnnotation(gene, "1", 1, "T", "A"))
                        .build())
                .build();
    }

    @NotNull
    private static ExtractedEvent createAmplification(@NotNull String gene, @Nullable String proteinEffect,
            @Nullable String associatedWithDrugResistance) {
        return ImmutableExtractedEvent.builder()
                .gene(gene)
                .event(gene + " amp")
                .variant(CkbTestFactory.createVariant(gene, "amp", gene + " amp", proteinEffect, associatedWithDrugResistance))
                .eventType(EventType.AMPLIFICATION)
                .eventExtractorOutput(ImmutableEventExtractorOutput.builder()
                        .copyNumber(GeneTestFactory.createGeneAnnotation(gene, GeneEvent.AMPLIFICATION))
                        .build())
                .build();
    }
}
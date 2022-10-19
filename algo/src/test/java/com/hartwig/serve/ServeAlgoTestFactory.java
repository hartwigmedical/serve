package com.hartwig.serve;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MutationTypeFilter;
import com.hartwig.serve.datamodel.ServeTestFactory;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableKnownCodon;
import com.hartwig.serve.extraction.codon.KnownCodon;
import com.hartwig.serve.extraction.copynumber.CopyNumberType;
import com.hartwig.serve.extraction.copynumber.ImmutableKnownCopyNumber;
import com.hartwig.serve.extraction.copynumber.KnownCopyNumber;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.exon.ImmutableExonAnnotation;
import com.hartwig.serve.extraction.exon.ImmutableKnownExon;
import com.hartwig.serve.extraction.exon.KnownExon;
import com.hartwig.serve.extraction.fusion.ImmutableKnownFusionPair;
import com.hartwig.serve.extraction.fusion.KnownFusionPair;
import com.hartwig.serve.extraction.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.extraction.hotspot.KnownHotspot;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ServeAlgoTestFactory {

    private ServeAlgoTestFactory() {
    }

    @NotNull
    public static ExtractionResult createResultForSource(@NotNull Knowledgebase source) {
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(source.refGenomeVersion())
                .addKnownHotspots(createTestKnownHotspotForSource(source))
                .addKnownCodons(createTestKnownCodonForSource(source))
                .addKnownExons(createTestKnownExonForSource(source))
                .addKnownCopyNumbers(createTestKnownCopyNumberForSource(source))
                .addKnownFusionPairs(createTestKnownFusionPairForSource(source))
                .addActionableHotspots(ServeTestFactory.createTestActionableHotspotForSource(source))
                .addActionableRanges(ServeTestFactory.createTestActionableRangeForSource(source))
                .addActionableGenes(ServeTestFactory.createTestActionableGeneForSource(source))
                .addActionableFusions(ServeTestFactory.createTestActionableFusionForSource(source))
                .addActionableCharacteristics(ServeTestFactory.createTestActionableCharacteristicForSource(source))
                .addActionableHLA(ServeTestFactory.createTestActionableImmunoHLAForSource(source))
                .build();
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspotForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownHotspot.builder().from(createTestKnownHotspot()).addSources(source).build();
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspot() {
        return ImmutableKnownHotspot.builder()
                .gene(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .proteinAnnotation(Strings.EMPTY)
                .build();
    }

    @NotNull
    public static KnownCodon createTestKnownCodonForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownCodon.builder().from(createTestKnownCodon()).addSources(source).build();
    }

    @NotNull
    public static KnownCodon createTestKnownCodon() {
        return ImmutableKnownCodon.builder()
                .annotation(ImmutableCodonAnnotation.builder().from(createTestCodonAnnotation()).build())
                .build();
    }

    @NotNull
    public static CodonAnnotation createTestCodonAnnotation() {
        return ImmutableCodonAnnotation.builder()
                .gene(Strings.EMPTY)
                .transcript(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .mutationType(MutationTypeFilter.ANY)
                .rank(0)
                .build();
    }

    @NotNull
    public static KnownExon createTestKnownExonForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownExon.builder().from(createTestKnownExon()).addSources(source).build();
    }

    @NotNull
    public static KnownExon createTestKnownExon() {
        return ImmutableKnownExon.builder().annotation(ImmutableExonAnnotation.builder().from(createTestExonAnnotation()).build()).build();
    }

    @NotNull
    public static ExonAnnotation createTestExonAnnotation() {
        return ImmutableExonAnnotation.builder()
                .gene(Strings.EMPTY)
                .transcript(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .mutationType(MutationTypeFilter.ANY)
                .rank(0)
                .build();
    }

    @NotNull
    public static KnownCopyNumber createTestKnownCopyNumberForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownCopyNumber.builder().from(createTestKnownCopyNumber()).addSources(source).build();
    }

    @NotNull
    public static KnownCopyNumber createTestKnownCopyNumber() {
        return ImmutableKnownCopyNumber.builder().gene(Strings.EMPTY).type(CopyNumberType.AMPLIFICATION).build();
    }

    @NotNull
    public static KnownFusionPair createTestKnownFusionPairForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownFusionPair.builder().from(createTestKnownFusionPair()).addSources(source).build();
    }

    @NotNull
    public static KnownFusionPair createTestKnownFusionPair() {
        return ImmutableKnownFusionPair.builder().geneUp(Strings.EMPTY).geneDown(Strings.EMPTY).build();
    }
}
package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class RangeTestFactory {

    private RangeTestFactory() {
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
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .transcript(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .applicableMutationType(MutationType.ANY)
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
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .transcript(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .applicableMutationType(MutationType.ANY)
                .rank(0)
                .build();
    }

    @NotNull
    public static ActionableRange createTestActionableRangeForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableRange.builder().from(createTestActionableRange()).source(source).build();
    }

    @NotNull
    public static ActionableRange createTestActionableRange() {
        return ImmutableActionableRange.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .gene(Strings.EMPTY)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .transcript(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .applicableMutationType(MutationType.ANY)
                .rangeType(RangeType.EXON)
                .rank(0)
                .build();
    }
}

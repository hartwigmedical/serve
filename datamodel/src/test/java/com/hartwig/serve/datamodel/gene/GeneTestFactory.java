package com.hartwig.serve.datamodel.gene;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.CommonTestFactory;

import org.jetbrains.annotations.NotNull;

public final class GeneTestFactory {

    private GeneTestFactory() {
    }

    @NotNull
    public static ImmutableKnownCopyNumber.Builder knownCopyNumberBuilder() {
        return ImmutableKnownCopyNumber.builder()
                .from(CommonTestFactory.createEmptyGeneAlteration())
                .type(CopyNumberType.AMPLIFICATION);
    }

    @NotNull
    public static KnownCopyNumber createTestKnownCopyNumberForSource(@NotNull Knowledgebase source) {
        return knownCopyNumberBuilder().addSources(source).build();
    }

    @NotNull
    public static ImmutableActionableGene.Builder actionableGeneBuilder() {
        return ImmutableActionableGene.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .from(CommonTestFactory.createEmptyGeneAlteration())
                .event(GeneLevelEvent.ANY_MUTATION);
    }

    @NotNull
    public static ActionableGene createTestActionableGeneForSource(@NotNull Knowledgebase source) {
        return actionableGeneBuilder().source(source).build();
    }

    @NotNull
    public static ActionableGene createTestActionableGene() {
        return actionableGeneBuilder().build();
    }
}

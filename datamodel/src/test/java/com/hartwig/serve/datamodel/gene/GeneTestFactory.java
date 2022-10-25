package com.hartwig.serve.datamodel.gene;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.CommonTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class GeneTestFactory {

    private GeneTestFactory() {
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

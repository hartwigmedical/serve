package com.hartwig.serve.datamodel.fusion;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class FusionTestFactory {

    private FusionTestFactory() {
    }

    @NotNull
    public static ImmutableKnownFusionPair.Builder knownFusionBuilder() {
        return ImmutableKnownFusionPair.builder().geneUp(Strings.EMPTY).geneDown(Strings.EMPTY).proteinEffect(ProteinEffect.UNKNOWN);
    }

    @NotNull
    public static KnownFusionPair createTestKnownFusionPairForSource(@NotNull Knowledgebase source) {
        return knownFusionBuilder().addSources(source).build();
    }

    @NotNull
    public static ImmutableActionableFusion.Builder actionableFusionBuilder() {
        return ImmutableActionableFusion.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .geneUp(Strings.EMPTY)
                .geneDown(Strings.EMPTY)
                .proteinEffect(ProteinEffect.UNKNOWN);
    }

    @NotNull
    public static ActionableFusion createTestActionableFusionForSource(@NotNull Knowledgebase source) {
        return actionableFusionBuilder().source(source).build();
    }
}

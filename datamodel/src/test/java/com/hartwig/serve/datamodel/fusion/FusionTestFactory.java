package com.hartwig.serve.datamodel.fusion;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class FusionTestFactory {

    private FusionTestFactory() {
    }

    @NotNull
    public static KnownFusionPair createTestKnownFusionPairForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownFusionPair.builder().from(createTestKnownFusionPair()).addSources(source).build();
    }

    @NotNull
    public static KnownFusionPair createTestKnownFusionPair() {
        return ImmutableKnownFusionPair.builder().geneUp(Strings.EMPTY).geneDown(Strings.EMPTY).build();
    }

    @NotNull
    public static ActionableFusion createTestActionableFusionForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableFusion.builder().from(createTestActionableFusion()).source(source).build();
    }

    @NotNull
    public static ActionableFusion createTestActionableFusion() {
        return ImmutableActionableFusion.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .geneUp(Strings.EMPTY)
                .geneDown(Strings.EMPTY)
                .build();
    }
}

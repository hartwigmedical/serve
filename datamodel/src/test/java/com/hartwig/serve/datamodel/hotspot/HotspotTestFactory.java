package com.hartwig.serve.datamodel.hotspot;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.CommonTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class HotspotTestFactory {

    private HotspotTestFactory() {
    }

    @NotNull
    public static ImmutableKnownHotspot.Builder knownHotspotBuilder() {
        return ImmutableKnownHotspot.builder()
                .from(CommonTestFactory.createEmptyGeneAlteration())
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .transcript(null)
                .proteinAnnotation(Strings.EMPTY);

    }

    @NotNull
    public static KnownHotspot createTestKnownHotspotForSource(@NotNull Knowledgebase source) {
        return knownHotspotBuilder().addSources(source).build();
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspot() {
        return knownHotspotBuilder().build();
    }

    @NotNull
    public static ImmutableActionableHotspot.Builder actionableHotspotBuilder() {
        return ImmutableActionableHotspot.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .from(CommonTestFactory.createEmptyGeneAlteration())
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY);
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspotForSource(@NotNull Knowledgebase source) {
        return actionableHotspotBuilder().source(source).build();
    }
}

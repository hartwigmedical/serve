package com.hartwig.serve.datamodel.hotspot;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class HotspotTestFactory {

    private HotspotTestFactory() {
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspotForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownHotspot.builder().from(createTestKnownHotspot()).addSources(source).build();
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspot() {
        return ImmutableKnownHotspot.builder()
                .gene(Strings.EMPTY)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .proteinAnnotation(Strings.EMPTY)
                .build();
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspotForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableHotspot.builder().from(createTestActionableHotspot()).source(source).build();
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspot() {
        return ImmutableActionableHotspot.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .gene(Strings.EMPTY)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .build();
    }
}

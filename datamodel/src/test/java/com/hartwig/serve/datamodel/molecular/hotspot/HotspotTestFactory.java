package com.hartwig.serve.datamodel.molecular.hotspot;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.MolecularTestFactory;
import com.hartwig.serve.datamodel.molecular.common.CommonTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class HotspotTestFactory {

    private HotspotTestFactory() {
    }

    @NotNull
    public static VariantAnnotation createTestVariantAnnotation() {
        return createVariantAnnotation(Strings.EMPTY, "1", 0, Strings.EMPTY, Strings.EMPTY);
    }

    @NotNull
    public static VariantAnnotation createVariantAnnotation(@NotNull String gene, @NotNull String chromosome, int position,
            @NotNull String ref, @NotNull String alt) {
        return ImmutableVariantAnnotation.builder().gene(gene).chromosome(chromosome).position(position).ref(ref).alt(alt).build();
    }

    @NotNull
    public static ImmutableKnownHotspot.Builder knownHotspotBuilder() {
        return ImmutableKnownHotspot.builder()
                .from(createTestVariantAnnotation())
                .from(CommonTestFactory.createTestGeneAlteration())
                .inputTranscript(null)
                .inputProteinAnnotation(Strings.EMPTY);
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspotForSource(@NotNull Knowledgebase source) {
        return knownHotspotBuilder().addSources(source).build();
    }

    @NotNull
    public static ImmutableActionableHotspot.Builder actionableHotspotBuilder() {
        return ImmutableActionableHotspot.builder().from(MolecularTestFactory.createTestActionableEvent());
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspot() {
        return ImmutableActionableHotspot.builder()
                .from(MolecularTestFactory.createTestActionableEvent())
                .addVariants(createTestVariantAnnotation())
                .build();
    }
}

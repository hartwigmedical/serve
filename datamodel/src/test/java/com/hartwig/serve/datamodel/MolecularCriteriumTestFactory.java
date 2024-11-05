package com.hartwig.serve.datamodel;

import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.range.ActionableRange;

import org.jetbrains.annotations.NotNull;

public final class MolecularCriteriumTestFactory {

    private MolecularCriteriumTestFactory() {
    }

    @NotNull
    public static MolecularCriterium createWithTestActionableHotspot() {
        return createWithActionableHotspot(HotspotTestFactory.createTestActionableHotspot());
    }

    @NotNull
    public static MolecularCriterium createWithActionableHotspot(@NotNull ActionableHotspot hotspot) {
        return ImmutableMolecularCriterium.builder().addHotspots(hotspot).build();
    }

    @NotNull
    public static MolecularCriterium createWithActionableCodon(@NotNull ActionableRange codon) {
        return ImmutableMolecularCriterium.builder().addCodons(codon).build();
    }

    @NotNull
    public static MolecularCriterium createWithActionableGene(@NotNull ActionableGene gene) {
        return ImmutableMolecularCriterium.builder().addGenes(gene).build();
    }

    @NotNull
    public static MolecularCriterium createWithActionableFusion(@NotNull ActionableFusion fusion) {
        return ImmutableMolecularCriterium.builder().addFusions(fusion).build();
    }
}

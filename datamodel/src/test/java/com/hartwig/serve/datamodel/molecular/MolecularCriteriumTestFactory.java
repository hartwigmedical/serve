package com.hartwig.serve.datamodel.molecular;

import java.util.Set;

import com.hartwig.serve.datamodel.molecular.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.characteristic.CharacteristicTestFactory;
import com.hartwig.serve.datamodel.molecular.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.molecular.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.molecular.range.ActionableRange;

import org.jetbrains.annotations.NotNull;

public final class MolecularCriteriumTestFactory {

    private MolecularCriteriumTestFactory() {
    }

    @NotNull
    public static Set<MolecularCriterium> createWithTestActionableHotspot() {
        return Set.of(createWithActionableHotspot(HotspotTestFactory.createTestActionableHotspot()));
    }

    @NotNull
    public static Set<MolecularCriterium> createWithTestActionableGene() {
        return Set.of(createWithActionableGene(GeneTestFactory.createTestActionableGene()));
    }

    @NotNull
    public static Set<MolecularCriterium> createWithTestActionableCharacteristic() {
        return Set.of(createWithActionableCharacteristics(CharacteristicTestFactory.createTestActionableCharacteristic()));
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

    @NotNull
    public static MolecularCriterium createWithActionableCharacteristics(@NotNull ActionableCharacteristic characteristic) {
        return ImmutableMolecularCriterium.builder().addCharacteristics(characteristic).build();
    }

    @NotNull
    public static MolecularCriterium createWithActionableExons(@NotNull ActionableRange exon) {
        return ImmutableMolecularCriterium.builder().addExons(exon).build();
    }
}

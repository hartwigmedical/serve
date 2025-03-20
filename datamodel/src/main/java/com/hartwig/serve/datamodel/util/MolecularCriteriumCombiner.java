package com.hartwig.serve.datamodel.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;

import org.jetbrains.annotations.NotNull;

public final class MolecularCriteriumCombiner {
    @NotNull
    public static MolecularCriterium combine(@NotNull List<MolecularCriterium> criteria) {
        return criteria.stream().reduce(MolecularCriteriumCombiner::combine)
                .orElse(ImmutableMolecularCriterium.builder().build());
    }

    @NotNull
    public static MolecularCriterium combine(@NotNull MolecularCriterium criteria1, @NotNull MolecularCriterium criteria2) {
        return ImmutableMolecularCriterium.builder()
                .addAllHotspots(union(criteria1.hotspots(), criteria2.hotspots()))
                .addAllCodons(union(criteria1.codons(), criteria2.codons()))
                .addAllExons(union(criteria1.exons(), criteria2.exons()))
                .addAllGenes(union(criteria1.genes(), criteria2.genes()))
                .addAllFusions(union(criteria1.fusions(), criteria2.fusions()))
                .addAllCharacteristics(union(criteria1.characteristics(), criteria2.characteristics()))
                .addAllHla(union(criteria1.hla(), criteria2.hla()))
                .build();
    }

    @NotNull
    private static <T> Set<T> union(@NotNull Set<T> set1, @NotNull Set<T> set2) {
        return Stream.concat(set1.stream(), set2.stream())
                .collect(Collectors.toSet());
    }
}

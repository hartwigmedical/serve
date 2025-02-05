package com.hartwig.serve.sources.ckb;

import java.util.List;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

final class MolecularCriteriaCombiner {

    private static final Logger LOGGER = LogManager.getLogger(MolecularCriteriaCombiner.class);

    @NotNull
    public static MolecularCriterium combine(@NotNull List<MolecularCriterium> criteria) {
        return criteria.stream().reduce(MolecularCriteriaCombiner::combine)
                .orElse(ImmutableMolecularCriterium.builder().build());
    }

    @NotNull
    public static MolecularCriterium combine(@NotNull MolecularCriterium criteria1, @NotNull MolecularCriterium criteria2) {
        return ImmutableMolecularCriterium.builder()
                .addAllAllOfAnyHotspots(Sets.union(criteria1.allOfAnyHotspots(), criteria2.allOfAnyHotspots()))
                .addAllHotspots(Sets.union(criteria1.hotspots(), criteria2.hotspots()))
                .addAllCodons(Sets.union(criteria1.codons(), criteria2.codons()))
                .addAllExons(Sets.union(criteria1.exons(), criteria2.exons()))
                .addAllGenes(Sets.union(criteria1.genes(), criteria2.genes()))
                .addAllFusions(Sets.union(criteria1.fusions(), criteria2.fusions()))
                .addAllCharacteristics(Sets.union(criteria1.characteristics(), criteria2.characteristics()))
                .addAllHla(Sets.union(criteria1.hla(), criteria2.hla()))
                .build();
    }
}

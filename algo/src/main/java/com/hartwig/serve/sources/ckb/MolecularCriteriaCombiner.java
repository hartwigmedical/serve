package com.hartwig.serve.sources.ckb;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

// TODO probably better naming to clarify semantics, cleanup ultimately unused methods
final class MolecularCriteriaCombiner {

    private static final Logger LOGGER = LogManager.getLogger(MolecularCriteriaCombiner.class);

    @NotNull
    public static Set<MolecularCriterium> combine(List<Set<MolecularCriterium>> multiCriteria) {
        Set<MolecularCriterium> result = multiCriteria.stream()
                .reduce(MolecularCriteriaCombiner::combineSets)
                .orElse(Set.of());

        LOGGER.info("Combined {} molecular criteria sets into combined set of {} criteria", multiCriteria.size(), result.size());
        return result;
    }

    @NotNull
    public static Set<MolecularCriterium> combineSets(Set<MolecularCriterium> set1, Set<MolecularCriterium> set2) {
        return set1.stream()
                .flatMap(c1 -> set2.stream().map(c2 -> combineCriteria(c1, c2)))
                .collect(Collectors.toSet());
    }

    @NotNull
    public static MolecularCriterium combineCriteriaList(List<MolecularCriterium> criteria) {
        return criteria.stream().reduce(MolecularCriteriaCombiner::combineCriteria)
                .orElse(ImmutableMolecularCriterium.builder().build());
    }

    @NotNull
    public static MolecularCriterium combineCriteria(MolecularCriterium criteria1, MolecularCriterium criteria2) {
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

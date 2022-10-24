package com.hartwig.serve.extraction.util;

import java.util.List;

import com.hartwig.serve.common.drivergene.DriverCategory;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.datamodel.MutationType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MutationTypeFilterAlgo {

    @NotNull
    private final List<DriverGene> driverGenes;

    public MutationTypeFilterAlgo(@NotNull final List<DriverGene> driverGenes) {
        this.driverGenes = driverGenes;
    }

    @NotNull
    public MutationType determine(@NotNull String gene, @NotNull String event) {
        String formattedEvent = event.toLowerCase();

        if (formattedEvent.contains("skipping") || formattedEvent.contains("splice")) {
            return MutationType.SPLICE;
        } else if (formattedEvent.contains("deletion/insertion") || formattedEvent.contains("insertions/deletions")) {
            return MutationType.INFRAME;
        } else if (formattedEvent.contains("deletion") || formattedEvent.contains("del")) {
            return MutationType.INFRAME_DELETION;
        } else if (formattedEvent.contains("insertion") || formattedEvent.contains("ins")) {
            return MutationType.INFRAME_INSERTION;
        } else if (formattedEvent.contains("frameshift")) {
            return MutationType.NONSENSE_OR_FRAMESHIFT;
        } else {
            DriverGene driverGene = findByGene(gene);
            if (driverGene != null) {
                if (driverGene.likelihoodType() == DriverCategory.ONCO) {
                    return MutationType.MISSENSE;
                } else if (driverGene.likelihoodType() == DriverCategory.TSG) {
                    return MutationType.ANY;
                }
            }
        }

        return MutationType.ANY;
    }

    @Nullable
    private DriverGene findByGene(@NotNull String gene) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(gene)) {
                return driverGene;
            }
        }
        return null;
    }
}

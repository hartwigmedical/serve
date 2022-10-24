package com.hartwig.serve.datamodel;

import com.hartwig.serve.datamodel.cancertype.ImmutableCancerType;
import com.hartwig.serve.datamodel.gene.GeneLevelEvent;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestBuilders {

    private DatamodelTestBuilders() {
    }

    @NotNull
    public static ImmutableActionableGene.Builder actionableGeneBuilder() {
        return ImmutableActionableGene.builder()
                .from(DatamodelTestFactory.createEmptyEvent())
                .from(DatamodelTestFactory.createEmptyGeneAlteration())
                .event(GeneLevelEvent.ANY_MUTATION);
    }

    @NotNull
    public static ImmutableTreatment.Builder treatmentBuilder() {
        return ImmutableTreatment.builder().treament(Strings.EMPTY);
    }

    @NotNull
    public static ImmutableCancerType.Builder cancerTypeBuilder() {
        return ImmutableCancerType.builder().name(Strings.EMPTY).doid(Strings.EMPTY);
    }
}

package com.hartwig.serve.datamodel;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestBuilders {

    private DatamodelTestBuilders() {
    }

    @NotNull
    public static ImmutableTreatment.Builder treatmentBuilder() {
        return ImmutableTreatment.builder().name(Strings.EMPTY);
    }

    @NotNull
    public static ImmutableCancerType.Builder cancerTypeBuilder() {
        return ImmutableCancerType.builder().name(Strings.EMPTY).doid(Strings.EMPTY);
    }
}

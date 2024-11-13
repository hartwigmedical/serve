package com.hartwig.serve.datamodel.common;

import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestFactory {

    private DatamodelTestFactory() {
    }

    @NotNull
    public static ImmutableCancerType.Builder cancerTypeBuilder() {
        return ImmutableCancerType.builder().name(Strings.EMPTY).doid(Strings.EMPTY);
    }

    @NotNull
    public static CancerType createTestCancerBuilder() {
        return cancerTypeBuilder().build();
    }

    @NotNull
    public static ImmutableIndication.Builder indicationBuilder() {
        return ImmutableIndication.builder().applicableType(createTestCancerBuilder());
    }

    @NotNull
    public static Indication createTestIndication(@NotNull String applicableType, @NotNull String excludedSubType) {
        return indicationBuilder().applicableType(cancerTypeBuilder().name(applicableType).build())
                .excludedSubTypes(Set.of(cancerTypeBuilder().name(excludedSubType).build()))
                .build();
    }
}
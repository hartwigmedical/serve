package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestFactory {

    private DatamodelTestFactory() {
    }

    @NotNull
    public static ImmutableTreatment.Builder treatmentBuilder() {
        return ImmutableTreatment.builder().name(Strings.EMPTY);
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
    public static Indication createTestIndication(@NotNull String applicableType, @NotNull String nonApplicableSubType) {
        return indicationBuilder().applicableType(cancerTypeBuilder().name(applicableType).build())
                .nonApplicableSubTypes(Set.of(cancerTypeBuilder().name(nonApplicableSubType).build()))
                .build();
    }

    @NotNull
    public static ImmutableCountry.Builder countryBuilder() {
        return ImmutableCountry.builder().countryName(Strings.EMPTY).hospitalsPerCity(Maps.newHashMap());
    }

    @NotNull
    public static Country createTestCountry(@NotNull String countryName) {
        return countryBuilder().countryName(countryName)
                .hospitalsPerCity(Map.of("city",
                        Set.of(ImmutableHospital.builder().isChildrensHospital(false).name("hospital name").build())))
                .build();
    }

    @NotNull
    public static ActionableEvent createTestActionableEvent() {
        return new ActionableEventImpl(LocalDate.EPOCH, Strings.EMPTY, Sets.newHashSet());
    }

    private static class ActionableEventImpl implements ActionableEvent {

        @NotNull
        private final LocalDate sourceDate;
        @NotNull
        private final String sourceEvent;
        @NotNull
        private final Set<String> sourceUrls;

        public ActionableEventImpl(@NotNull final LocalDate sourceDate, @NotNull final String sourceEvent,
                @NotNull final Set<String> sourceUrls) {
            this.sourceDate = sourceDate;
            this.sourceEvent = sourceEvent;
            this.sourceUrls = sourceUrls;
        }

        @NotNull
        @Override
        public LocalDate sourceDate() {
            return sourceDate;
        }

        @NotNull
        @Override
        public String sourceEvent() {
            return sourceEvent;
        }

        @NotNull
        @Override
        public Set<String> sourceUrls() {
            return sourceUrls;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final ActionableEventImpl that = (ActionableEventImpl) o;
            return Objects.equals(sourceDate, that.sourceDate) && Objects.equals(sourceEvent, that.sourceEvent)
                    && Objects.equals(sourceUrls, that.sourceUrls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceDate, sourceEvent, sourceUrls);
        }

        @Override
        public String toString() {
            return "ActionableEventImpl{" + "sourceDate=" + sourceDate + ", sourceEvent='" + sourceEvent + '\'' + ", sourceUrls="
                    + sourceUrls + '}';
        }
    }
}
package com.hartwig.serve.datamodel;

import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestFactory {

    private DatamodelTestFactory() {
    }

    @NotNull
    public static String setToField(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(",");
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    @NotNull
    public static ImmutableClinicalTrial.Builder clinicalTrialBuilderBuilder() {
        return ImmutableClinicalTrial.builder()
                .studyNctId(Strings.EMPTY)
                .studyTitle(Strings.EMPTY)
                .countriesOfStudy(Sets.newHashSet())
                .therapyNames(Sets.newHashSet());
    }

    @NotNull
    public static ImmutableTreatment.Builder extractTreatment() {
        return ImmutableTreatment.builder().name(Strings.EMPTY);
    }

    @NotNull
    public static ImmutableCancerType.Builder cancerTypeBuilder() {
        return ImmutableCancerType.builder().name(Strings.EMPTY).doid(Strings.EMPTY);
    }

    @NotNull
    public static Treatment extractTreatment(@NotNull ActionableEvent event) {
        Treatment treatment = null;
        if (event.intervention() instanceof Treatment) {
            treatment = (Treatment) event.intervention();
        }

        if (treatment == null) {
            throw new IllegalStateException("An actionable event has to contain treatment");
        }
        return treatment;
    }

    @NotNull
    public static ClinicalTrial extractClinicalTrial(@NotNull ActionableEvent event) {
        ClinicalTrial clinicalTrial = null;
        if (event.intervention() instanceof ClinicalTrial) {
            clinicalTrial = (ClinicalTrial) event.intervention();
        }

        if (clinicalTrial == null) {
            throw new IllegalStateException("An actionable event has to contain clinical trial");
        }
        return clinicalTrial;
    }

    @NotNull
    public static Intervention interventionBuilder(boolean isTrial, boolean isTreatment, @NotNull String treatmentName) {
        ClinicalTrial clinicalTrial = isTrial ? clinicalTrialBuilderBuilder().therapyNames(Sets.newHashSet(treatmentName)).build() : null;
        Treatment treatment = isTreatment ? extractTreatment().name(treatmentName).build() : null;

        if ((clinicalTrial == null && treatment == null) || (clinicalTrial != null && treatment != null)) {
            throw new IllegalStateException("An actionable event has to contain either treatment or clinical trial");
        }
        return isTrial ? clinicalTrial : treatment;
    }

    @NotNull
    public static ActionableEvent createTestActionableEvent() {

        return createActionableEvent(Knowledgebase.UNKNOWN,
                Strings.EMPTY,
                Sets.newHashSet(),
                interventionBuilder(false, true, "treatment1"),
                DatamodelTestFactory.cancerTypeBuilder().build(),
                Sets.newHashSet(),
                EvidenceLevel.A,
                EvidenceDirection.NO_BENEFIT,
                Sets.newHashSet());
    }

    @NotNull
    public static ActionableEvent createActionableEvent(@NotNull Knowledgebase source, @NotNull String sourceEvent,
            @NotNull Set<String> sourceUrls, @NotNull Intervention intervention, @NotNull CancerType applicableCancerType,
            @NotNull Set<CancerType> blacklistCancerTypes, @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction,
            @NotNull Set<String> evidenceUrls) {
        return new ActionableEventImpl(source,
                sourceEvent,
                sourceUrls,
                intervention,
                applicableCancerType,
                blacklistCancerTypes,
                level,
                direction,
                evidenceUrls);
    }

    private static class ActionableEventImpl implements ActionableEvent {

        @NotNull
        private final Knowledgebase source;
        @NotNull
        private final String sourceEvent;
        @NotNull
        private final Set<String> sourceUrls;
        @NotNull
        private final Intervention intervention;
        @NotNull
        private final CancerType applicableCancerType;
        @NotNull
        private final Set<CancerType> blacklistCancerTypes;
        @NotNull
        private final EvidenceLevel level;
        @NotNull
        private final EvidenceDirection direction;
        @NotNull
        private final Set<String> evidenceUrls;

        public ActionableEventImpl(@NotNull Knowledgebase source, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls,
                @NotNull Intervention intervention, @NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistCancerTypes,
                @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls) {
            this.source = source;
            this.sourceEvent = sourceEvent;
            this.sourceUrls = sourceUrls;
            this.intervention = intervention;
            this.applicableCancerType = applicableCancerType;
            this.blacklistCancerTypes = blacklistCancerTypes;
            this.level = level;
            this.direction = direction;
            this.evidenceUrls = evidenceUrls;
        }

        @NotNull
        @Override
        public Knowledgebase source() {
            return source;
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

        @NotNull
        @Override
        public Intervention intervention() {
            return intervention;
        }

        @NotNull
        @Override
        public CancerType applicableCancerType() {
            return applicableCancerType;
        }

        @NotNull
        @Override
        public Set<CancerType> blacklistCancerTypes() {
            return blacklistCancerTypes;
        }

        @NotNull
        @Override
        public EvidenceLevel level() {
            return level;
        }

        @NotNull
        @Override
        public EvidenceDirection direction() {
            return direction;
        }

        @NotNull
        @Override
        public Set<String> evidenceUrls() {
            return evidenceUrls;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ActionableEventImpl that = (ActionableEventImpl) o;
            return source == that.source && Objects.equals(sourceEvent, that.sourceEvent) && Objects.equals(sourceUrls, that.sourceUrls)
                    && Objects.equals(intervention, that.intervention) && Objects.equals(applicableCancerType, that.applicableCancerType)
                    && Objects.equals(blacklistCancerTypes, that.blacklistCancerTypes) && level == that.level && direction == that.direction
                    && Objects.equals(evidenceUrls, that.evidenceUrls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source,
                    sourceEvent,
                    sourceUrls,
                    intervention,
                    applicableCancerType,
                    blacklistCancerTypes,
                    level,
                    direction,
                    evidenceUrls);
        }

        @Override
        public String toString() {
            return "ActionableEventImpl{" + "source=" + source + ", sourceEvent='" + sourceEvent + '\'' + ", sourceUrls=" + sourceUrls
                    + ", intervention=" + intervention + ", applicableCancerType=" + applicableCancerType + ", blacklistCancerTypes="
                    + blacklistCancerTypes + ", level=" + level + ", direction=" + direction + ", evidenceUrls=" + evidenceUrls + '}';
        }
    }
}
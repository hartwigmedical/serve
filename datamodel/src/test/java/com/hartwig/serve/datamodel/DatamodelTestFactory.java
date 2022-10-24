package com.hartwig.serve.datamodel;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.cancertype.CancerType;
import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestFactory {

    private DatamodelTestFactory() {
    }

    @NotNull
    static GeneAlteration createEmptyGeneAlteration() {
        return createGeneAlteration(Strings.EMPTY, GeneRole.UNKNOWN, ProteinEffect.UNKNOWN);
    }

    @NotNull
    public static GeneAlteration createGeneAlteration(@NotNull String gene, @NotNull GeneRole geneRole,
            @NotNull ProteinEffect proteinEffect) {
        return new GeneAlterationImpl(gene, geneRole, proteinEffect);
    }

    @NotNull
    static ActionableEvent createEmptyEvent() {
        return createEvent(Knowledgebase.UNKNOWN,
                Strings.EMPTY,
                Sets.newHashSet(),
                DatamodelTestBuilders.treatmentBuilder().build(),
                DatamodelTestBuilders.cancerTypeBuilder().build(),
                Sets.newHashSet(),
                EvidenceLevel.A,
                EvidenceDirection.NO_BENEFIT,
                Sets.newHashSet());
    }

    @NotNull
    public static ActionableEvent createEvent(@NotNull Knowledgebase source, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls,
            @NotNull Treatment treatment, @NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistCancerTypes,
            @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls) {
        return new ActionableEventImpl(source,
                sourceEvent,
                sourceUrls,
                treatment,
                applicableCancerType,
                blacklistCancerTypes,
                level,
                direction,
                evidenceUrls);
    }

    private static class GeneAlterationImpl implements GeneAlteration {
        @NotNull
        private final String gene;
        @NotNull
        private final GeneRole geneRole;
        @NotNull
        private final ProteinEffect proteinEffect;

        public GeneAlterationImpl(@NotNull final String gene, @NotNull final GeneRole geneRole,
                @NotNull final ProteinEffect proteinEffect) {
            this.gene = gene;
            this.geneRole = geneRole;
            this.proteinEffect = proteinEffect;
        }

        @NotNull
        @Override
        public String gene() {
            return gene;
        }

        @NotNull
        @Override
        public GeneRole geneRole() {
            return geneRole;
        }

        @NotNull
        @Override
        public ProteinEffect proteinEffect() {
            return proteinEffect;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final GeneAlterationImpl that = (GeneAlterationImpl) o;
            return gene.equals(that.gene) && geneRole == that.geneRole && proteinEffect == that.proteinEffect;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, geneRole, proteinEffect);
        }

        @Override
        public String toString() {
            return "GeneAlterationImpl{" + "gene='" + gene + '\'' + ", geneRole=" + geneRole + ", proteinEffect=" + proteinEffect + '}';
        }
    }

    private static class ActionableEventImpl implements ActionableEvent {

        @NotNull
        private final Knowledgebase source;
        @NotNull
        private final String sourceEvent;
        @NotNull
        private final Set<String> sourceUrls;
        @NotNull
        private final Treatment treatment;
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
                @NotNull Treatment treatment, @NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistCancerTypes,
                @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls) {
            this.source = source;
            this.sourceEvent = sourceEvent;
            this.sourceUrls = sourceUrls;
            this.treatment = treatment;
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
        public Treatment treatment() {
            return treatment;
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
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final ActionableEventImpl that = (ActionableEventImpl) o;
            return source == that.source && sourceEvent.equals(that.sourceEvent) && sourceUrls.equals(that.sourceUrls) && treatment.equals(
                    that.treatment) && applicableCancerType.equals(that.applicableCancerType)
                    && blacklistCancerTypes.equals(that.blacklistCancerTypes) && level == that.level && direction == that.direction
                    && evidenceUrls.equals(that.evidenceUrls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source,
                    sourceEvent,
                    sourceUrls,
                    treatment,
                    applicableCancerType,
                    blacklistCancerTypes,
                    level,
                    direction,
                    evidenceUrls);
        }

        @Override
        public String toString() {
            return "ActionableEventImpl{" + "source=" + source + ", sourceEvent='" + sourceEvent + '\'' + ", sourceUrls=" + sourceUrls
                    + ", treatment=" + treatment + ", applicableCancerType=" + applicableCancerType + ", blacklistCancerTypes="
                    + blacklistCancerTypes + ", level=" + level + ", direction=" + direction + ", evidenceUrls=" + evidenceUrls + '}';
        }
    }
}

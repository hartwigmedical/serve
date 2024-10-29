package com.hartwig.serve.datamodel.fusion;

import java.util.Objects;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FusionTestFactory {

    private FusionTestFactory() {
    }

    @NotNull
    public static FusionPair createFusionPair(@NotNull String geneUp, @NotNull String geneDown, @Nullable Integer minExonUp,
            @Nullable Integer minExonDown) {
        return new FusionPairImpl(geneUp, minExonUp, null, geneDown, minExonDown, null);
    }

    @NotNull
    public static ImmutableKnownFusion.Builder knownFusionBuilder() {
        return ImmutableKnownFusion.builder().geneUp(Strings.EMPTY).geneDown(Strings.EMPTY).proteinEffect(ProteinEffect.UNKNOWN);
    }

    @NotNull
    public static KnownFusion createTestKnownFusionForSource(@NotNull Knowledgebase source) {
        return knownFusionBuilder().addSources(source).build();
    }

    @NotNull
    public static ImmutableActionableFusion.Builder actionableFusionBuilder() {
        return ImmutableActionableFusion.builder()
                .from(DatamodelTestFactory.createTestActionableEvent())
                .geneUp(Strings.EMPTY)
                .geneDown(Strings.EMPTY);
    }

    @NotNull
    public static ActionableFusion createTestActionableFusionForSource() {
        return actionableFusionBuilder().build();
    }

    private static class FusionPairImpl implements FusionPair {

        @NotNull
        private final String geneUp;
        @Nullable
        private final Integer minExonUp;
        @Nullable
        private final Integer maxExonUp;
        @NotNull
        private final String geneDown;
        @Nullable
        private final Integer minExonDown;
        @Nullable
        private final Integer maxExonDown;

        public FusionPairImpl(@NotNull final String geneUp, @Nullable final Integer minExonUp, @Nullable final Integer maxExonUp,
                @NotNull final String geneDown, @Nullable final Integer minExonDown, @Nullable final Integer maxExonDown) {
            this.geneUp = geneUp;
            this.minExonUp = minExonUp;
            this.maxExonUp = maxExonUp;
            this.geneDown = geneDown;
            this.minExonDown = minExonDown;
            this.maxExonDown = maxExonDown;
        }

        @NotNull
        @Override
        public String geneUp() {
            return geneUp;
        }

        @Nullable
        @Override
        public Integer minExonUp() {
            return minExonUp;
        }

        @Nullable
        @Override
        public Integer maxExonUp() {
            return maxExonUp;
        }

        @NotNull
        @Override
        public String geneDown() {
            return geneDown;
        }

        @Nullable
        @Override
        public Integer minExonDown() {
            return minExonDown;
        }

        @Nullable
        @Override
        public Integer maxExonDown() {
            return maxExonDown;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final FusionPairImpl that = (FusionPairImpl) o;
            return geneUp.equals(that.geneUp) && Objects.equals(minExonUp, that.minExonUp) && Objects.equals(maxExonUp, that.maxExonUp)
                    && geneDown.equals(that.geneDown) && Objects.equals(minExonDown, that.minExonDown) && Objects.equals(maxExonDown,
                    that.maxExonDown);
        }

        @Override
        public int hashCode() {
            return Objects.hash(geneUp, minExonUp, maxExonUp, geneDown, minExonDown, maxExonDown);
        }

        @Override
        public String toString() {
            return "FusionPairImpl{" + "geneUp='" + geneUp + '\'' + ", minExonUp=" + minExonUp + ", maxExonUp=" + maxExonUp + ", geneDown='"
                    + geneDown + '\'' + ", minExonDown=" + minExonDown + ", maxExonDown=" + maxExonDown + '}';
        }
    }
}

package com.hartwig.serve.datamodel.characteristic;

import java.util.Objects;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CharacteristicTestFactory {

    private CharacteristicTestFactory() {
    }

    @NotNull
    public static TumorCharacteristic createTumorCharacteristic(@NotNull TumorCharacteristicType type,
            @Nullable TumorCharacteristicCutoffType cutoffType, @Nullable Double cutoff) {
        return new TumorCharacteristicImpl(type, cutoffType, cutoff);
    }

    @NotNull
    public static ImmutableActionableCharacteristic.Builder actionableCharacteristicBuilder() {
        return ImmutableActionableCharacteristic.builder()
                .from(DatamodelTestFactory.createTestActionableEvent())
                .type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE);
    }

    @NotNull
    public static ActionableCharacteristic createTestActionableCharacteristicForSource(@NotNull Knowledgebase source) {
        return actionableCharacteristicBuilder().source(source).build();
    }

    private static class TumorCharacteristicImpl implements TumorCharacteristic {

        @NotNull
        private final TumorCharacteristicType type;
        @Nullable
        private final TumorCharacteristicCutoffType cutoffType;
        @Nullable
        private final Double cutoff;

        public TumorCharacteristicImpl(@NotNull final TumorCharacteristicType type,
                @Nullable final TumorCharacteristicCutoffType cutoffType, @Nullable final Double cutoff) {
            this.type = type;
            this.cutoffType = cutoffType;
            this.cutoff = cutoff;
        }

        @NotNull
        @Override
        public TumorCharacteristicType type() {
            return type;
        }

        @Nullable
        @Override
        public TumorCharacteristicCutoffType cutoffType() {
            return cutoffType;
        }

        @Nullable
        @Override
        public Double cutoff() {
            return cutoff;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final TumorCharacteristicImpl that = (TumorCharacteristicImpl) o;
            return type == that.type && cutoffType == that.cutoffType && Objects.equals(cutoff, that.cutoff);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, cutoffType, cutoff);
        }

        @Override
        public String toString() {
            return "TumorCharacteristicImpl{" + "type=" + type + ", cutoffType=" + cutoffType + ", cutoff=" + cutoff + '}';
        }
    }
}

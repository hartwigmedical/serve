package com.hartwig.serve.datamodel.molecular;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class MolecularTestFactory {

    private MolecularTestFactory() {
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

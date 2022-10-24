package com.hartwig.serve.datamodel.util;

import java.util.Set;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.Treatment;

import org.jetbrains.annotations.NotNull;

public final class ActionableFileFunctions {

    public static final String FIELD_DELIMITER = "\t";

    private static final String SUB_FIELD_DELIMITER = ",";
    private static final String NAME_DOID_DELIMITER = ";";

    private ActionableFileFunctions() {
    }

    @NotNull
    public static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("source")
                .add("sourceEvent")
                .add("sourceUrls")
                .add("treatment")
                .add("sourceRelevantTreatmentApproaches")
                .add("relevantTreatmentApproaches")
                .add("applicableCancerType")
                .add("applicableDoid")
                .add("blacklistCancerTypes")
                .add("level")
                .add("direction")
                .add("evidenceUrls")
                .toString();
    }

    @NotNull
    public static ActionableEvent fromLine(@NotNull String[] values, int startingPosition) {
        return new ActionableEvent() {

            @NotNull
            @Override
            public Knowledgebase source() {
                return Knowledgebase.valueOf(values[startingPosition]);
            }

            @NotNull
            @Override
            public String sourceEvent() {
                return values[startingPosition + 1];
            }

            @NotNull
            @Override
            public Set<String> sourceUrls() {
                int urlPosition = startingPosition + 2;
                return values.length > urlPosition ? fieldToSet(values[urlPosition]) : Sets.newHashSet();
            }

            @NotNull
            @Override
            public Treatment treatment() {
                return ImmutableTreatment.builder()
                        .name(values[startingPosition + 3])
                        .sourceRelevantTreatmentApproaches(fieldToSet(values[startingPosition + 4]))
                        .relevantTreatmentApproaches(fieldToSet(values[startingPosition + 5]))
                        .build();
            }

            @NotNull
            @Override
            public CancerType applicableCancerType() {
                return ImmutableCancerType.builder().name(values[startingPosition + 6]).doid(values[startingPosition + 7]).build();
            }

            @NotNull
            @Override
            public Set<CancerType> blacklistCancerTypes() {
                return fieldToCancerTypes(values[startingPosition + 8]);
            }

            @NotNull
            @Override
            public EvidenceLevel level() {
                return EvidenceLevel.valueOf(values[startingPosition + 9]);
            }

            @NotNull
            @Override
            public EvidenceDirection direction() {
                return EvidenceDirection.valueOf(values[startingPosition + 10]);
            }

            @NotNull
            @Override
            public Set<String> evidenceUrls() {
                int urlPosition = startingPosition + 11;
                return values.length > urlPosition ? fieldToSet(values[urlPosition]) : Sets.newHashSet();
            }
        };
    }

    @NotNull
    public static String toLine(@NotNull ActionableEvent event) {
        return new StringJoiner(FIELD_DELIMITER).add(event.source().toString())
                .add(event.sourceEvent())
                .add(setToField(event.sourceUrls()))
                .add(event.treatment().name())
                .add(setToField(event.treatment().sourceRelevantTreatmentApproaches()))
                .add(setToField(event.treatment().relevantTreatmentApproaches()))
                .add(event.applicableCancerType().name())
                .add(event.applicableCancerType().doid())
                .add(cancerTypesToField(event.blacklistCancerTypes()))
                .add(event.level().toString())
                .add(event.direction().toString())
                .add(setToField(event.evidenceUrls()))
                .toString();
    }

    @NotNull
    private static Set<String> fieldToSet(@NotNull String field) {
        if (field.isEmpty()) {
            return Sets.newHashSet();
        }

        return Sets.newHashSet(field.split(SUB_FIELD_DELIMITER));
    }

    @NotNull
    private static String setToField(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(SUB_FIELD_DELIMITER);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    @VisibleForTesting
    @NotNull
    static String cancerTypesToField(@NotNull Set<CancerType> cancerTypes) {
        StringJoiner joiner = new StringJoiner(SUB_FIELD_DELIMITER);
        for (CancerType cancerType : cancerTypes) {
            joiner.add(cancerType.name() + NAME_DOID_DELIMITER + cancerType.doid());
        }
        return joiner.toString();
    }

    @VisibleForTesting
    @NotNull
    static Set<CancerType> fieldToCancerTypes(@NotNull String field) {
        if (field.isEmpty()) {
            return Sets.newHashSet();
        }

        Set<CancerType> cancerTypes = Sets.newHashSet();
        for (String cancerTypeEntry : field.split(SUB_FIELD_DELIMITER)) {
            String[] nameAndDoid = cancerTypeEntry.split(NAME_DOID_DELIMITER);
            cancerTypes.add(ImmutableCancerType.builder().name(nameAndDoid[0]).doid(nameAndDoid[1]).build());
        }

        return cancerTypes;
    }
}
package com.hartwig.serve.datamodel.serialization.util;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.*;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ActionableFileUtil {

    public static final String FIELD_DELIMITER = "\t";

    private static final String SUB_FIELD_DELIMITER = ",";
    private static final String NAME_DOID_DELIMITER = ";";

    private ActionableFileUtil() {
    }

    @NotNull
    public static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("source")
                .add("sourceEvent")
                .add("sourceUrls")
                .add("studyNctId")
                .add("studyTitle")
                .add("countriesOfStudy")
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
    public static ActionableEvent fromLine(@NotNull String[] values, Map<String, Integer> fields) {
        return new ActionableEvent() {

            @NotNull
            @Override
            public Knowledgebase source() {
                return Knowledgebase.valueOf(values[fields.get("source")]);
            }

            @NotNull
            @Override
            public String sourceEvent() {
                return values[fields.get("sourceEvent")];
            }

            @NotNull
            @Override
            public Set<String> sourceUrls() {
                return fieldToSet(values[fields.get("sourceUrls")]);
            }

            @NotNull
            @Override
            public ClinicalTrial clinicalTrial() {
                return ImmutableClinicalTrial.builder()
                        .studyNctId(values[fields.get("studyNctId")])
                        .studyTitle(values[fields.get("studyTitle")])
                        .countriesOfStudy(fieldToSet(values[fields.get("countriesOfStudy")]))
                        .build();
            }

            @NotNull
            @Override
            public Treatment treatment() {
                return ImmutableTreatment.builder()
                        .name(values[fields.get("treatment")])
                        .sourceRelevantTreatmentApproaches(fieldToSet(values[fields.get("sourceRelevantTreatmentApproaches")]))
                        .relevantTreatmentApproaches(fieldToSet(values[fields.get("relevantTreatmentApproaches")]))
                        .build();
            }

            @NotNull
            @Override
            public CancerType applicableCancerType() {
                return ImmutableCancerType.builder()
                        .name(values[fields.get("applicableCancerType")])
                        .doid(values[fields.get("applicableDoid")])
                        .build();
            }

            @NotNull
            @Override
            public Set<CancerType> blacklistCancerTypes() {
                return fieldToCancerTypes(values[fields.get("blacklistCancerTypes")]);
            }

            @NotNull
            @Override
            public EvidenceLevel level() {
                return EvidenceLevel.valueOf(values[fields.get("level")]);
            }

            @NotNull
            @Override
            public EvidenceDirection direction() {
                return EvidenceDirection.valueOf(values[fields.get("direction")]);
            }

            @NotNull
            @Override
            public Set<String> evidenceUrls() {
                int evidenceUrlPosition = fields.get("evidenceUrls");
                return values.length > evidenceUrlPosition ? fieldToSet(values[evidenceUrlPosition]) : Sets.newHashSet();
            }
        };
    }

    @NotNull
    public static String toLine(@NotNull ActionableEvent event) {
        return new StringJoiner(FIELD_DELIMITER).add(event.source().toString())
                .add(event.sourceEvent())
                .add(setToField(event.sourceUrls()))
                .add(event.clinicalTrial() == null ? Strings.EMPTY : Objects.requireNonNull(event.clinicalTrial()).studyNctId())
                .add(event.clinicalTrial() == null ? Strings.EMPTY : Objects.requireNonNull(event.clinicalTrial()).studyTitle())
                .add(event.clinicalTrial() == null ? Strings.EMPTY : setToField(Objects.requireNonNull(event.clinicalTrial()).countriesOfStudy()))
                .add(event.treatment() == null ? Strings.EMPTY : Objects.requireNonNull(event.treatment()).name())
                .add(event.treatment() == null ? Strings.EMPTY : setToField(Objects.requireNonNull(event.treatment()).sourceRelevantTreatmentApproaches()))
                .add(event.treatment() == null ? Strings.EMPTY : setToField(Objects.requireNonNull(event.treatment()).relevantTreatmentApproaches()))
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
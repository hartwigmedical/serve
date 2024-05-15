package com.hartwig.serve.datamodel.serialization.util;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Intervention;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.Treatment;

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
            public Intervention intervention() {
                boolean isClinicalTrial = !values[fields.get("studyNctId")].isEmpty();
                boolean isTreatment = values[fields.get("studyNctId")].isEmpty() && !values[fields.get("treatment")].isEmpty();

                if (isClinicalTrial && isTreatment) {
                    throw new IllegalStateException("An actionable event cannot be both a treatment and clinical trial");
                }

                if (isTreatment) {
                    return ImmutableTreatment.builder()
                            .name(values[fields.get("treatment")])
                            .sourceRelevantTreatmentApproaches(fieldToSet(values[fields.get("sourceRelevantTreatmentApproaches")]))
                            .relevantTreatmentApproaches(fieldToSet(values[fields.get("relevantTreatmentApproaches")]))
                            .build();
                } else if (isClinicalTrial) {
                    return ImmutableClinicalTrial.builder()
                            .studyNctId(values[fields.get("studyNctId")])
                            .studyTitle(values[fields.get("studyTitle")])
                            .countriesOfStudy(fieldToSet(values[fields.get("countriesOfStudy")]))
                            .therapyNames(fieldToSet(values[fields.get("treatment")]))
                            .build();
                } else {
                    throw new IllegalStateException("An actionable event has to be either a treatment or a clinical trial!");
                }
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
        ClinicalTrial clinicalTrial = null;
        Treatment treatment = null;
        if (event.intervention() instanceof ClinicalTrial) {
            clinicalTrial = (ClinicalTrial) event.intervention();
        } else if (event.intervention() instanceof Treatment) {
            treatment = (Treatment) event.intervention();
        }

        if ((clinicalTrial == null && treatment == null)) {
            throw new IllegalStateException("An actionable event has to contain either treatment or clinical trial: " + event);
        }

        Set<String> therapy = Sets.newHashSet();
        if (clinicalTrial != null) {
            therapy = clinicalTrial.therapyNames();
        } else {
            therapy.add(treatment.name());
        }
        return new StringJoiner(FIELD_DELIMITER).add(event.source().toString())
                .add(event.sourceEvent())
                .add(setToField(event.sourceUrls()))
                .add(clinicalTrial != null ? clinicalTrial.studyNctId() : Strings.EMPTY)
                .add(clinicalTrial != null ? clinicalTrial.studyTitle() : Strings.EMPTY)
                .add(clinicalTrial != null ? setToField(clinicalTrial.countriesOfStudy()) : Strings.EMPTY)
                .add(setToField(therapy))
                .add(treatment != null ? setToField(treatment.sourceRelevantTreatmentApproaches()) : Strings.EMPTY)
                .add(treatment != null ? setToField(treatment.relevantTreatmentApproaches()) : Strings.EMPTY)
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
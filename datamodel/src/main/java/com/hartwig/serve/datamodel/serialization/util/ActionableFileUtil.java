package com.hartwig.serve.datamodel.serialization.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ApprovalStatus;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.Country;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.ImmutableCountry;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Intervention;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.Treatment;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ActionableFileUtil {

    public static final String FIELD_DELIMITER = "\t";

    private static final String MAIN_DELIMITER = ",";
    private static final String SUB_DELIMITER = ";";

    private ActionableFileUtil() {
    }

    @NotNull
    public static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("source")
                .add("sourceEvent")
                .add("sourceUrls")
                .add("nctId")
                .add("title")
                .add("acronym")
                .add("genderCriterium")
                .add("countriesAndCities")
                .add("hospitalsPerCity")
                .add("treatment")
                .add("treatmentApproachesDrugClass")
                .add("treatmentApproachesTherapy")
                .add("applicableCancerType")
                .add("applicableDoid")
                .add("blacklistCancerTypes")
                .add("level")
                .add("approvalStatus")
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
                boolean isClinicalTrial = !values[fields.get("nctId")].isEmpty();
                boolean isTreatment = values[fields.get("nctId")].isEmpty() && !values[fields.get("treatment")].isEmpty();

                if (isClinicalTrial && isTreatment) {
                    throw new IllegalStateException("An actionable event cannot be both a treatment and clinical trial");
                }
                if (isTreatment) {
                    return ImmutableTreatment.builder()
                            .name(values[fields.get("treatment")])
                            .treatmentApproachesDrugClass(fieldToSet(values[fields.get("treatmentApproachesDrugClass")]))
                            .treatmentApproachesTherapy(fieldToSet(values[fields.get("treatmentApproachesTherapy")]))
                            .build();
                } else if (isClinicalTrial) {
                    return ImmutableClinicalTrial.builder()
                            .nctId(values[fields.get("nctId")])
                            .title(values[fields.get("title")])
                            .acronym(SerializationUtil.optionalString(values[fields.get("acronym")]))
                            .genderCriterium(SerializationUtil.optionalString(values[fields.get("genderCriterium")]))
                            .countries(twoFieldsToCountries(values[fields.get("countriesAndCities")],
                                    values[fields.get("hospitalsPerCity")]))
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
            public ApprovalStatus approvalStatus() { return ApprovalStatus.valueOf(values[fields.get("approvalStatus")]); }

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
                .add(clinicalTrial != null ? clinicalTrial.nctId() : Strings.EMPTY)
                .add(clinicalTrial != null ? clinicalTrial.title() : Strings.EMPTY)
                .add(clinicalTrial != null && clinicalTrial.acronym() != null ? clinicalTrial.acronym() : Strings.EMPTY)
                .add(clinicalTrial != null && clinicalTrial.genderCriterium() != null ? clinicalTrial.genderCriterium() : Strings.EMPTY)
                .add(clinicalTrial != null ? countriesToCountryNameAndCitiesField(clinicalTrial.countries()) : Strings.EMPTY)
                .add(clinicalTrial != null ? countriesToHospitalsField(clinicalTrial.countries()) : Strings.EMPTY)
                .add(setToField(therapy))
                .add(treatment != null && !treatment.treatmentApproachesDrugClass().isEmpty()
                        ? setToField(treatment.treatmentApproachesDrugClass())
                        : Strings.EMPTY)
                .add(treatment != null && !treatment.treatmentApproachesTherapy().isEmpty()
                        ? setToField(treatment.treatmentApproachesTherapy())
                        : Strings.EMPTY)
                .add(event.applicableCancerType().name())
                .add(event.applicableCancerType().doid())
                .add(cancerTypesToField(event.blacklistCancerTypes()))
                .add(event.level().toString())
                .add(event.approvalStatus().toString())
                .add(event.direction().toString())
                .add(setToField(event.evidenceUrls()))
                .toString();
    }

    @NotNull
    private static Set<String> fieldToSet(@NotNull String field) {
        if (field.isEmpty()) {
            return Sets.newHashSet();
        }

        return Sets.newHashSet(field.split(MAIN_DELIMITER));
    }

    @NotNull
    private static String setToField(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(MAIN_DELIMITER);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    @VisibleForTesting
    @NotNull
    static String countriesToCountryNameAndCitiesField(@NotNull Set<Country> countries) {
        return countries.stream()
                .map(country -> country.countryName() + "(" + String.join(SUB_DELIMITER, country.hospitalsPerCity().keySet()) + ")")
                .collect(Collectors.joining(MAIN_DELIMITER));
    }

    @VisibleForTesting
    @NotNull
    static String countriesToHospitalsField(@NotNull Set<Country> countries) {
        StringJoiner joiner = new StringJoiner(MAIN_DELIMITER);
        for (Country country : countries) {
            for (Map.Entry<String, Set<String>> entry : country.hospitalsPerCity().entrySet()) {
                String city = entry.getKey();
                Set<String> hospitals = entry.getValue();
                joiner.add(city + "(" + String.join(SUB_DELIMITER, hospitals) + ")");
            }
        }
        return joiner.toString();
    }

    @VisibleForTesting
    @NotNull
    static Set<Country> twoFieldsToCountries(@NotNull String countriesAndCitiesField, @NotNull String hospitalsField) {
        if (countriesAndCitiesField.isEmpty()) {
            return Sets.newHashSet();
        }

        Map<String, Set<String>> hospitalsPerCity = fieldToHospitalPerCity(hospitalsField);

        return Arrays.stream(countriesAndCitiesField.split(MAIN_DELIMITER)).map(part -> {
            String[] countriesAndCities = part.split("\\(", 2);
            String countryName = countriesAndCities[0];
            Set<String> cities = Arrays.stream(countriesAndCities[1].replace(")", "").split(SUB_DELIMITER)).collect(Collectors.toSet());
            Map<String, Set<String>> hospitalsPerCityForCountry =
                    cities.stream().filter(hospitalsPerCity::containsKey).collect(Collectors.toMap(city -> city, hospitalsPerCity::get));
            return ImmutableCountry.builder().countryName(countryName).hospitalsPerCity(hospitalsPerCityForCountry).build();
        }).collect(Collectors.toSet());
    }

    @NotNull
    static Map<String, Set<String>> fieldToHospitalPerCity(@NotNull String hospitalsField) {
        return Arrays.stream(hospitalsField.split(MAIN_DELIMITER)).map(part -> {
            String[] citiesAndHospitals = part.split("\\(", 2);
            String cityName = citiesAndHospitals[0];
            Set<String> hospitals = Arrays.stream(citiesAndHospitals[1].replace(")", "").split(SUB_DELIMITER)).collect(Collectors.toSet());
            return new AbstractMap.SimpleEntry<>(cityName, hospitals);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @VisibleForTesting
    @NotNull
    static String cancerTypesToField(@NotNull Set<CancerType> cancerTypes) {
        StringJoiner joiner = new StringJoiner(MAIN_DELIMITER);
        for (CancerType cancerType : cancerTypes) {
            joiner.add(cancerType.name() + SUB_DELIMITER + cancerType.doid());
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
        for (String cancerTypeEntry : field.split(MAIN_DELIMITER)) {
            String[] nameAndDoid = cancerTypeEntry.split(SUB_DELIMITER);
            cancerTypes.add(ImmutableCancerType.builder().name(nameAndDoid[0]).doid(nameAndDoid[1]).build());
        }

        return cancerTypes;
    }
}
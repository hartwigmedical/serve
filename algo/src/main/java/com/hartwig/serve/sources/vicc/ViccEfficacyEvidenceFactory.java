package com.hartwig.serve.sources.vicc;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.CancerType;
import com.hartwig.serve.datamodel.common.ImmutableCancerType;
import com.hartwig.serve.datamodel.common.ImmutableIndication;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.EvidenceDirection;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevel;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.efficacy.ImmutableEfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.ImmutableTreatment;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.sources.vicc.curation.DrugCurator;
import com.hartwig.serve.sources.vicc.curation.EvidenceLevelCurator;
import com.hartwig.serve.sources.vicc.doid.DoidLookup;
import com.hartwig.serve.vicc.datamodel.EvidenceInfo;
import com.hartwig.serve.vicc.datamodel.Phenotype;
import com.hartwig.serve.vicc.datamodel.PhenotypeType;
import com.hartwig.serve.vicc.datamodel.ViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccSource;
import com.hartwig.serve.vicc.datamodel.civic.Civic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.immutables.value.internal.$guava$.annotations.$VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ViccEfficacyEvidenceFactory {

    private static final Logger LOGGER = LogManager.getLogger(ViccEfficacyEvidenceFactory.class);

    private static final String CANCER_TYPE_SEPARATOR = ";";

    private static final Set<String> RESPONSIVE_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> RESISTANT_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> DIRECTIONS_TO_IGNORE = Sets.newHashSet();

    static {
        RESPONSIVE_DIRECTIONS.add("Responsive");
        RESPONSIVE_DIRECTIONS.add("Sensitivity");
        RESPONSIVE_DIRECTIONS.add("Sensitive");

        RESISTANT_DIRECTIONS.add("Resistant");

        DIRECTIONS_TO_IGNORE.add("Adverse response");
        DIRECTIONS_TO_IGNORE.add("No responsive");
        DIRECTIONS_TO_IGNORE.add("Not applicable");
        DIRECTIONS_TO_IGNORE.add("Conflicting");
        DIRECTIONS_TO_IGNORE.add("Na");
        DIRECTIONS_TO_IGNORE.add("N/a");
        DIRECTIONS_TO_IGNORE.add("No benefit");
        DIRECTIONS_TO_IGNORE.add("Increased toxicity");
        DIRECTIONS_TO_IGNORE.add("Increased toxicity (myelosupression)");
        DIRECTIONS_TO_IGNORE.add("Increased toxicity (ototoxicity)");
        DIRECTIONS_TO_IGNORE.add("Increased toxicity (hyperbilirubinemia)");
        DIRECTIONS_TO_IGNORE.add("Increased toxicity (haemolytic anemia)");
        DIRECTIONS_TO_IGNORE.add("Unknown");

        // These directions only appear in evidence which lacks either level or drugs
        DIRECTIONS_TO_IGNORE.add("Pathogenic");
        DIRECTIONS_TO_IGNORE.add("Likely pathogenic");
        DIRECTIONS_TO_IGNORE.add("Positive");
        DIRECTIONS_TO_IGNORE.add("Negative");
        DIRECTIONS_TO_IGNORE.add("Uncertain significance");
        DIRECTIONS_TO_IGNORE.add("Better outcome");
    }

    @NotNull
    private final DoidLookup missingDoidLookup;
    @NotNull
    private final DrugCurator drugCurator;
    @NotNull
    private final EvidenceLevelCurator evidenceLevelCurator;

    public ViccEfficacyEvidenceFactory(@NotNull final DoidLookup missingDoidLookup, @NotNull final DrugCurator drugCurator,
            @NotNull final EvidenceLevelCurator evidenceLevelCurator) {
        this.missingDoidLookup = missingDoidLookup;
        this.drugCurator = drugCurator;
        this.evidenceLevelCurator = evidenceLevelCurator;
    }

    @NotNull
    public Set<EfficacyEvidence> toEfficacyEvidence(@NotNull ViccEntry entry, @NotNull Set<MolecularCriterium> molecularCriteria) {
        Set<EfficacyEvidence> evidences = Sets.newHashSet();

        boolean isSupportive = isSupportiveEntry(entry);
        String treatment = reformatDrugLabels(entry.association().drugLabels());
        EvidenceLevel level = resolveLevel(entry.association().evidenceLabel());
        EvidenceDirection direction = resolveDirection(entry.association().responseType());

        if (isSupportive && treatment != null && level != null && direction != null) {
            ImmutableEfficacyEvidence.Builder builder = ImmutableEfficacyEvidence.builder()
                    .source(fromViccSource(entry.source()))
                    .efficacyDescription(Strings.EMPTY)
                    .evidenceLevel(evidenceLevelCurator.curate(entry.source(), entry.genes(), treatment, level, direction))
                    .evidenceLevelDetails(EvidenceLevelDetails.UNKNOWN)
                    .evidenceDirection(direction)
                    .evidenceYear(2024)
                    .urls(resolveUrls(entry.association().evidence().info()));

            List<List<String>> drugLists = drugCurator.curate(entry.source(), level, treatment);
            Map<String, Set<String>> cancerTypeToDoidsMap = buildCancerTypeToDoidsMap(resolveCancerType(entry.association().phenotype()),
                    resolveDoid(entry.association().phenotype()));

            for (Map.Entry<String, Set<String>> cancerTypeEntry : cancerTypeToDoidsMap.entrySet()) {
                String cancerType = cancerTypeEntry.getKey();
                for (String doid : cancerTypeEntry.getValue()) {
                    Set<CancerType> excludedCancerSubTypes = Sets.newHashSet();
                    if (doid.equals(CancerTypeConstants.CANCER_DOID)) {
                        excludedCancerSubTypes.add(CancerTypeConstants.LEUKEMIA_TYPE);
                        excludedCancerSubTypes.add(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE);
                        excludedCancerSubTypes.add(CancerTypeConstants.BONE_MARROW_TYPE);
                    }
                    builder.indication(ImmutableIndication.builder()
                            .applicableType(ImmutableCancerType.builder().name(cancerType).doid(doid).build())
                            .excludedSubTypes(excludedCancerSubTypes)
                            .build());

                    for (List<String> drugList : drugLists) {
                        builder.treatment(ImmutableTreatment.builder().name(formatDrugList(drugList)).build());
                        for (MolecularCriterium molecularCriterium : molecularCriteria) {
                            evidences.add(builder.molecularCriterium(molecularCriterium).build());
                        }
                    }
                }
            }
        }

        return evidences;
    }

    private static boolean isSupportiveEntry(@NotNull ViccEntry entry) {
        // CIViC contributes entries that seem "sensitive" or "resistant" but are not "supportive" and rather do not support the evidence.
        // We do not want to generate actionability for them (see also INC-92)
        if (entry.kbSpecificObject() instanceof Civic) {
            String direction = ((Civic) entry.kbSpecificObject()).evidenceItem().evidenceDirection();
            if (direction == null || direction.equals("Supports")) {
                return true;
            } else if (direction.equals("Does Not Support")) {
                return false;
            } else {
                LOGGER.warn("Unrecognized CIViC direction entry '{}' in entry {}", direction, entry);
                return true;
            }
        } else {
            return true;
        }
    }

    public void evaluateCuration() {
        drugCurator.reportUnusedCurationKeys();
        evidenceLevelCurator.reportUnusedCurationKeys();
    }

    @NotNull
    private static String formatDrugList(@NotNull List<String> drugList) {
        List<String> sortedDrugs = Lists.newArrayList(drugList);
        sortedDrugs.sort(Comparator.naturalOrder());

        StringJoiner joiner = new StringJoiner(" + ");
        for (String drug : sortedDrugs) {
            joiner.add(drug);
        }
        return joiner.toString();
    }

    @NotNull
    private Map<String, Set<String>> buildCancerTypeToDoidsMap(@Nullable String cancerType, @Nullable String doid) {
        Map<String, Set<String>> cancerTypeToDoidsMap = Maps.newHashMap();
        if (cancerType != null) {
            if (cancerType.contains(CANCER_TYPE_SEPARATOR)) {
                String[] parts = cancerType.split(CANCER_TYPE_SEPARATOR);
                for (String part : parts) {
                    // We always look up the DOIDs when there is aggregate cancer type information as the DOID in this case is unreliable.
                    cancerTypeToDoidsMap.put(part, lookupDoids(part));
                }
            } else if (doid != null) {
                cancerTypeToDoidsMap.put(cancerType, Sets.newHashSet(doid));
            } else {
                cancerTypeToDoidsMap.put(cancerType, lookupDoids(cancerType));
            }
        }
        return cancerTypeToDoidsMap;
    }

    @NotNull
    private Set<String> lookupDoids(@NotNull String cancerType) {
        Set<String> doids = missingDoidLookup.lookupDoidsForCancerType(cancerType);
        if (doids != null) {
            return doids;
        } else {
            LOGGER.warn("Could not resolve doids for VICC cancer type '{}'", cancerType);
            return Sets.newHashSet();
        }
    }

    @Nullable
    @VisibleForTesting
    static String reformatDrugLabels(@Nullable String drugLabels) {
        if (drugLabels == null) {
            return null;
        }

        String drugSeparator = ",";
        String[] parts = drugLabels.split(drugSeparator);
        StringJoiner joiner = new StringJoiner(drugSeparator);
        for (String part : parts) {
            joiner.add(reformatField(part));
        }
        return joiner.toString();
    }

    @Nullable
    @$VisibleForTesting
    static EvidenceLevel resolveLevel(@Nullable String evidenceLabel) {
        if (evidenceLabel == null) {
            return null;
        }

        EvidenceLevel level = EvidenceLevel.fromString(evidenceLabel);
        if (level == null) {
            LOGGER.warn("Could not resolve evidence label '{}'", evidenceLabel);
        }
        return level;
    }

    @Nullable
    private static String resolveCancerType(@Nullable Phenotype phenotype) {
        return phenotype != null ? phenotype.description() : null;
    }

    @Nullable
    private static String resolveDoid(@Nullable Phenotype phenotype) {
        if (phenotype != null) {
            PhenotypeType type = phenotype.type();
            if (type != null) {
                return extractDoid(type.id());
            }
        }

        return null;
    }

    @Nullable
    @VisibleForTesting
    static String extractDoid(@Nullable String doidString) {
        if (doidString == null) {
            return null;
        }

        String[] parts = doidString.split(":");
        if (parts.length == 2) {
            if (parts[0].equalsIgnoreCase("doid")) {
                return parts[1];
            } else {
                return null;
            }
        } else {
            LOGGER.warn("Unexpected Doid string: '{}'", doidString);
            return null;
        }
    }

    @Nullable
    @VisibleForTesting
    static EvidenceDirection resolveDirection(@Nullable String direction) {
        String effectiveDirection = reformatField(direction);
        if (effectiveDirection == null) {
            return null;
        }

        if (RESPONSIVE_DIRECTIONS.contains(effectiveDirection)) {
            return EvidenceDirection.RESPONSIVE;
        } else if (RESISTANT_DIRECTIONS.contains(effectiveDirection)) {
            return EvidenceDirection.RESISTANT;
        }

        if (!DIRECTIONS_TO_IGNORE.contains(effectiveDirection)) {
            LOGGER.warn("Could not resolve VICC direction '{}'", effectiveDirection);
        }
        return null;
    }

    @NotNull
    private static Set<String> resolveUrls(@Nullable EvidenceInfo info) {
        return info != null ? Sets.newHashSet(info.publications()) : Sets.newHashSet();
    }

    @Nullable
    @VisibleForTesting
    static String reformatField(@Nullable String field) {
        if (field == null) {
            return null;
        } else if (field.length() < 2) {
            return field.toUpperCase();
        } else {
            return field.substring(0, 1).toUpperCase() + field.substring(1).toLowerCase();
        }
    }

    @NotNull
    public static Knowledgebase fromViccSource(@NotNull ViccSource source) {
        switch (source) {
            case CIVIC:
                return Knowledgebase.VICC_CIVIC;
            case CGI:
                return Knowledgebase.VICC_CGI;
            case JAX:
                return Knowledgebase.VICC_JAX;
            case ONCOKB:
                return Knowledgebase.VICC_ONCOKB;
            default:
                throw new IllegalStateException("Source not supported by SERVE: " + source);
        }
    }
}
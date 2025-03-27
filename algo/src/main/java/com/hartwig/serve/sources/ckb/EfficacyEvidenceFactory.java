package com.hartwig.serve.sources.ckb;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.drug.DrugClass;
import com.hartwig.serve.ckb.datamodel.evidence.Evidence;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.DrugClassTreatmentApproach;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.TherapyTreatmentApproach;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.EvidenceDirection;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevel;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.efficacy.ImmutableEfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.ImmutableTreatment;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.sources.ckb.filter.CkbEvidenceFilterModel;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class EfficacyEvidenceFactory {

    private static final Logger LOGGER = LogManager.getLogger(EfficacyEvidenceFactory.class);

    private static final Set<String> RESPONSIVE_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> PREDICTED_RESPONSIVE_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> RESISTANT_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> PREDICTED_RESISTANT_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> NO_BENEFIT_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> DECREASED_RESPONSE_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> DIRECTIONS_TO_IGNORE = Sets.newHashSet();

    private static final Set<String> USABLE_EVIDENCE_TYPES = Sets.newHashSet();
    private static final Set<String> EVIDENCE_TYPES_TO_IGNORE = Sets.newHashSet();

    static {
        RESPONSIVE_DIRECTIONS.add("sensitive");
        PREDICTED_RESPONSIVE_DIRECTIONS.add("predicted - sensitive");

        RESISTANT_DIRECTIONS.add("resistant");
        PREDICTED_RESISTANT_DIRECTIONS.add("predicted - resistant");

        NO_BENEFIT_DIRECTIONS.add("no benefit");
        DECREASED_RESPONSE_DIRECTIONS.add("decreased response");

        DIRECTIONS_TO_IGNORE.add("unknown");
        DIRECTIONS_TO_IGNORE.add("not applicable");
        DIRECTIONS_TO_IGNORE.add("conflicting");
        DIRECTIONS_TO_IGNORE.add("not predictive");

        USABLE_EVIDENCE_TYPES.add("Actionable");

        EVIDENCE_TYPES_TO_IGNORE.add("Prognostic");
        EVIDENCE_TYPES_TO_IGNORE.add("Emerging");
        EVIDENCE_TYPES_TO_IGNORE.add("Risk Factor");
        EVIDENCE_TYPES_TO_IGNORE.add("Diagnostic");
    }

    @NotNull
    private final TreatmentApproachCurator curator;

    @NotNull
    private final CkbEvidenceFilterModel filterEvidence;

    public EfficacyEvidenceFactory(@NotNull TreatmentApproachCurator curator, @NotNull CkbEvidenceFilterModel filterEvidence) {
        this.curator = curator;
        this.filterEvidence = filterEvidence;
    }

    @NotNull
    public Set<EfficacyEvidence> create(@NotNull CkbEntry entry, @NotNull MolecularCriterium molecularCriterium,
            @NotNull String combinedSourceEvent, @NotNull String combinedSourceGenes) {
        Set<EfficacyEvidence> efficacyEvidences = Sets.newHashSet();

        for (Evidence evidence : evidencesWithUsableType(entry.evidences())) {
            EvidenceLevelDetails evidenceLevelDetails = resolveEvidenceLevelDetails(evidence.approvalStatus());
            EvidenceDirection direction = resolveDirection(evidence.responseType());
            EvidenceLevel level = resolveLevel(evidence.ampCapAscoEvidenceLevel(), evidenceLevelDetails, direction);
            Indication indication = ActionableFunctions.extractIndication(evidence.indication());
            int evidenceYear = extractEvidenceYear(entry.createDate(), evidence.references(), evidence.therapy());

            if (level != null && direction != null && indication != null && evidenceLevelDetails != null) {
                String treatment = evidence.therapy().therapyName();

                // TODO: Complex filters are not supported, if needed will be implemented in the future
                if (!filterEvidence.shouldFilterEvidence(treatment,
                        indication.applicableType().name(),
                        level,
                        combinedSourceGenes,
                        combinedSourceEvent)) {
                    Set<String> evidenceUrls = Sets.newHashSet();
                    for (Reference reference : evidence.references()) {
                        if (reference.url() != null) {
                            evidenceUrls.add(reference.url());
                        }
                    }

                    Set<String> treatmentApproachDrugClasses = evidence.drugTreatmentApproaches()
                            .stream()
                            .map(DrugClassTreatmentApproach::drugClass)
                            .map(DrugClass::drugClass)
                            .collect(Collectors.toSet());

                    Set<String> treatmentApproachTherapies = evidence.therapyTreatmentApproaches()
                            .stream()
                            .map(TherapyTreatmentApproach::therapy)
                            .map(Therapy::therapyName)
                            .collect(Collectors.toSet());

                    // TODO: implement
                    //                    String treatmentApproachString = String.join(",", sourceRelevantTreatmentApproaches);
                    //                    String treatmentApproachInterpret;
                    //                    if (sourceRelevantTreatmentApproaches.isEmpty()) {
                    //                        treatmentApproachInterpret = null;
                    //                    } else if (treatmentApproachString.endsWith(",")) {
                    //                        treatmentApproachInterpret = treatmentApproachString.substring(0, treatmentApproachString.length() - 1);
                    //                    } else {
                    //                        treatmentApproachInterpret = treatmentApproachString;
                    //                    }
                    //
                    //                    TreatmentApproachCurationEntryKey key = ImmutableTreatmentApproachCurationEntryKey.builder()
                    //                            .treatment(treatment)
                    //                            .treatmentApproach(treatmentApproachInterpret == null || treatmentApproachInterpret.isEmpty()
                    //                                    ? null
                    //                                    : treatmentApproachInterpret)
                    //                            .event(sourceGene + " " + entry.type())
                    //                            .direction(direction)
                    //                            .build();
                    //
                    //                    Set<String> curatedRelevantTreatmentApproaches = Sets.newHashSet(curator.isMatch(key));

                    efficacyEvidences.add(ImmutableEfficacyEvidence.builder()
                            .source(Knowledgebase.CKB)
                            .treatment(ImmutableTreatment.builder()
                                    .name(treatment)
                                    .treatmentApproachesDrugClass(treatmentApproachDrugClasses)
                                    .treatmentApproachesTherapy(treatmentApproachTherapies)
                                    .build())
                            .indication(indication)
                            .molecularCriterium(molecularCriterium)
                            .efficacyDescription(evidence.efficacyEvidence())
                            .evidenceLevel(level)
                            .evidenceLevelDetails(evidenceLevelDetails)
                            .evidenceDirection(direction)
                            .evidenceYear(evidenceYear)
                            .urls(evidenceUrls)
                            .build());
                }
            }
        }
        return efficacyEvidences;
    }

    @NotNull
    private static List<Evidence> evidencesWithUsableType(@NotNull List<Evidence> evidences) {
        List<Evidence> filtered = Lists.newArrayList();
        for (Evidence evidence : evidences) {
            if (hasUsableEvidenceType(evidence.evidenceType())) {
                filtered.add(evidence);
            }
        }
        return filtered;
    }

    @VisibleForTesting
    static boolean hasUsableEvidenceType(@NotNull String evidenceType) {
        if (USABLE_EVIDENCE_TYPES.contains(evidenceType)) {
            return true;
        } else {
            if (!EVIDENCE_TYPES_TO_IGNORE.contains(evidenceType)) {
                LOGGER.warn("Unrecognized CKB evidence type: '{}'", evidenceType);
            }
            return false;
        }
    }

    @Nullable
    @VisibleForTesting
    static EvidenceLevel resolveLevel(@Nullable String evidenceLabel, @Nullable EvidenceLevelDetails evidenceLevelDetails,
            @Nullable EvidenceDirection direction) {
        if (direction == EvidenceDirection.DECREASED_RESPONSE && (evidenceLabel == null || evidenceLabel.equals("NA"))) {
            if (evidenceLevelDetails == EvidenceLevelDetails.FDA_APPROVED
                    || evidenceLevelDetails == EvidenceLevelDetails.FDA_CONTRAINDICATED
                    || evidenceLevelDetails == EvidenceLevelDetails.GUIDELINE) {
                return EvidenceLevel.A;
            } else if (evidenceLevelDetails == EvidenceLevelDetails.CLINICAL_STUDY) {
                return EvidenceLevel.C;
            } else if (evidenceLevelDetails == EvidenceLevelDetails.PRECLINICAL
                    || evidenceLevelDetails == EvidenceLevelDetails.CASE_REPORTS_SERIES) {
                return EvidenceLevel.D;
            }
        }

        if (evidenceLabel == null || evidenceLabel.equals("NA")) {
            return null;
        }

        EvidenceLevel level = EvidenceLevel.fromString(evidenceLabel);
        if (level == null) {
            LOGGER.warn("Could not resolve CKB evidence level: '{}'", evidenceLabel);
        }
        return level;
    }

    @Nullable
    @VisibleForTesting
    static EvidenceLevelDetails resolveEvidenceLevelDetails(@Nullable String approvalStatus) {
        if (approvalStatus == null) {
            return null;
        }
        approvalStatus = approvalStatus.toLowerCase();
        if (approvalStatus.contains("preclinical")) {
            return EvidenceLevelDetails.PRECLINICAL;
        } else if (approvalStatus.contains("case report")) {
            return EvidenceLevelDetails.CASE_REPORTS_SERIES;
        } else if (approvalStatus.contains("clinical study")) {
            return EvidenceLevelDetails.CLINICAL_STUDY;
        } else if (approvalStatus.contains("phase")) {
            return resolvePhase(approvalStatus);
        } else if (approvalStatus.contains("guideline")) {
            return EvidenceLevelDetails.GUIDELINE;
        } else if (approvalStatus.contains("fda approved")) {
            return EvidenceLevelDetails.FDA_APPROVED;
        } else if (approvalStatus.contains("fda contraindicated")) {
            return EvidenceLevelDetails.FDA_CONTRAINDICATED;
        } else {
            LOGGER.warn("Could not resolve CKB evidence level details (approvalStatus) '{}'", approvalStatus);
            return EvidenceLevelDetails.UNKNOWN;
        }
    }

    private static EvidenceLevelDetails resolvePhase(final String approvalStatus) {
        switch (approvalStatus.toLowerCase()) {
            case "phase 0":
                return EvidenceLevelDetails.PHASE_0;
            case "phase i":
                return EvidenceLevelDetails.PHASE_I;
            case "phase ib/ii":
                return EvidenceLevelDetails.PHASE_IB_II;
            case "phase ii":
                return EvidenceLevelDetails.PHASE_II;
            case "phase iii":
                return EvidenceLevelDetails.PHASE_III;
            default:
                throw new IllegalArgumentException(String.format("Input [%s] could not be resolved to a valid phase.", approvalStatus));
        }
    }

    @Nullable
    @VisibleForTesting
    static EvidenceDirection resolveDirection(@Nullable String direction) {
        if (direction == null) {
            return null;
        }

        if (RESPONSIVE_DIRECTIONS.contains(direction)) {
            return EvidenceDirection.RESPONSIVE;
        } else if (PREDICTED_RESPONSIVE_DIRECTIONS.contains(direction)) {
            return EvidenceDirection.PREDICTED_RESPONSIVE;
        } else if (RESISTANT_DIRECTIONS.contains(direction)) {
            return EvidenceDirection.RESISTANT;
        } else if (PREDICTED_RESISTANT_DIRECTIONS.contains(direction)) {
            return EvidenceDirection.PREDICTED_RESISTANT;
        } else if (NO_BENEFIT_DIRECTIONS.contains(direction)) {
            return EvidenceDirection.NO_BENEFIT;
        } else if (DECREASED_RESPONSE_DIRECTIONS.contains(direction)) {
            return EvidenceDirection.DECREASED_RESPONSE;
        }

        if (!DIRECTIONS_TO_IGNORE.contains(direction)) {
            LOGGER.warn("Could not resolve CKB direction '{}'", direction);
        }
        return null;
    }

    @VisibleForTesting
    static int extractEvidenceYear(@NotNull LocalDate entryDate, @NotNull List<Reference> references, @Nullable Therapy therapy) {
        Optional<Integer> mostRecentYear = references.stream()
                .map(Reference::year)
                .filter(year -> year != null && !year.isEmpty() && !year.equals("0"))
                .map(Integer::parseInt)
                .max(Comparator.naturalOrder());

        if (mostRecentYear.isPresent()) {
            return mostRecentYear.get();
        }

        if (therapy != null) {
            return entryDate.isAfter(therapy.createDate()) ? entryDate.getYear() : therapy.createDate().getYear();
        } else {
            return entryDate.getYear();
        }
    }

    @NotNull
    @VisibleForTesting
    static String toUrlString(@NotNull String string) {
        return string.replaceAll(" ", "+");
    }
}
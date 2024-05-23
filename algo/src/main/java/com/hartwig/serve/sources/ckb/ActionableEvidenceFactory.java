package com.hartwig.serve.sources.ckb;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.evidence.Evidence;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.DrugClassTreatmentApproach;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.RelevantTreatmentApproaches;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.TherapyTreatmentApproach;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.sources.ckb.blacklist.CkbEvidenceBlacklistModel;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ActionableEvidenceFactory implements ActionableEntryFactory {

    private static final Logger LOGGER = LogManager.getLogger(ActionableEvidenceFactory.class);

    private static final Set<String> RESPONSIVE_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> PREDICTED_RESPONSIVE_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> RESISTANT_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> PREDICTED_RESISTANT_DIRECTIONS = Sets.newHashSet();
    private static final Set<String> DIRECTIONS_TO_IGNORE = Sets.newHashSet();

    private static final Set<String> USABLE_EVIDENCE_TYPES = Sets.newHashSet();
    private static final Set<String> EVIDENCE_TYPES_TO_IGNORE = Sets.newHashSet();

    static {
        RESPONSIVE_DIRECTIONS.add("sensitive");
        PREDICTED_RESPONSIVE_DIRECTIONS.add("predicted - sensitive");

        RESISTANT_DIRECTIONS.add("resistant");
        PREDICTED_RESISTANT_DIRECTIONS.add("predicted - resistant");

        DIRECTIONS_TO_IGNORE.add("unknown");
        DIRECTIONS_TO_IGNORE.add("not applicable");
        DIRECTIONS_TO_IGNORE.add("conflicting");
        DIRECTIONS_TO_IGNORE.add("no benefit");
        DIRECTIONS_TO_IGNORE.add("not predictive");
        DIRECTIONS_TO_IGNORE.add("decreased response");

        USABLE_EVIDENCE_TYPES.add("Actionable");

        EVIDENCE_TYPES_TO_IGNORE.add("Prognostic");
        EVIDENCE_TYPES_TO_IGNORE.add("Emerging");
        EVIDENCE_TYPES_TO_IGNORE.add("Risk Factor");
        EVIDENCE_TYPES_TO_IGNORE.add("Diagnostic");
    }

    @NotNull
    private final TreatmentApproachCurator curator;

    @NotNull
    private final CkbEvidenceBlacklistModel blacklistEvidence;

    public ActionableEvidenceFactory(@NotNull TreatmentApproachCurator curator, @NotNull CkbEvidenceBlacklistModel blacklistEvidence) {
        this.curator = curator;
        this.blacklistEvidence = blacklistEvidence;
    }

    @NotNull
    @Override
    public Set<ActionableEntry> create(@NotNull CkbEntry entry, @NotNull String sourceEvent, @NotNull String sourceGene) {
        Set<ActionableEntry> actionableEntries = Sets.newHashSet();

        for (Evidence evidence : evidencesWithUsableType(entry.evidences())) {
            EvidenceLevel level = resolveLevel(evidence.ampCapAscoEvidenceLevel());
            EvidenceDirection direction = resolveDirection(evidence.responseType());
            CancerTypeExtraction cancerTypeExtraction = ActionableFunctions.extractCancerTypeDetails(evidence.indication());

            if (level != null && direction != null && cancerTypeExtraction != null) {
                String treatment = evidence.therapy().therapyName();

                if (!blacklistEvidence.isBlacklistEvidence(treatment,
                        cancerTypeExtraction.applicableCancerType().name(),
                        level,
                        sourceGene,
                        sourceEvent)) {
                    Set<String> evidenceUrls = Sets.newHashSet();
                    for (Reference reference : evidence.references()) {
                        if (reference.url() != null) {
                            evidenceUrls.add(reference.url());
                        }
                    }

                    Set<String> sourceUrls = Sets.newHashSet();
                    sourceUrls.add("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=" + entry.profileId());

                    Set<String> treatmentApproachDrugClasses = Sets.newHashSet();
                    Set<String> treatmentApproachTherapies = Sets.newHashSet();

                    for (RelevantTreatmentApproaches relevantTreatmentApproaches : evidence.relevantTreatmentApproaches()) {

                        TherapyTreatmentApproach therapyTreatmentApproach = extractOptionalTherapyTreatmentApproach(relevantTreatmentApproaches);
                        DrugClassTreatmentApproach drugClassTreatmentApproach = extractOptionalDrugClassTreatmentApproach(relevantTreatmentApproaches);

                        // If drugClass contains data then therapy is null. When therapy contains data then drugClass is null
                        if (drugClassTreatmentApproach.drugClass() != null) {
                            treatmentApproachDrugClasses.add(drugClassTreatmentApproach.drugClass().drugClass());
                        }

                        if (therapyTreatmentApproach.therapy() != null) {
                            treatmentApproachTherapies.add(therapyTreatmentApproach.therapy().therapyName());
                        }
                    }
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

                    actionableEntries.add(ImmutableActionableEntry.builder()
                            .source(Knowledgebase.CKB_EVIDENCE)
                            .sourceEvent(sourceEvent)
                            .sourceUrls(sourceUrls)
                            .intervention(ImmutableTreatment.builder()
                                    .name(treatment)
                                    .treatmentApproachesDrugClass(treatmentApproachDrugClasses)
                                    .treatmentApproachesTherapy(treatmentApproachTherapies)
                                    .build())
                            .applicableCancerType(cancerTypeExtraction.applicableCancerType())
                            .blacklistCancerTypes(cancerTypeExtraction.blacklistedCancerTypes())
                            .level(level)
                            .direction(direction)
                            .evidenceUrls(evidenceUrls)
                            .build());
                }
            }
        }
        return actionableEntries;
    }

    @Nullable
    private static TherapyTreatmentApproach extractOptionalTherapyTreatmentApproach(
            @NotNull RelevantTreatmentApproaches treatmentApproaches) {
        TherapyTreatmentApproach therapyTreatmentApproach = null;
        if (treatmentApproaches.treatmentApproachIntervation() instanceof TherapyTreatmentApproach) {
            therapyTreatmentApproach = (TherapyTreatmentApproach) treatmentApproaches.treatmentApproachIntervation();
        }
        return therapyTreatmentApproach;
    }

    @Nullable
    private static DrugClassTreatmentApproach extractOptionalDrugClassTreatmentApproach(
            @NotNull RelevantTreatmentApproaches treatmentApproaches) {
        DrugClassTreatmentApproach drugClassTreatmentApproach = null;
        if (treatmentApproaches.treatmentApproachIntervation() instanceof DrugClassTreatmentApproach) {
            drugClassTreatmentApproach = (DrugClassTreatmentApproach) treatmentApproaches.treatmentApproachIntervation();
        }
        return drugClassTreatmentApproach;
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
    static EvidenceLevel resolveLevel(@Nullable String evidenceLabel) {
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
        }

        if (!DIRECTIONS_TO_IGNORE.contains(direction)) {
            LOGGER.warn("Could not resolve CKB direction '{}'", direction);
        }
        return null;
    }

    @NotNull
    @VisibleForTesting
    static String toUrlString(@NotNull String string) {
        return string.replaceAll(" ", "+");
    }
}
package com.hartwig.serve.sources.iclusion;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.datamodel.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.ImmutableCountry;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.datamodel.IclusionTumorLocation;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class ActionableTrialFactory {

    private static final Logger LOGGER = LogManager.getLogger(ActionableTrialFactory.class);

    @NotNull
    private final DoidLookup missingDoidLookup;

    public ActionableTrialFactory(@NotNull final DoidLookup missingDoidLookup) {
        this.missingDoidLookup = missingDoidLookup;
    }

    @NotNull
    public List<ActionableTrial> toActionableTrials(@NotNull IclusionTrial trial, @NotNull String sourceEvent) {
        ImmutableActionableTrial.Builder actionableBuilder = toActionableBuilder(trial, sourceEvent);

        Set<CancerType> blacklistCancerTypes = determineBlacklistedCancerTypes(trial);

        List<ActionableTrial> actionableTrials = Lists.newArrayList();
        for (IclusionTumorLocation tumorLocation : trial.tumorLocations()) {
            for (String doid : determineDoids(tumorLocation)) {
                String doidCurated = curateDoid(doid);
                CancerType applicable = ImmutableCancerType.builder().name(tumorLocation.primaryTumorLocation()).doid(doidCurated).build();

                Set<CancerType> finalBlacklistedCancerTypes = Sets.newHashSet(blacklistCancerTypes.iterator());
                if (doidCurated.equals(CancerTypeConstants.CANCER_DOID)) {
                    finalBlacklistedCancerTypes.add(CancerTypeConstants.LEUKEMIA_TYPE);
                    finalBlacklistedCancerTypes.add(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE);
                    finalBlacklistedCancerTypes.add(CancerTypeConstants.BONE_MARROW_TYPE);
                }

                actionableTrials.add(actionableBuilder.applicableCancerType(applicable)
                        .blacklistCancerTypes(finalBlacklistedCancerTypes)
                        .build());
            }
        }
        return actionableTrials;
    }

    @NotNull
    private static ImmutableActionableTrial.Builder toActionableBuilder(@NotNull IclusionTrial trial, @NotNull String sourceEvent) {
        return ImmutableActionableTrial.builder()
                .source(Knowledgebase.ICLUSION)
                .sourceEvent(sourceEvent)
                .entryDate(LocalDate.EPOCH)
                .sourceUrls(Sets.newHashSet("https://www.trial-eye.com/hmf/" + trial.id()))
                .intervention(ImmutableClinicalTrial.builder()
                        .nctId(trial.nct())
                        .title("")
                        .acronym(trial.acronym())
                        .genderCriterium(null)
                        .countries(Sets.newHashSet(ImmutableCountry.builder()
                                .countryName("The Netherlands")
                                .hospitalsPerCity(Map.of("Unknown", Set.of("Unknown")))
                                .build()))
                        .therapyNames(Sets.newHashSet())
                        .build())
                .efficacyDescription(Strings.EMPTY)
                .evidenceYear(2024)
                .evidenceLevel(EvidenceLevel.B)
                .evidenceLevelDetails(EvidenceLevelDetails.UNKNOWN)
                .direction(EvidenceDirection.RESPONSIVE)
                .evidenceUrls(Sets.newHashSet());
    }

    @NotNull
    private Set<CancerType> determineBlacklistedCancerTypes(@NotNull IclusionTrial trial) {
        Set<CancerType> blacklistedCancerTypes = Sets.newHashSet();
        for (IclusionTumorLocation blacklistTumorLocation : trial.blacklistedTumorLocations()) {
            for (String doid : determineDoids(blacklistTumorLocation)) {
                blacklistedCancerTypes.add(ImmutableCancerType.builder()
                        .name(blacklistTumorLocation.primaryTumorLocation())
                        .doid(curateDoid(doid))
                        .build());
            }
        }

        return blacklistedCancerTypes;
    }

    @NotNull
    private List<String> determineDoids(@NotNull IclusionTumorLocation tumorLocation) {
        if (!tumorLocation.doids().isEmpty()) {
            return tumorLocation.doids();
        }

        Set<String> manualDoids = missingDoidLookup.lookupDoidsForCancerType(tumorLocation.primaryTumorLocation());
        if (manualDoids == null) {
            LOGGER.warn("No doids could be derived for iClusion primary tumor location '{}'", tumorLocation.primaryTumorLocation());
            manualDoids = Sets.newHashSet();
        } else {
            LOGGER.debug("Resolved doids to '{}' for iClusion primary tumor location '{}'",
                    manualDoids,
                    tumorLocation.primaryTumorLocation());
        }

        return Lists.newArrayList(manualDoids.iterator());
    }

    @NotNull
    @VisibleForTesting
    static String curateDoid(@NotNull String doid) {
        if (doid.equals(CancerTypeConstants.ORGAN_SYSTEM_CANCER_DOID)) {
            return CancerTypeConstants.CANCER_DOID;
        } else if (doid.equals("MESH: D009382") || doid.equals("UNKNOWN")) {
            return CancerTypeConstants.ORGAN_SYSTEM_CANCER_DOID;
        } else {
            return doid;
        }
    }
}
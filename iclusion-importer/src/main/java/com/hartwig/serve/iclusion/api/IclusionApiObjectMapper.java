package com.hartwig.serve.iclusion.api;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.iclusion.datamodel.IclusionMutation;
import com.hartwig.serve.iclusion.datamodel.IclusionMutationCondition;
import com.hartwig.serve.iclusion.datamodel.IclusionMutationLogicType;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.datamodel.IclusionTumorLocation;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionMutation;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionMutationCondition;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionTrial;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionTumorLocation;
import com.hartwig.serve.iclusion.io.IclusionTrialFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class IclusionApiObjectMapper {

    private static final Logger LOGGER = LogManager.getLogger(IclusionApiObjectMapper.class);

    private IclusionApiObjectMapper() {
    }

    @NotNull
    static List<IclusionTrial> fromApiObjects(@NotNull List<IclusionObjectStudy> studies,
            @NotNull List<IclusionObjectIndication> indications, @NotNull List<IclusionObjectGene> genes,
            @NotNull List<IclusionObjectVariant> variants) {
        List<IclusionTrial> trials = Lists.newArrayList();

        for (IclusionObjectStudy study : studies) {
            // We loose MAIN_FIELD_DELIMITER in titles for good but that is considered acceptable compromise.
            String title = study.title;
            if (title.contains(IclusionTrialFile.MAIN_FIELD_DELIMITER)) {
                LOGGER.info("Replacing trial file delimiters '{}' with a space from study with acronym '{}'",
                        IclusionTrialFile.MAIN_FIELD_DELIMITER,
                        study.acronym);
                title = title.replace(IclusionTrialFile.MAIN_FIELD_DELIMITER, " ");
            }

            if (title.contains("\n")) {
                LOGGER.info("Replacing line ends with a space from study with acronym '{}'", study.acronym);
                title = title.replace("\n", " ");
            }

            // We loose NULL nct and ipn fields forever, but that is considered acceptable
            // (no functional difference between empty nct and null nct)
            trials.add(ImmutableIclusionTrial.builder()
                    .id(study.id)
                    .acronym(study.acronym)
                    .title(title)
                    .eudra(study.eudra)
                    .nct(nullToEmpty(study.nct))
                    .ipn(nullToEmpty(study.ipn))
                    .ccmo(nullToEmpty(study.ccmo))
                    .tumorLocations(buildTumorLocations(indications, study.indicationIds))
                    .blacklistedTumorLocations(buildTumorLocations(indications, study.blacklistIndicationIds))
                    .mutationConditions(buildMutationConditions(genes, variants, study.mutationConditions))
                    .build());
        }

        return trials;
    }

    @NotNull
    private static List<IclusionTumorLocation> buildTumorLocations(@NotNull Iterable<IclusionObjectIndication> indications,
            @NotNull Iterable<String> indicationIds) {
        List<IclusionTumorLocation> tumorLocations = Lists.newArrayList();
        for (String id : indicationIds) {
            IclusionObjectIndication indication = findIndicationById(indications, id);
            if (indication != null) {
                List<String> doids = Lists.newArrayList();
                if (indication.doid != null) {
                    doids.add(indication.doid);
                }

                if (indication.doid2 != null) {
                    doids.add(indication.doid2);
                }

                tumorLocations.add(ImmutableIclusionTumorLocation.builder()
                        .primaryTumorLocation(indication.indicationNameFull)
                        .doids(doids)
                        .build());
            } else {
                LOGGER.warn("Could not find indication with ID '{}' in list of indications!", id);
            }
        }

        return tumorLocations;
    }

    @Nullable
    private static IclusionObjectIndication findIndicationById(@NotNull Iterable<IclusionObjectIndication> indications,
            @NotNull String id) {
        for (IclusionObjectIndication indication : indications) {
            if (indication.id.equals(id)) {
                return indication;
            }
        }
        return null;
    }

    @NotNull
    private static List<IclusionMutationCondition> buildMutationConditions(@NotNull Iterable<IclusionObjectGene> genes,
            @NotNull Iterable<IclusionObjectVariant> variants,
            @NotNull Iterable<IclusionObjectMutationCondition> mutationConditionObjects) {
        List<IclusionMutationCondition> mutationConditions = Lists.newArrayList();
        for (IclusionObjectMutationCondition mutationConditionObject : mutationConditionObjects) {
            List<IclusionMutation> mutations = Lists.newArrayList();
            for (IclusionObjectMutation mutationObject : mutationConditionObject.mutations) {
                IclusionObjectGene gene = findGeneById(genes, mutationObject.geneId);
                if (gene == null) {
                    LOGGER.warn("Could not find gene with ID '{}' in list of genes!", mutationObject.geneId);
                }

                IclusionObjectVariant variant = findVariantById(variants, mutationObject.variantId);
                if (variant == null) {
                    LOGGER.warn("Could not find variant with ID '{}' in list of variants!", mutationObject.variantId);
                }

                if (gene != null && variant != null) {
                    boolean negation = mutationObject.negation.equals("1");
                    mutations.add(ImmutableIclusionMutation.builder()
                            .name(variant.variantName)
                            .gene(gene.geneName)
                            .negation(negation)
                            .build());
                }
            }
            mutationConditions.add(ImmutableIclusionMutationCondition.builder()
                    .mutations(mutations)
                    .logicType(toLogicType(mutationConditionObject.logicType))
                    .build());
        }

        return mutationConditions;
    }

    @NotNull
    @VisibleForTesting
    static IclusionMutationLogicType toLogicType(@Nullable String logicType) {
        if (logicType == null) {
            return IclusionMutationLogicType.UNKNOWN;
        }

        String formattedLogicType = logicType.toLowerCase().trim();
        switch (formattedLogicType) {
            case "or":
                return IclusionMutationLogicType.OR;
            case "and":
                return IclusionMutationLogicType.AND;
            default: {
                LOGGER.warn("Could not resolve mutation logic type '{}'", logicType);
                return IclusionMutationLogicType.UNKNOWN;
            }
        }
    }

    @Nullable
    private static IclusionObjectGene findGeneById(@NotNull Iterable<IclusionObjectGene> genes, @NotNull String id) {
        for (IclusionObjectGene gene : genes) {
            if (gene.id.equals(id)) {
                return gene;
            }
        }
        return null;
    }

    @Nullable
    private static IclusionObjectVariant findVariantById(@NotNull Iterable<IclusionObjectVariant> variants, @NotNull String id) {
        for (IclusionObjectVariant variant : variants) {
            if (variant.id.equals(id)) {
                return variant;
            }
        }
        return null;
    }
}

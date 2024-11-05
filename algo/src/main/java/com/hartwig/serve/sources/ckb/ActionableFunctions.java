package com.hartwig.serve.sources.ckb;

import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableIndication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ActionableFunctions {

    private static final Logger LOGGER = LogManager.getLogger(ActionableFunctions.class);

    private ActionableFunctions() {
    }

    @Nullable
    public static com.hartwig.serve.datamodel.Indication extractIndication(@NotNull Indication indication) {
        String[] sourceDoidValues = splitSourceDoidString(indication.termId());

        if (sourceDoidValues == null) {
            return null;
        }

        if (sourceDoidValues.length != 2) {
            throw new IllegalStateException("Unexpected termId" + indication.termId() + " for indication " + indication.name());
        }

        ImmutableCancerType.Builder applicableTypeBuilder = ImmutableCancerType.builder().name(indication.name());
        Set<CancerType> nonApplicableSubTypes = Sets.newHashSet();

        String source = sourceDoidValues[0];
        String id = sourceDoidValues[1];
        if (source.equalsIgnoreCase("doid")) {
            applicableTypeBuilder.doid(id);
        } else if (source.equalsIgnoreCase("jax")) {
            switch (id) {
                case CancerTypeConstants.CKB_ADVANCED_SOLID_TUMOR: {
                    applicableTypeBuilder.doid(CancerTypeConstants.CANCER_DOID);
                    nonApplicableSubTypes.add(CancerTypeConstants.LEUKEMIA_TYPE);
                    nonApplicableSubTypes.add(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE);
                    nonApplicableSubTypes.add(CancerTypeConstants.BONE_MARROW_TYPE);
                    break;
                }
                case CancerTypeConstants.CKB_CANCER_OF_UNKNOWN_PRIMARY: {
                    applicableTypeBuilder.doid(CancerTypeConstants.CANCER_DOID);
                    break;
                }
                case CancerTypeConstants.CKB_CARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableTypeBuilder.doid(CancerTypeConstants.CARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                case CancerTypeConstants.CKB_ADENOCARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableTypeBuilder.doid(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                case CancerTypeConstants.CKB_SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableTypeBuilder.doid(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                default: {
                    // CKB uses 10000005 for configuring "Not a cancer". We can ignore these.
                    if (!id.equals(CancerTypeConstants.CKB_NOT_CANCER)) {
                        LOGGER.warn("Unexpected DOID string annotated by CKB: '{}'", source + ":" + id);
                    }
                    return null;
                }
            }
        } else {
            LOGGER.warn("Unexpected source '{}'", source);
            return null;
        }

        return ImmutableIndication.builder()
                .applicableType(applicableTypeBuilder.build())
                .nonApplicableSubTypes(nonApplicableSubTypes)
                .build();
    }

    @Nullable
    @VisibleForTesting
    static String[] splitSourceDoidString(@Nullable String doidString) {
        if (doidString == null) {
            return null;
        }

        String[] parts = doidString.split(":");
        if (parts.length == 2) {
            String source = parts[0];
            if (source.equalsIgnoreCase("doid") || source.equalsIgnoreCase("jax")) {
                return parts;
            } else {
                LOGGER.warn("Unexpected length of doid string '{}'", doidString);
                return null;
            }
        } else {
            LOGGER.warn("Unexpected DOID string in CKB: '{}'", doidString);
            return null;
        }
    }
}

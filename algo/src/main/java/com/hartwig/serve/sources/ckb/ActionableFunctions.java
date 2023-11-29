package com.hartwig.serve.sources.ckb;

import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.ImmutableCancerType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ActionableFunctions {

    private static final Logger LOGGER = LogManager.getLogger(ActionableFunctions.class);

    @Nullable
    static CancerTypeExtraction extractCancerTypeDetails(@NotNull Indication indication) {
        String[] sourceCancerTypeDetails = extractSourceCancerTypeId(indication.termId());

        if (sourceCancerTypeDetails == null) {
            return null;
        }

        ImmutableCancerType.Builder applicableCancerTypeBuilder = ImmutableCancerType.builder().name(indication.name());
        Set<CancerType> blacklistedCancerTypes = Sets.newHashSet();

        assert sourceCancerTypeDetails.length == 2;
        String source = sourceCancerTypeDetails[0];
        String id = sourceCancerTypeDetails[1];
        if (source.equalsIgnoreCase("doid")) {
            applicableCancerTypeBuilder.doid(id);
        } else if (source.equalsIgnoreCase("jax")) {
            switch (id) {
                case CancerTypeConstants.JAX_ADVANCED_SOLID_TUMOR: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.CANCER_DOID);
                    blacklistedCancerTypes.add(CancerTypeConstants.LEUKEMIA_TYPE);
                    blacklistedCancerTypes.add(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE);
                    blacklistedCancerTypes.add(CancerTypeConstants.BONE_MARROW_TYPE);
                    break;
                }
                case CancerTypeConstants.JAX_CANCER_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.CANCER_DOID);
                    break;
                }
                case CancerTypeConstants.JAX_CARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.CARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                case CancerTypeConstants.JAX_ADENOCARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                case CancerTypeConstants.JAX_SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                default: {
                    // CKB uses 10000005 for configuring "Not a cancer". We can ignore these.
                    if (!id.equals(CancerTypeConstants.JAX_NOT_CANCER)) {
                        LOGGER.warn("Unexpected DOID string annotated by CKB: '{}'", source + ":" + id);
                    }
                    return null;
                }
            }
        } else {
            LOGGER.warn("Unexpected source '{}'", source);
            return null;
        }

        return new CancerTypeExtraction(applicableCancerTypeBuilder.build(), blacklistedCancerTypes);
    }

    @Nullable
    @VisibleForTesting
    static String[] extractSourceCancerTypeId(@Nullable String doidString) {
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

    static class CancerTypeExtraction {

        @NotNull
        private final CancerType applicableCancerType;
        @NotNull
        private final Set<CancerType> blacklistedCancerTypes;

        public CancerTypeExtraction(@NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistedCancerTypes) {
            this.applicableCancerType = applicableCancerType;
            this.blacklistedCancerTypes = blacklistedCancerTypes;
        }

        @NotNull
        public CancerType applicableCancerType() {
            return applicableCancerType;
        }

        @NotNull
        public Set<CancerType> blacklistedCancerTypes() {
            return blacklistedCancerTypes;
        }
    }
}

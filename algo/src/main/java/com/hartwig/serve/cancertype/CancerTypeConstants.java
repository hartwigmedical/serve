package com.hartwig.serve.cancertype;

import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.ImmutableCancerType;

public final class CancerTypeConstants {

    public static final String CANCER_DOID = "162";
    public static final String ORGAN_SYSTEM_CANCER_DOID = "0050686";
    public static final String CARCINOMA_OF_UNKNOWN_PRIMARY = "305";
    public static final String SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY = "1749";
    public static final String ADENOCARCINOMA_OF_UNKNOWN_PRIMARY = "299";

    // JAX defined some cancer types in a topology other than doid
    public static final String JAX_ADVANCED_SOLID_TUMORS = "10000003";
    public static final String JAX_NOT_CANCER = "10000005";
    public static final String JAX_CANCER_OF_UNKNOWN_PRIMARY = "10000006";
    public static final String JAX_CARCINOMA_OF_UNKNOWN_PRIMARY = "10000007";
    public static final String JAX_ADENOCARCINOMA_OF_UNKNOWN_PRIMARY = "10000008";
    public static final String JAX_SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY = "10000009";

    // Cancer types which should be blacklisted for solid tumors
    public static final CancerType LEUKEMIA_TYPE = ImmutableCancerType.builder().name("Leukemia").doid("1240").build();
    public static final CancerType REFRACTORY_HEMATOLOGIC_TYPE =
            ImmutableCancerType.builder().name("Refractory hematologic cancer").doid("712").build();
    public static final CancerType BONE_MARROW_TYPE = ImmutableCancerType.builder().name("Bone marrow cancer").doid("4960").build();

    private CancerTypeConstants() {
    }
}

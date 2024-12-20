package com.hartwig.serve.cancertype;

import com.hartwig.serve.datamodel.common.CancerType;
import com.hartwig.serve.datamodel.common.ImmutableCancerType;

public final class CancerTypeConstants {

    public static final String CANCER_DOID = "162";
    public static final String CARCINOMA_OF_UNKNOWN_PRIMARY = "305";
    public static final String SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY = "1749";
    public static final String ADENOCARCINOMA_OF_UNKNOWN_PRIMARY = "299";

    // CKB defines some cancer types in a topology other than doid
    public static final String CKB_ADVANCED_SOLID_TUMOR = "10000003";
    public static final String CKB_NOT_CANCER = "10000005";
    public static final String CKB_CANCER_OF_UNKNOWN_PRIMARY = "10000006";
    public static final String CKB_CARCINOMA_OF_UNKNOWN_PRIMARY = "10000007";
    public static final String CKB_ADENOCARCINOMA_OF_UNKNOWN_PRIMARY = "10000008";
    public static final String CKB_SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY = "10000009";

    // Cancer types which should be considered non-applicable for solid tumors
    public static final CancerType LEUKEMIA_TYPE = ImmutableCancerType.builder().name("Leukemia").doid("1240").build();
    public static final CancerType REFRACTORY_HEMATOLOGIC_TYPE =
            ImmutableCancerType.builder().name("Refractory hematologic cancer").doid("712").build();
    public static final CancerType BONE_MARROW_TYPE = ImmutableCancerType.builder().name("Bone marrow cancer").doid("4960").build();

    private CancerTypeConstants() {
    }
}

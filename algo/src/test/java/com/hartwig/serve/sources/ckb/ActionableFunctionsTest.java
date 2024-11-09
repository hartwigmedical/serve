package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.datamodel.CancerType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableFunctionsTest {

    @Test
    public void canExtractCancerTypeDetails() {
        assertNull(ActionableFunctions.extractIndication(CkbTestFactory.createIndication("test", "JAX:not a doid")));

        assertEquals("0060463", extractDoidForApplicableCancerType("DOID:0060463"));
        assertEquals(CancerTypeConstants.CANCER_DOID, extractDoidForApplicableCancerType("JAX:10000003"));
        assertEquals(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY, extractDoidForApplicableCancerType("JAX:10000009"));
        assertEquals(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY, extractDoidForApplicableCancerType("JAX:10000008"));
        assertNull(ActionableFunctions.extractIndication(CkbTestFactory.createIndication("test", "JAX:10000004")));

        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                CancerTypeConstants.BONE_MARROW_TYPE,
                CancerTypeConstants.LEUKEMIA_TYPE), extractExcludedSubTypes("JAX:10000003"));
        assertTrue(extractExcludedSubTypes("JAX:10000009").isEmpty());
    }

    @Test
    public void canSplitSourceDoidString() {
        assertNull(ActionableFunctions.splitSourceDoidString(null));
        assertNull(ActionableFunctions.splitSourceDoidString("not a doid"));

        assertNotNull(ActionableFunctions.splitSourceDoidString("DOID:0060463"));
        assertEquals("0060463", ActionableFunctions.splitSourceDoidString("DOID:0060463")[1]);
        assertEquals("10000003", ActionableFunctions.splitSourceDoidString("JAX:10000003")[1]);
    }

    @NotNull
    private static String extractDoidForApplicableCancerType(@NotNull String termId) {
        return ActionableFunctions.extractIndication(CkbTestFactory.createIndication("test", termId)).applicableType().doid();
    }

    @NotNull
    private static Set<CancerType> extractExcludedSubTypes(@NotNull String termId) {
        return ActionableFunctions.extractIndication(CkbTestFactory.createIndication("test", termId)).excludedSubTypes();
    }
}

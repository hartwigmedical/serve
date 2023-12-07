package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableFunctionsTest {

    @Test
    public void canExtractSourceCancerTypeID() {
        assertNull(ActionableFunctions.extractSourceCancerTypeDetails(null));
        assertNull(ActionableFunctions.extractSourceCancerTypeDetails("not a doid"));

        assertNotNull(ActionableFunctions.extractSourceCancerTypeDetails("DOID:0060463"));
        assertEquals("0060463", ActionableFunctions.extractSourceCancerTypeDetails("DOID:0060463")[1]);
        assertEquals("10000003", ActionableFunctions.extractSourceCancerTypeDetails("JAX:10000003")[1]);
    }

    @Test
    public void canExtractCancerTypeDetails() {
        assertNull(ActionableFunctions.extractCancerTypeDetails(CkbTestFactory.createIndication("test", "JAX:not a doid")));

        assertEquals("0060463", extractDoidForApplicableCancerType("DOID:0060463"));
        assertEquals(CancerTypeConstants.CANCER_DOID, extractDoidForApplicableCancerType("JAX:10000003"));
        assertEquals(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY, extractDoidForApplicableCancerType("JAX:10000009"));
        assertEquals(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY, extractDoidForApplicableCancerType("JAX:10000008"));
        assertNull(ActionableFunctions.extractCancerTypeDetails(CkbTestFactory.createIndication("test", "JAX:10000004")));

        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                        CancerTypeConstants.BONE_MARROW_TYPE,
                        CancerTypeConstants.LEUKEMIA_TYPE),
                ActionableFunctions.extractCancerTypeDetails(CkbTestFactory.createIndication("test", "JAX:10000003"))
                        .blacklistedCancerTypes());
        assertTrue(ActionableFunctions.extractCancerTypeDetails(CkbTestFactory.createIndication("test", "JAX:10000009"))
                .blacklistedCancerTypes()
                .isEmpty());

    }

    private static String extractDoidForApplicableCancerType(@NotNull String termId) {
        return ActionableFunctions.extractCancerTypeDetails(CkbTestFactory.createIndication("test", termId)).applicableCancerType().doid();
    }
}

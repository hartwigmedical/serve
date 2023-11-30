package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;

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
        assertNull(ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:not a doid")));

        assertEquals("0060463",
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "DOID:0060463"))
                        .applicableCancerType()
                        .doid());
        assertEquals(CancerTypeConstants.CANCER_DOID,
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000003"))
                        .applicableCancerType()
                        .doid());
        assertEquals(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY,
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000009"))
                        .applicableCancerType()
                        .doid());
        assertEquals(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY,
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000008"))
                        .applicableCancerType()
                        .doid());
        assertNull(ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000004")));

        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                        CancerTypeConstants.BONE_MARROW_TYPE,
                        CancerTypeConstants.LEUKEMIA_TYPE),
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000003"))
                        .blacklistedCancerTypes());
        assertEquals(Sets.newHashSet(),
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000009"))
                        .blacklistedCancerTypes());

    }
}

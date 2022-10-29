package com.hartwig.serve.common.ensemblcache;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class GeneNameMappingTest {

    @Test
    public void canLoadFromResource() {
        assertNotNull(GeneNameMapping.loadFromResource());
    }
}
package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.junit.Test;

public class ActionableEventsLoaderTest {

    private static final String TEST_ACTIONABILITY_DIR = Resources.getResource("actionable").getPath();

    @Test
    public void canLoadFromTestDir() throws IOException {
        assertNotNull(ActionableEventsLoader.readFromDir(TEST_ACTIONABILITY_DIR, RefGenomeVersion.V37));
    }
}
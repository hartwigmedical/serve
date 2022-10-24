package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.junit.Test;

public class ActionableEventsLoaderTest {

    private static final String TEST_SERVE_DIR = Resources.getResource("serve").getPath();

    @Test
    public void canLoadFromTestDir() throws IOException {
        assertNotNull(ActionableEventsLoader.readFromDir(TEST_SERVE_DIR, RefGenomeVersion.V37));
    }
}
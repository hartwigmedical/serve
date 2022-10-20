package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;

import org.junit.Test;

public class ActionableEventsLoaderTest {

    @Test
    public void canLoadFromTestDir() throws IOException {
        assertNotNull(ActionableEventsLoader.readFromDir(DatamodelTestFactory.TEST_SERVE_DIR, RefGenomeVersion.V37));
    }
}
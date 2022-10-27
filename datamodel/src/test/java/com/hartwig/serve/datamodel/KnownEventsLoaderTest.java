package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.junit.Test;

public class KnownEventsLoaderTest {

    private static final String TEST_KNOWN_DIR = Resources.getResource("known").getPath();

    @Test
    public void canLoadFromTestDir() throws IOException {
        assertNotNull(KnownEventsLoader.readFromDir(TEST_KNOWN_DIR, RefGenomeVersion.V37));
    }
}
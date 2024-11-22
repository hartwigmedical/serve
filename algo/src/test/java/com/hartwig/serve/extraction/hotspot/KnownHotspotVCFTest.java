package com.hartwig.serve.extraction.hotspot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;

import org.junit.Test;

public class KnownHotspotVCFTest {

    private static final String KNOWN_HOTSPOT_VCF = Resources.getResource("known_hotspots/known_hotspots_example.vcf").getPath();

    @Test
    public void canReadKnownHotspotVCF() throws IOException {
        List<KnownHotspot> hotspots = KnownHotspotVCF.read(KNOWN_HOTSPOT_VCF);

        assertEquals(2, hotspots.size());
    }

    @Test
    public void canVersionFilePaths() {
        String path = "/this/is/my/path.vcf";
        assertEquals("/this/is/my/path.37.vcf", KnownHotspotVCF.addVersionToFilePath(RefGenome.V37, path));

        String path2 = "file.testing.tsv";
        assertEquals("file.testing.37.tsv", KnownHotspotVCF.addVersionToFilePath(RefGenome.V37, path2));

        String path3 = "file.vcf.gz";
        assertEquals("file.37.vcf.gz", KnownHotspotVCF.addVersionToFilePath(RefGenome.V37, path3));
    }

    @Test
    public void canResolveIdentifierForAllRefGenomes() {
        for (RefGenome refGenome : RefGenome.values()) {
            assertNotNull(KnownHotspotVCF.determineRefGenomeIdentifier(refGenome));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHandlePathsWithNoExtension() {
        KnownHotspotVCF.addVersionToFilePath(RefGenome.V37, "path");
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHandlePathWithJustGzipExtension() {
        KnownHotspotVCF.addVersionToFilePath(RefGenome.V37, "path.gz");
    }
}
package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RefGenomeTest {

    @Test
    public void canVersionFilePaths() {
        String path = "/this/is/my/path.vcf";
        assertEquals("/this/is/my/path.37.vcf", RefGenome.V37.addVersionToFilePath(path));

        String path2 = "file.testing.tsv";
        assertEquals("file.testing.37.tsv", RefGenome.V37.addVersionToFilePath(path2));

        String path3 = "file.vcf.gz";
        assertEquals("file.37.vcf.gz", RefGenome.V37.addVersionToFilePath(path3));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHandlePathsWithNoExtension() {
        RefGenome.V37.addVersionToFilePath("path");
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHandlePathWithJustGzipExtension() {
        RefGenome.V37.addVersionToFilePath("path.gz");
    }
}
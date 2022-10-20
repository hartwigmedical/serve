package com.hartwig.serve.extraction.exon;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;

import org.junit.Test;

public class KnownExonFileTest {

    private static final String TEST_KNOWN_EXONS_DIR = Resources.getResource("known_exons").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String knownExonTsv = KnownExonFile.knownExonTsvPath(TEST_KNOWN_EXONS_DIR, RefGenomeVersion.V37);
        List<KnownExon> knownExons = KnownExonFile.read(knownExonTsv);

        assertEquals(2, knownExons.size());

        List<String> lines = KnownExonFile.toLines(knownExons);
        List<KnownExon> regeneratedExons = KnownExonFile.fromLines(lines);
        List<String> regeneratedLines = KnownExonFile.toLines(regeneratedExons);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}
package com.hartwig.serve.extraction.codon;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.junit.Test;

public class KnownCodonFileTest {

    private static final String TEST_KNOWN_CODON_DIR = Resources.getResource("known_codons").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String knownCodonTsv = KnownCodonFile.knownCodonTsvPath(TEST_KNOWN_CODON_DIR, RefGenomeVersion.V37);
        List<KnownCodon> knownCodons = KnownCodonFile.read(knownCodonTsv);

        assertEquals(2, knownCodons.size());

        List<String> lines = KnownCodonFile.toLines(knownCodons);
        List<KnownCodon> regeneratedCodons = KnownCodonFile.fromLines(lines);
        List<String> regeneratedLines = KnownCodonFile.toLines(regeneratedCodons);
        assertEquals(lines.size(), regeneratedLines.size());

        for (int i = 0; i < lines.size(); i++) {
            assertEquals(lines.get(i), regeneratedLines.get(i));
        }
    }
}
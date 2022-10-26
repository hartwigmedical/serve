package com.hartwig.serve.extraction.immuno;

import static org.junit.Assert.assertEquals;

import com.hartwig.serve.common.classification.EventType;

import org.junit.Test;

public class ImmunoHLAExtractorTest {

    @Test
    public void canExtractImmunoHLA() {
        ImmunoHLAExtractor immunoHLAExtractor = new ImmunoHLAExtractor();

        ImmunoHLA immunoHLACorrect = immunoHLAExtractor.extract(EventType.IMMUNO_HLA, "A*20");
        assertEquals("A*20", immunoHLACorrect.hlaAllele());

        ImmunoHLA immunoHLALess = immunoHLAExtractor.extract(EventType.IMMUNO_HLA, "A*2");
        assertEquals("A*2", immunoHLALess.hlaAllele());

        ImmunoHLA immunoHLAMore = immunoHLAExtractor.extract(EventType.IMMUNO_HLA, "HLA-A*021:054");
        assertEquals("HLA-A*021:054", immunoHLAMore.hlaAllele());
    }
}
package com.hartwig.serve.extraction.immuno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.hartwig.serve.common.classification.EventType;

import org.junit.Test;

public class ImmunoHLAExtractorTest {

    @Test
    public void canExtractImmunoHLAWithoutThirdAndFourthField() {
        ImmunoHLAExtractor immunoHLAExtractor = new ImmunoHLAExtractor();

        ImmunoHLA immunoHLACorrect = immunoHLAExtractor.extract("HLA-A", EventType.IMMUNO_HLA, "*01:02");
        assertEquals("HLA-A", immunoHLACorrect.gene());
        assertEquals("01", immunoHLACorrect.alleleGroup());
        assertEquals("02", immunoHLACorrect.hlaProtein());
        assertNull(immunoHLACorrect.synonymousDnaChange());
        assertNull(immunoHLACorrect.nonCodingDifferences());
        assertNull(immunoHLACorrect.expressionStatus());
    }

    @Test
    public void canExtractImmunoHLAWithoutThirdAndFourthFieldAndWithExpressionStatus() {
        ImmunoHLAExtractor immunoHLAExtractor = new ImmunoHLAExtractor();

        ImmunoHLA immunoHLACorrect = immunoHLAExtractor.extract("HLA-A", EventType.IMMUNO_HLA, "*01:02P");
        assertEquals("HLA-A", immunoHLACorrect.gene());
        assertEquals("01", immunoHLACorrect.alleleGroup());
        assertEquals("02", immunoHLACorrect.hlaProtein());
        assertNull(immunoHLACorrect.synonymousDnaChange());
        assertNull(immunoHLACorrect.nonCodingDifferences());
        assertEquals("P", immunoHLACorrect.expressionStatus());
    }

    @Test
    public void canExtractImmunoHLAWithThirdField() {
        ImmunoHLAExtractor immunoHLAExtractor = new ImmunoHLAExtractor();

        ImmunoHLA immunoHLACorrect = immunoHLAExtractor.extract("HLA-A", EventType.IMMUNO_HLA, "*01:02:03");
        assertEquals("HLA-A", immunoHLACorrect.gene());
        assertEquals("01", immunoHLACorrect.alleleGroup());
        assertEquals("02", immunoHLACorrect.hlaProtein());
        assertEquals("03", immunoHLACorrect.synonymousDnaChange());
        assertNull(immunoHLACorrect.nonCodingDifferences());
        assertNull(immunoHLACorrect.expressionStatus());
    }

    @Test
    public void canExtractImmunoHLAWithThirdFieldAndExpressionStatus() {
        ImmunoHLAExtractor immunoHLAExtractor = new ImmunoHLAExtractor();

        ImmunoHLA immunoHLACorrect = immunoHLAExtractor.extract("HLA-A", EventType.IMMUNO_HLA, "*01:02:03N");
        assertEquals("HLA-A", immunoHLACorrect.gene());
        assertEquals("01", immunoHLACorrect.alleleGroup());
        assertEquals("02", immunoHLACorrect.hlaProtein());
        assertEquals("03", immunoHLACorrect.synonymousDnaChange());
        assertNull(immunoHLACorrect.nonCodingDifferences());
        assertEquals("N", immunoHLACorrect.expressionStatus());
    }

    @Test
    public void canExtractImmunoHLAWithFourthField() {
        ImmunoHLAExtractor immunoHLAExtractor = new ImmunoHLAExtractor();

        ImmunoHLA immunoHLACorrect = immunoHLAExtractor.extract("HLA-A", EventType.IMMUNO_HLA, "*01:02:03:04");
        assertEquals("HLA-A", immunoHLACorrect.gene());
        assertEquals("01", immunoHLACorrect.alleleGroup());
        assertEquals("02", immunoHLACorrect.hlaProtein());
        assertEquals("03", immunoHLACorrect.synonymousDnaChange());
        assertEquals("04", immunoHLACorrect.nonCodingDifferences());
        assertNull(immunoHLACorrect.expressionStatus());
    }

    @Test
    public void canExtractImmunoHLAWithFourthFieldAndExpressionStatus() {
        ImmunoHLAExtractor immunoHLAExtractor = new ImmunoHLAExtractor();

        ImmunoHLA immunoHLACorrect = immunoHLAExtractor.extract("HLA-A", EventType.IMMUNO_HLA, "*01:02:03:04Q");
        assertEquals("HLA-A", immunoHLACorrect.gene());
        assertEquals("01", immunoHLACorrect.alleleGroup());
        assertEquals("02", immunoHLACorrect.hlaProtein());
        assertEquals("03", immunoHLACorrect.synonymousDnaChange());
        assertEquals("04", immunoHLACorrect.nonCodingDifferences());
        assertEquals("Q", immunoHLACorrect.expressionStatus());
    }
}
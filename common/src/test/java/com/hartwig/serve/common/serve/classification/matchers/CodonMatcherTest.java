package com.hartwig.serve.common.serve.classification.matchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class CodonMatcherTest {

    @Test
    public void canAssessWhetherEventIsCodon() {
        EventMatcher matcher = new CodonMatcher(event -> event);

        assertTrue(matcher.matches("GNAS", "R201"));
        assertTrue(matcher.matches("EGFR", "E709X"));

        assertFalse(matcher.matches(Strings.EMPTY, Strings.EMPTY));
        assertFalse(matcher.matches("BRAF", "600"));
        assertFalse(matcher.matches("EZH2", "EZH2 (Y641,A677)"));
        assertFalse(matcher.matches("EZH2", "T148HFSX9"));
        assertFalse(matcher.matches("EGFR", "RARE EX 18-21 MUT"));
        assertFalse(matcher.matches("BRAF", "V600E"));
    }

    @Test
    public void canCountDigitSequences() {
        assertEquals(0, CodonMatcher.countDigitSequences(Strings.EMPTY));
        assertEquals(0, CodonMatcher.countDigitSequences("hi"));
        assertEquals(1, CodonMatcher.countDigitSequences("V600K"));

        assertEquals(4, CodonMatcher.countDigitSequences("A1B2C3D4"));
        assertEquals(2, CodonMatcher.countDigitSequences("100A200"));
    }
}
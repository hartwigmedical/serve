package com.hartwig.serve.common.classification.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HlaMatcherTest {

    @Test
    public void canAssessWhetherEventIsHLA() {
        EventMatcher matcher = new HlaMatcher();

        assertTrue(matcher.matches("HLA-A", "*02"));
        assertTrue(matcher.matches("HLA-A", "*02:01"));
        assertTrue(matcher.matches("HLA-A", "*02:01P"));
        assertTrue(matcher.matches("HLA-A", "*02:01:01"));
        assertTrue(matcher.matches("HLA-A", "*02:130:01N"));
        assertTrue(matcher.matches("HLA-A", "*02:01:6700:01"));
        assertTrue(matcher.matches("HLA-A", "*02:01:01:01Q"));

        assertFalse(matcher.matches("BRAF", "V600E"));
        assertFalse(matcher.matches("HLA-A", "V600E"));
        assertFalse(matcher.matches("HLA-A", "*10L"));
    }
}
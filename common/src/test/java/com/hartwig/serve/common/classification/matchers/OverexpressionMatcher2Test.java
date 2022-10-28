package com.hartwig.serve.common.classification.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

public class OverexpressionMatcher2Test {

    private static final Set<String> OVEREXPRESSION_KEYWORDS = Sets.newHashSet();
    private static final Set<String> OVEREXPRESSION_KEY_PHRASES = Sets.newHashSet("over exp");

    @Test
    public void canAssessWhetherEventIsAmplification() {
        EventMatcher matcher = new AmplificationMatcher(OVEREXPRESSION_KEYWORDS, OVEREXPRESSION_KEY_PHRASES);

        assertTrue(matcher.matches("ALK", "ALK over exp"));

        assertFalse(matcher.matches("ALK", "ALK  amp"));
        assertFalse(matcher.matches("BRAF", "V600E"));
    }
}
package com.hartwig.serve.common.classification.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

public class UnderexpressionMatcher2Test {

    private static final Set<String> UNDEREXPRESSION_KEYWORDS = Sets.newHashSet();
    private static final Set<String> UNDEREXPRESSION_KEY_PHRASES = Sets.newHashSet("dec exp");

    @Test
    public void canAssessWhetherEventIsDeletion() {
        EventMatcher matcher = new UnderexpressionMatcher2(UNDEREXPRESSION_KEYWORDS, UNDEREXPRESSION_KEY_PHRASES);

        assertTrue(matcher.matches("CDKN2A", "CDKN2A dec exp"));

        assertFalse(matcher.matches("CDKN2A", "CDKN2A del"));
        assertFalse(matcher.matches("EGFR", "EGFR Ex19 del"));
        assertFalse(matcher.matches("BRAF", "V600E"));
    }
}
package com.hartwig.serve.common.classification.matchers;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PositiveMatcherTest {


    private static final Set<String> POSITIVE_EVENTS = Sets.newHashSet("positive");
    private static final Set<String> POSITIVE_BLACKLIST_EVENTS = Sets.newHashSet("HRD", "MSI");

    @Test
    public void canAssessWhetherEventIsPositiveEvent() {
        PositiveMatcher matcher = new PositiveMatcher(POSITIVE_EVENTS,
                POSITIVE_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("AKT1", "AKT1 positive"));
        assertTrue(matcher.matches("TP53", "TP53 positive"));

        assertFalse(matcher.matches("HRD", "HRD positive"));
        assertFalse(matcher.matches("MSI", "MSI positive"));
    }
}
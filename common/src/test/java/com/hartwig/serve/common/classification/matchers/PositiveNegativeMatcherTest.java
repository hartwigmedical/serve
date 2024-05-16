package com.hartwig.serve.common.classification.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

public class PositiveNegativeMatcherTest {

    private static final Set<String> POSITIVE_EVENTS = Sets.newHashSet("positive");
    private static final Set<String> POSITIVE_BLACKLIST_EVENTS = Sets.newHashSet("HRD", "MSI");
    private static final Set<String> NEGATIVE_EVENTS = Sets.newHashSet("negative");
    private static final Set<String> NEGATIVE_BLACKLIST_EVENTS = Sets.newHashSet("HRD", "MSI");

    @Test
    public void canAssessWhetherEventIsPositiveEvent() {
        PositiveNegativeMatcher matcher = new PositiveNegativeMatcher(POSITIVE_EVENTS,
                POSITIVE_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("AKT1", "AKT1 positive"));
        assertTrue(matcher.matches("TP53", "TP53 positive"));

        assertFalse(matcher.matches("HRD", "HRD positive"));
        assertFalse(matcher.matches("MSI", "MSI positive"));
    }

    @Test
    public void canAssessWhetherEventIsNegativeEvent() {
        PositiveNegativeMatcher matcher = new PositiveNegativeMatcher(NEGATIVE_EVENTS,
                NEGATIVE_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("AKT1", "AKT1 negative"));
        assertTrue(matcher.matches("TP53", "TP53 negative"));

        assertFalse(matcher.matches("HRD", "HRD negative"));
        assertFalse(matcher.matches("MSI", "MSI negative"));
    }

}
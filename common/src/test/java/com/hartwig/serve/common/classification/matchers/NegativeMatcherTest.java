package com.hartwig.serve.common.classification.matchers;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NegativeMatcherTest {

    private static final Set<String> NEGATIVE_EVENTS = Sets.newHashSet("negative");
    private static final Set<String> NEGATIVE_BLACKLIST_EVENTS = Sets.newHashSet("HRD", "MSI");

    @Test
    public void canAssessWhetherEventIsGeneLevelEvent() {
        NegativeMatcher matcher = new NegativeMatcher(NEGATIVE_EVENTS,
                NEGATIVE_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("AKT1", "AKT1 negative"));
        assertTrue(matcher.matches("TP53", "TP53 negative"));

        assertFalse(matcher.matches("HRD", "HRD negative"));
        assertFalse(matcher.matches("MSI", "MSI negative"));
    }
}
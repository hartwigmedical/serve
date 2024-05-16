package com.hartwig.serve.common.classification.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

public class PresenceAbsenceOfProteinMatcherTest {

    private static final Set<String> PRESENCE_OF_PROTEIN_EVENTS = Sets.newHashSet("positive");
    private static final Set<String> PRESENCE_OF_PROTEIN_BLACKLIST_EVENTS = Sets.newHashSet("HRD", "MSI");
    private static final Set<String> ABSENCE_OF_PROTEIN_EVENTS = Sets.newHashSet("negative");
    private static final Set<String> ABSENCE_OF_PROTEIN_BLACKLIST_EVENTS = Sets.newHashSet("HRD", "MSI");

    @Test
    public void canAssessWhetherEventIsPositiveEvent() {
        PresenceAbsenceOfProteinMatcher matcher = new PresenceAbsenceOfProteinMatcher(PRESENCE_OF_PROTEIN_EVENTS,
                PRESENCE_OF_PROTEIN_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("AKT1", "AKT1 positive"));
        assertTrue(matcher.matches("TP53", "TP53 positive"));

        assertFalse(matcher.matches("HRD", "HRD positive"));
        assertFalse(matcher.matches("MSI", "MSI positive"));
    }

    @Test
    public void canAssessWhetherEventIsNegativeEvent() {
        PresenceAbsenceOfProteinMatcher matcher = new PresenceAbsenceOfProteinMatcher(ABSENCE_OF_PROTEIN_EVENTS,
                ABSENCE_OF_PROTEIN_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("AKT1", "AKT1 negative"));
        assertTrue(matcher.matches("TP53", "TP53 negative"));

        assertFalse(matcher.matches("HRD", "HRD negative"));
        assertFalse(matcher.matches("MSI", "MSI negative"));
    }

}
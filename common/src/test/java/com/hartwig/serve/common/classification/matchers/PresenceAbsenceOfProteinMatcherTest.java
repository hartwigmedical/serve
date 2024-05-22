package com.hartwig.serve.common.classification.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

public class PresenceAbsenceOfProteinMatcherTest {

    private static final Set<String> PRESENCE_OF_PROTEIN_EVENTS = Sets.newHashSet("positive");
    private static final Set<String> PRESENCE_OF_PROTEIN_BLACKLIST_EVENTS = Sets.newHashSet("blacklist");
    private static final Set<String> ABSENCE_OF_PROTEIN_EVENTS = Sets.newHashSet("negative");
    private static final Set<String> ABSENCE_OF_PROTEIN_BLACKLIST_EVENTS = Sets.newHashSet("blacklist");

    @Test
    public void canAssessWhetherEventIsPositiveEvent() {
        PresenceAbsenceOfProteinMatcher matcher = new PresenceAbsenceOfProteinMatcher(PRESENCE_OF_PROTEIN_EVENTS,
                PRESENCE_OF_PROTEIN_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("whitelist", "whitelist positive"));
        assertFalse(matcher.matches("blacklist", "blacklist positive"));
    }

    @Test
    public void canAssessWhetherEventIsNegativeEvent() {
        PresenceAbsenceOfProteinMatcher matcher = new PresenceAbsenceOfProteinMatcher(ABSENCE_OF_PROTEIN_EVENTS,
                ABSENCE_OF_PROTEIN_BLACKLIST_EVENTS);

        assertTrue(matcher.matches("whitelist", "whitelist negative"));
        assertFalse(matcher.matches("blacklist", "blacklist negative"));
    }
}
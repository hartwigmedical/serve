package com.hartwig.serve.common.classification.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

public class ExonMatcherTest {

    private static final Set<String> EXON_IDENTIFIERS = Sets.newHashSet("exon", "EXON");
    private static final Set<String> EXON_KEYWORDS = Sets.newHashSet("deletions", "MUTATION");
    private static final Set<String> EXON_BLACKLIST_KEY_PHRASES = Sets.newHashSet("no exon");
    private static final Set<String> SPECIFIC_EXON_EVENTS = Sets.newHashSet("RARE EX 18-21 MUT");

    @Test
    public void canAssessWhetherEventIsExon() {
        EventMatcher matcher = new ExonMatcher(EXON_IDENTIFIERS, EXON_KEYWORDS, EXON_BLACKLIST_KEY_PHRASES, SPECIFIC_EXON_EVENTS);

        assertTrue(matcher.matches("EGFR", "EGFR exon 19 deletions"));
        assertTrue(matcher.matches("AXSL1", "EXON 12 MUTATION"));
        assertTrue(matcher.matches("EGFR", "RARE EX 18-21 MUT"));

        assertFalse(matcher.matches("EGFR", "EGFR no exon deletions"));
        assertFalse(matcher.matches("EGFR", "EGFR deletions"));
        assertFalse(matcher.matches("EGFR", "EGFR exon 19"));
        assertFalse(matcher.matches("BRAF", "V600E"));
    }

    @Test
    public void doesNotMatchExonicFusionPairs() {
        // Mimics a config in which "exon" itself is both the identifier and the keyword, eg. CKB.
        EventMatcher matcher = new ExonMatcher(Sets.newHashSet("exon"), Sets.newHashSet("exon"), Sets.newHashSet(), Sets.newHashSet());

        assertTrue(matcher.matches("EGFR", "exon 19 del"));

        assertFalse(matcher.matches("RET", "RUFY2 exon 9::RET exon 12"));
        assertFalse(matcher.matches("RET", "RUFY2 exon9::RET exon12"));
        assertFalse(matcher.matches("MET", "MET exon 13::MET exon 15"));
    }
}
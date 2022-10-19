package com.hartwig.serve.util;

import org.junit.Test;

public class ProgressTrackerTest {

    @Test
    public void canTrackProgressForZeroEntries() {
        // Zero is a special case, avoid division-by-zero
        new ProgressTracker("label", 0).update();
    }
}
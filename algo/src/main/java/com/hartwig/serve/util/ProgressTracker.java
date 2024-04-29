package com.hartwig.serve.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ProgressTracker {

    private static final Logger LOGGER = LogManager.getLogger(ProgressTracker.class);
    private static final int INTERVAL = 10;

    @NotNull
    private final String label;
    private final int totalCount;

    private final AtomicInteger counter = new AtomicInteger(0);

    public ProgressTracker(@NotNull final String label, final int totalCount) {
        this.label = label;
        this.totalCount = totalCount;
    }

    public void update() {
        var count = this.counter.addAndGet(1);
        if (totalCount >= INTERVAL && count % (totalCount / INTERVAL) == 0) {
            LOGGER.info(" Processed {} of {} {} entries", this.counter, totalCount, label);
        }
    }
}

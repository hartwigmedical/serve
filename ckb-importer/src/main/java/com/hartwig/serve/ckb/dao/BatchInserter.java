package com.hartwig.serve.ckb.dao;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Query;

final class BatchInserter {

    @NotNull
    private final DSLContext context;
    private final int batchSize;
    @NotNull
    private final List<Query> pending;

    BatchInserter(@NotNull DSLContext context, int batchSize) {
        this.context = context;
        this.batchSize = batchSize;
        this.pending = new ArrayList<>(batchSize);
    }

    void add(@NotNull Query query) {
        pending.add(query);
        if (pending.size() >= batchSize) {
            flush();
        }
    }

    void flush() {
        if (pending.isEmpty()) {
            return;
        }

        context.batch(pending).execute();
        pending.clear();
    }
}
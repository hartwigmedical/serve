package com.hartwig.serve.dao;

import org.jetbrains.annotations.Nullable;

public final class DatabaseUtil {

    public static final int DB_BATCH_INSERT_SIZE = 1000;

    private DatabaseUtil() {
    }

    @Nullable
    public static Byte toByte(@Nullable Boolean bool) {
        return bool != null ? (byte) (bool ? 1 : 0) : null;
    }
}

package com.hartwig.serve.iclusion.dao;

import org.jetbrains.annotations.NotNull;

public class Utils {

    private Utils() {
    }

    static boolean anyNull(@NotNull Object... arguments) {
        for (Object object : arguments) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }
}

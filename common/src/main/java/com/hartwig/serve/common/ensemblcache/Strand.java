package com.hartwig.serve.common.ensemblcache;

import org.jetbrains.annotations.NotNull;

public enum Strand {
    FORWARD,
    REVERSE;

    @NotNull
    public static Strand resolve(int direction) {
        switch (direction) {
            case 1:
                return Strand.FORWARD;
            case -1:
                return Strand.REVERSE;
        }

        throw new IllegalArgumentException("Invalid direction " + direction);
    }
}

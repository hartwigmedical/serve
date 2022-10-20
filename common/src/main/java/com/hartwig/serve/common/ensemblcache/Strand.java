package com.hartwig.serve.common.ensemblcache;

import org.jetbrains.annotations.NotNull;

public enum Strand {
    FORWARD,
    REVERSE;

    // alternative byte representation for file I/O
    public static final byte POS_STRAND = 1;
    public static final byte NEG_STRAND = -1;

    @NotNull
    public static Strand valueOf(int direction) {
        switch (direction) {
            case 1:
                return Strand.FORWARD;
            case -1:
                return Strand.REVERSE;
        }

        throw new IllegalArgumentException("Invalid direction " + direction);
    }

    public byte asByte() {
        return this == FORWARD ? POS_STRAND : NEG_STRAND;
    }
}

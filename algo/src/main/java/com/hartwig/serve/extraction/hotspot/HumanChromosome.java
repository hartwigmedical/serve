package com.hartwig.serve.extraction.hotspot;

import com.hartwig.serve.common.RefGenomeFunctions;

import org.jetbrains.annotations.NotNull;

public enum HumanChromosome implements Chromosome {
    _1,
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    _10,
    _11,
    _12,
    _13,
    _14,
    _15,
    _16,
    _17,
    _18,
    _19,
    _20,
    _21,
    _22,
    _X,
    _Y;

    private final String name;

    HumanChromosome() {
        this.name = name().substring(1).intern();
    }

    @NotNull
    public static HumanChromosome fromString(@NotNull String chromosome) {
        if (chromosome.toLowerCase().startsWith("chr")) {
            return HumanChromosome.valueOf("_" + chromosome.substring(3));
        }

        return HumanChromosome.valueOf("_" + chromosome);
    }

    public static boolean contains(@NotNull String chromosome) {
        String trimmedContig = RefGenomeFunctions.stripChrPrefix(chromosome);
        if (isNumeric(trimmedContig)) {
            int integerContig = Integer.parseInt(trimmedContig);
            return integerContig >= 1 && integerContig <= 22;
        }

        return trimmedContig.equals("X") || trimmedContig.equals("Y");
    }

    @Override
    public String toString() {
        return name;
    }

    private static boolean isNumeric(@NotNull String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}

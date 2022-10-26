package com.hartwig.serve.datamodel.common;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class ChromosomeComparator implements Comparator<String> {

    private static final String CHR_PREFIX = "chr";

    @Override
    public int compare(@NotNull String chromosome1, @NotNull String chromosome2) {
        return Integer.compare(chromosomeRank(chromosome1), chromosomeRank(chromosome2));
    }

    private static int chromosomeRank(@NotNull String chromosome) {
        String chrTrimmed = stripChrPrefix(chromosome);

        if (chrTrimmed.equalsIgnoreCase("X")) {
            return 23;
        } else if (chrTrimmed.equalsIgnoreCase("Y")) {
            return 24;
        } else if (chrTrimmed.equalsIgnoreCase("MT") || chrTrimmed.equalsIgnoreCase("M")) {
            return 25;
        } else {
            return Integer.parseInt(chrTrimmed);
        }
    }

    @NotNull
    private static String stripChrPrefix(@NotNull String chromosome) {
        if (chromosome.startsWith(CHR_PREFIX)) {
            return chromosome.substring(CHR_PREFIX.length());
        }

        return chromosome;
    }
}

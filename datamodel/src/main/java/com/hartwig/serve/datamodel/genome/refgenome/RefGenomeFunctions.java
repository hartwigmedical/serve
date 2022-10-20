package com.hartwig.serve.datamodel.genome.refgenome;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class RefGenomeFunctions {

    private static final Logger LOGGER = LogManager.getLogger(RefGenomeFunctions.class);

    private static final String CHR_PREFIX = "chr";

    private RefGenomeFunctions() {
    }

    @NotNull
    public static String versionedChromosome(@NotNull String chromosome, @NotNull RefGenomeVersion refGenomeVersion) {
        if (refGenomeVersion == RefGenomeVersion.V38) {
            return RefGenomeFunctions.enforceChrPrefix(chromosome);
        } else if (refGenomeVersion == RefGenomeVersion.V37) {
            return RefGenomeFunctions.stripChrPrefix(chromosome);
        } else {
            LOGGER.warn("Unrecognized ref genome version for making chromosome ref genome specific: {}", refGenomeVersion);
            return chromosome;
        }
    }

    @NotNull
    public static String stripChrPrefix(@NotNull final String chromosome) {
        if (chromosome.startsWith(CHR_PREFIX)) {
            return chromosome.substring(CHR_PREFIX.length());
        }

        return chromosome;
    }

    @NotNull
    public static String enforceChrPrefix(@NotNull final String chromosome) {
        if (!chromosome.startsWith(CHR_PREFIX)) {
            return CHR_PREFIX + chromosome;
        }

        return chromosome;
    }
}

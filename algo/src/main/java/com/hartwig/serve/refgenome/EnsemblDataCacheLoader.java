package com.hartwig.serve.refgenome;

import java.io.IOException;

import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.genome.refgenome.RefGenomeVersion;

import org.jetbrains.annotations.NotNull;

public final class EnsemblDataCacheLoader {

    private EnsemblDataCacheLoader() {
    }

    @NotNull
    public static EnsemblDataCache load(@NotNull String ensemblDataDir, @NotNull RefGenomeVersion refGenomeVersion) throws IOException {
        EnsemblDataCache ensemblDataCache = new EnsemblDataCache(ensemblDataDir, refGenomeVersion);
        ensemblDataCache.setRequiredData(true, false, false, true);

        if (!ensemblDataCache.load(false)) {
            throw new IOException("Could not load ensembl data cache from " + ensemblDataDir);
        }
        return ensemblDataCache;
    }
}

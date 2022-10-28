package com.hartwig.serve;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.ensemblcache.EnsemblDataLoader;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.jetbrains.annotations.NotNull;

public final class EnsemblDataCacheTestFactory {

    private static final String ENSEMBL_DATA_DIR_37 = Resources.getResource("ensembl_data_cache/v37").getPath();

    private EnsemblDataCacheTestFactory() {
    }

    @NotNull
    public static EnsemblDataCache create37() {
        try {
            return EnsemblDataLoader.load(ENSEMBL_DATA_DIR_37, RefGenomeVersion.V37);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load test ensembl cache");
        }
    }
}
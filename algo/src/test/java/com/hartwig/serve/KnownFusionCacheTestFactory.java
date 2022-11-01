package com.hartwig.serve;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.refgenome.RefGenomeManagerFactory;

import org.jetbrains.annotations.NotNull;

public final class KnownFusionCacheTestFactory {

    private static final String FUSION_DATA_37 = Resources.getResource("known_fusion_data/known_fusion_data.37.csv").getPath();

    private KnownFusionCacheTestFactory() {
    }

    @NotNull
    public static KnownFusionCache create37() {
        try {
            return RefGenomeManagerFactory.buildKnownFusionCacheFromFile(FUSION_DATA_37);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load test fusion data cache");
        }
    }
}
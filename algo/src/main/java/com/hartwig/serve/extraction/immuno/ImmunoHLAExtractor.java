package com.hartwig.serve.extraction.immuno;

import com.hartwig.serve.common.classification.EventType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImmunoHLAExtractor {

    private static final Logger LOGGER = LogManager.getLogger(ImmunoHLAExtractor.class);

    public ImmunoHLAExtractor() {
    }

    @Nullable
    public ImmunoHLA extract(@NotNull EventType type, @NotNull String event) {
        if (type == EventType.IMMUNO_HLA) {
            String onlyHLAType = event.contains("-") ? event.split("-")[1] : event;
            String mainHlaType = onlyHLAType.contains(":") ? onlyHLAType.split(":")[0] : onlyHLAType;

            if (mainHlaType.length() != 4 ){
                LOGGER.warn("Unknown HLA allele {} in knowledgebase", mainHlaType);
            }
            return ImmutableImmunoHLA.builder().hlaAllele(event).build();
        }
        return null;
    }
}
package com.hartwig.serve.extraction.hotspot;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.common.drivercatalog.DriverCategory;
import com.hartwig.serve.common.drivercatalog.panel.DriverGene;
import com.hartwig.serve.common.serve.classification.EventPreprocessor;
import com.hartwig.serve.common.serve.classification.EventType;
import com.hartwig.serve.common.variant.hotspot.VariantHotspot;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotspotExtractor {

    private static final Logger LOGGER = LogManager.getLogger(HotspotExtractor.class);
    @NotNull
    private final GeneChecker geneChecker;
    @NotNull
    private final ProteinResolver proteinResolver;
    @NotNull
    private final EventPreprocessor proteinAnnotationExtractor;
    @NotNull
    private final DriverInconsistencyMode driverInconsistencyMode;
    @NotNull
    private final List<DriverGene> driverGenes;

    public HotspotExtractor(@NotNull final GeneChecker geneChecker, @NotNull final ProteinResolver proteinResolver,
            @NotNull final EventPreprocessor proteinAnnotationExtractor, @NotNull final DriverInconsistencyMode driverInconsistencyMode,
            @NotNull final List<DriverGene> driverGenes) {
        this.geneChecker = geneChecker;
        this.proteinResolver = proteinResolver;
        this.proteinAnnotationExtractor = proteinAnnotationExtractor;
        this.driverInconsistencyMode = driverInconsistencyMode;
        this.driverGenes = driverGenes;
    }

    @Nullable
    @VisibleForTesting
    public static DriverCategory findByGene(@NotNull List<DriverGene> driverGenes, @NotNull String gene) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(gene)) {
                return driverGene.likelihoodType();
            }
        }
        return null;
    }

    @Nullable
    public List<VariantHotspot> extract(@NotNull String gene, @Nullable String transcriptId, @NotNull EventType type,
            @NotNull String event) {
        if (type == EventType.HOTSPOT && geneChecker.isValidGene(gene)) {
            DriverCategory driverCategory = findByGene(driverGenes, gene);
            if (driverCategory == null && driverInconsistencyMode.isActive()) {
                if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                    LOGGER.warn("Hotpot event on {} on {} is not included in driver catalog and won't ever be reported.", type, gene);
                } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                    LOGGER.info("Hotspot event filtered -- {} on {} is not included in driver catalog and won't ever be reported.",
                            type,
                            gene);
                    return null;
                }
            }

            return proteinResolver.resolve(gene, transcriptId, proteinAnnotationExtractor.apply(event));
        }

        return null;
    }
}

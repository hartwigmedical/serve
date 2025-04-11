package com.hartwig.serve.extraction.variant;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.common.classification.EventPreprocessor;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivergene.DriverCategory;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableVariantAnnotation;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantAnnotation;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariantExtractor {

    private static final Logger LOGGER = LogManager.getLogger(VariantExtractor.class);

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

    public VariantExtractor(@NotNull final GeneChecker geneChecker, @NotNull final ProteinResolver proteinResolver,
            @NotNull final EventPreprocessor proteinAnnotationExtractor, @NotNull final DriverInconsistencyMode driverInconsistencyMode,
            @NotNull final List<DriverGene> driverGenes) {
        this.geneChecker = geneChecker;
        this.proteinResolver = proteinResolver;
        this.proteinAnnotationExtractor = proteinAnnotationExtractor;
        this.driverInconsistencyMode = driverInconsistencyMode;
        this.driverGenes = driverGenes;
    }
    
    @Nullable
    public List<VariantAnnotation> extract(@NotNull String gene, @Nullable String transcriptId, @NotNull EventType type,
            @NotNull String event) {
        if (type == EventType.VARIANT && geneChecker.isValidGene(gene)) {
            DriverCategory driverCategory = findByGene(driverGenes, gene);
            if (driverCategory == null && driverInconsistencyMode.isActive()) {
                if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                    LOGGER.warn("Variant event on {} on {} is not included in driver catalog and won't ever be reported.", type, gene);
                } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                    LOGGER.info("Variant event filtered -- {} on {} is not included in driver catalog and won't ever be reported.",
                            type,
                            gene);
                    return null;
                }
            }

            List<VariantAnnotation> variants = Lists.newArrayList();
            for (Variant variant : proteinResolver.resolve(gene, transcriptId, proteinAnnotationExtractor.apply(event))) {
                variants.add(ImmutableVariantAnnotation.builder().from(variant).gene(gene).ref(variant.ref()).alt(variant.alt()).build());
            }
            return variants;
        }

        return null;
    }

    @Nullable
    @VisibleForTesting
    static DriverCategory findByGene(@NotNull List<DriverGene> driverGenes, @NotNull String gene) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(gene)) {
                return driverGene.likelihoodType();
            }
        }
        return null;
    }
}

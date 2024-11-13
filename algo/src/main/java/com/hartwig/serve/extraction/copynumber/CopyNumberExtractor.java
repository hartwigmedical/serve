package com.hartwig.serve.extraction.copynumber;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivergene.DriverCategory;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.gene.GeneEvent;
import com.hartwig.serve.extraction.gene.ImmutableGeneAnnotationImpl;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CopyNumberExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CopyNumberExtractor.class);

    private static final Set<EventType> COPY_NUMBER_EVENTS =
            Sets.newHashSet(EventType.AMPLIFICATION, EventType.OVEREXPRESSION, EventType.DELETION, EventType.UNDEREXPRESSION);

    @NotNull
    private final GeneChecker geneChecker;
    @NotNull
    private final List<DriverGene> driverGenes;
    private final DriverInconsistencyMode driverInconsistencyMode;

    public CopyNumberExtractor(@NotNull final GeneChecker geneChecker, @NotNull final List<DriverGene> driverGenes,
            @NotNull final DriverInconsistencyMode driverInconsistencyMode) {
        this.geneChecker = geneChecker;
        this.driverGenes = driverGenes;
        this.driverInconsistencyMode = driverInconsistencyMode;
    }

    @Nullable
    public GeneAnnotation extract(@NotNull String gene, @NotNull EventType type) {
        if (COPY_NUMBER_EVENTS.contains(type) && geneChecker.isValidGene(gene)) {
            DriverCategory driverCategory = findByGene(driverGenes, gene);

            if (driverInconsistencyMode.isActive()) {
                if ((driverCategory == DriverCategory.TSG && type == EventType.AMPLIFICATION) || (driverCategory == DriverCategory.TSG
                        && type == EventType.OVEREXPRESSION) || (driverCategory == DriverCategory.ONCO && type == EventType.DELETION) || (
                        driverCategory == DriverCategory.ONCO && type == EventType.UNDEREXPRESSION) || driverCategory == null) {
                    if (driverInconsistencyMode == DriverInconsistencyMode.WARN_ONLY) {
                        LOGGER.warn("CopyNumber event mismatch for {} in driver category {} vs event type {}", gene, driverCategory, type);
                    } else if (driverInconsistencyMode == DriverInconsistencyMode.FILTER) {
                        LOGGER.info("CopyNumber event filtered -- Mismatch for {} in driver category {} vs event type {}",
                                gene,
                                driverCategory,
                                type);
                        return null;
                    }
                }
            }

            return ImmutableGeneAnnotationImpl.builder().gene(gene).event(toCopyNumberEvent(type)).build();
        }

        return null;
    }

    @Nullable
    private static DriverCategory findByGene(@NotNull List<DriverGene> driverGenes, @NotNull String gene) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(gene)) {
                return driverGene.likelihoodType();
            }
        }
        return null;
    }

    @NotNull
    private static GeneEvent toCopyNumberEvent(@NotNull EventType eventType) {
        assert COPY_NUMBER_EVENTS.contains(eventType);

        switch (eventType) {
            case AMPLIFICATION:
                return GeneEvent.AMPLIFICATION;
            case OVEREXPRESSION:
                return GeneEvent.OVEREXPRESSION;
            case DELETION:
                return GeneEvent.DELETION;
            case UNDEREXPRESSION:
                return GeneEvent.UNDEREXPRESSION;
            default:
                throw new IllegalStateException("Could not convert event type to copy number event: " + eventType);
        }
    }
}
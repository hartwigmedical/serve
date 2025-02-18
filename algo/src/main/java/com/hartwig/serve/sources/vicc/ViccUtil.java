package com.hartwig.serve.sources.vicc;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspot;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.vicc.datamodel.Feature;
import com.hartwig.serve.vicc.datamodel.ViccEntry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ViccUtil {

    private static final Logger LOGGER = LogManager.getLogger(ViccUtil.class);

    private ViccUtil() {
    }

    public static void printExtractionResults(@NotNull Map<ViccEntry, ViccExtractionResult> resultsPerEntry) {
        List<Feature> featuresWithoutGenomicEvents = Lists.newArrayList();

        int totalFeatureCount = 0;
        int featuresWithVariantsCount = 0;
        int totalVariantsCount = 0;
        int featuresWithCodonCount = 0;
        int totalCodonCount = 0;
        int featuresWithExonCount = 0;
        int totalExonCount = 0;

        int featuresWithGeneLevelEventCount = 0;
        int featuresWithCopyNumberCount = 0;
        int featuresWithFusionCount = 0;
        int featuresWithCharacteristicCount = 0;

        for (Map.Entry<ViccEntry, ViccExtractionResult> resultPerEntry : resultsPerEntry.entrySet()) {
            ViccEntry entry = resultPerEntry.getKey();
            ViccExtractionResult result = resultPerEntry.getValue();
            for (Feature feature : entry.features()) {
                List<VariantHotspot> variantsForFeature = result.variantsPerFeature().get(feature);
                List<CodonAnnotation> codonsForFeature = result.codonsPerFeature().get(feature);
                List<ExonAnnotation> exonsForFeature = result.exonsPerFeature().get(feature);
                GeneAnnotation geneAnnotationForFeature = result.geneLevelEventsPerFeature().get(feature);
                GeneAnnotation ampDelForFeature = result.ampsDelsPerFeature().get(feature);
                FusionPair fusionForFeature = result.fusionsPerFeature().get(feature);
                TumorCharacteristic characteristicForFeature = result.characteristicsPerFeature().get(feature);

                if (variantsForFeature == null && codonsForFeature == null && exonsForFeature == null && geneAnnotationForFeature == null
                        && ampDelForFeature == null && fusionForFeature == null && characteristicForFeature == null) {
                    if (feature.type() != EventType.COMBINED && feature.type() != EventType.COMPLEX) {
                        // For both combined and complex events we expect no genomic events to be derived.
                        featuresWithoutGenomicEvents.add(feature);
                    }
                } else {
                    if (variantsForFeature != null) {
                        featuresWithVariantsCount++;
                        totalVariantsCount += variantsForFeature.size();
                    }

                    if (codonsForFeature != null) {
                        featuresWithCodonCount++;
                        totalCodonCount += codonsForFeature.size();
                    }

                    if (exonsForFeature != null) {
                        featuresWithExonCount++;
                        totalExonCount += exonsForFeature.size();
                    }

                    if (geneAnnotationForFeature != null) {
                        featuresWithGeneLevelEventCount++;
                    }

                    if (ampDelForFeature != null) {
                        featuresWithCopyNumberCount++;
                    }

                    if (fusionForFeature != null) {
                        featuresWithFusionCount++;
                    }

                    if (characteristicForFeature != null) {
                        featuresWithCharacteristicCount++;
                    }
                }

                totalFeatureCount++;
            }
        }

        if (!featuresWithoutGenomicEvents.isEmpty()) {
            LOGGER.warn("No genomic events derived for {} features!", featuresWithoutGenomicEvents.size());
            for (Feature feature : featuresWithoutGenomicEvents) {
                LOGGER.warn(" No genomic events derived from '{}' in '{}'", feature.name(), feature.geneSymbol());
            }
        }

        LOGGER.info("Analysis performed on {} features in {} VICC entries", totalFeatureCount, resultsPerEntry.size());
        LOGGER.info(" Extracted {} variants for {} features", totalVariantsCount, featuresWithVariantsCount);
        LOGGER.info(" Extracted {} codons for {} features", totalCodonCount, featuresWithCodonCount);
        LOGGER.info(" Extracted {} exons for {} features", totalExonCount, featuresWithExonCount);
        LOGGER.info(" Extracted {} gene level events", featuresWithGeneLevelEventCount);
        LOGGER.info(" Extracted {} known amps and dels", featuresWithCopyNumberCount);
        LOGGER.info(" Extracted {} known fusions pairs", featuresWithFusionCount);
        LOGGER.info(" Extracted {} tumor characteristics", featuresWithCharacteristicCount);
    }
}

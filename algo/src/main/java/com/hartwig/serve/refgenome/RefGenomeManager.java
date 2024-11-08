package com.hartwig.serve.refgenome;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.refgenome.liftover.LiftOverAlgo;
import com.hartwig.serve.refgenome.liftover.UCSCLiftOver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class RefGenomeManager {

    private static final Logger LOGGER = LogManager.getLogger(RefGenomeManager.class);

    @NotNull
    private final Map<RefGenome, RefGenomeResource> refGenomeResourceMap;
    @NotNull
    private final ConversionFilter conversionFilter;

    public RefGenomeManager(@NotNull final Map<RefGenome, RefGenomeResource> refGenomeResourceMap) {
        this.refGenomeResourceMap = refGenomeResourceMap;
        this.conversionFilter = new ConversionFilter();
    }

    @NotNull
    public RefGenomeResource pickResourceForKnowledgebase(@NotNull Knowledgebase knowledgebase) {
        return checkedRetrieve(knowledgebase.refGenomeVersion());
    }

    @NotNull
    public IndexedFastaSequenceFile refSequenceForRefGenome(@NotNull RefGenome version) {
        return checkedRetrieve(version).refSequence();
    }

    public void evaluate() {
        evaluateProteinResolving();

        conversionFilter.reportUnusedFilterEntries();
    }

    @NotNull
    public Map<RefGenome, ExtractionResult> makeVersioned(@NotNull List<ExtractionResult> extractions) {
        return refGenomeResourceMap.keySet().stream().map(version -> {
            LOGGER.info("Creating extraction results for ref genome version {}", version);
            List<ExtractionResult> converted =
                    extractions.stream().map(extractionResult -> convert(extractionResult, version)).collect(Collectors.toList());
            return Map.entry(version, ExtractionFunctions.merge(converted));
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    private ExtractionResult convert(@NotNull ExtractionResult extraction, @NotNull RefGenome targetVersion) {
        RefGenome sourceVersion = extraction.refGenomeVersion();
        if (sourceVersion == targetVersion) {
            return extraction;
        }

        RefGenomeResource sourceResource = checkedRetrieve(sourceVersion);
        IndexedFastaSequenceFile targetSequence = refSequenceForRefGenome(targetVersion);
        String chainFromSourceToTarget = sourceResource.chainToOtherRefGenomeMap().get(targetVersion);

        LiftOverAlgo liftOverAlgo = UCSCLiftOver.fromChainFile(chainFromSourceToTarget, targetVersion);
        RefGenomeConverter converter = new RefGenomeConverter(sourceVersion, targetVersion, targetSequence, liftOverAlgo);
        ExtractionResult filteredExtraction = conversionFilter.filter(extraction);

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(targetVersion)
                .eventInterpretations(filteredExtraction.eventInterpretations())
                .knownEvents(converter.convertKnownEvents(filteredExtraction.knownEvents()))
                .efficacyEvidences(converter.convertEfficacyEvidences(filteredExtraction.efficacyEvidences()))
                .trials(converter.convertTrials(filteredExtraction.trials()))
                .build();
    }

    @NotNull
    private RefGenomeResource checkedRetrieve(@NotNull RefGenome version) {
        RefGenomeResource resource = refGenomeResourceMap.get(version);
        if (resource == null) {
            throw new IllegalStateException("No ref genome resources found for ref genome version " + version);
        }
        return resource;
    }

    private void evaluateProteinResolving() {
        for (Map.Entry<RefGenome, RefGenomeResource> entry : refGenomeResourceMap.entrySet()) {
            RefGenome version = entry.getKey();
            RefGenomeResource resource = entry.getValue();
            Set<String> unresolvedProteinAnnotations = resource.proteinResolver().unresolvedProteinAnnotations();
            if (!unresolvedProteinAnnotations.isEmpty()) {
                LOGGER.warn("Protein resolver {} could not resolve {} protein annotations", version, unresolvedProteinAnnotations.size());
                for (String unresolvedProteinAnnotation : unresolvedProteinAnnotations) {
                    LOGGER.warn("Protein resolver {} could not resolve protein annotation '{}'", version, unresolvedProteinAnnotation);
                }
            } else {
                LOGGER.debug("Protein resolver {} observed no issues when resolving hotspots", version);
            }
        }
    }
}

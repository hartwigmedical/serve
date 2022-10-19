package com.hartwig.serve.extraction;

import java.io.IOException;

import com.hartwig.serve.common.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.actionability.characteristic.ActionableCharacteristicFile;
import com.hartwig.serve.datamodel.actionability.fusion.ActionableFusionFile;
import com.hartwig.serve.datamodel.actionability.gene.ActionableGeneFile;
import com.hartwig.serve.datamodel.actionability.hotspot.ActionableHotspotFile;
import com.hartwig.serve.datamodel.actionability.immuno.ActionableHLAFile;
import com.hartwig.serve.datamodel.actionability.range.ActionableRangeFile;
import com.hartwig.serve.extraction.codon.KnownCodonFile;
import com.hartwig.serve.extraction.copynumber.KnownCopyNumberFile;
import com.hartwig.serve.extraction.events.EventInterpretationFile;
import com.hartwig.serve.extraction.exon.KnownExonFile;
import com.hartwig.serve.extraction.fusion.KnownFusionPairFile;
import com.hartwig.serve.extraction.hotspot.KnownHotspotFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class ExtractionResultWriter {

    private static final Logger LOGGER = LogManager.getLogger(ExtractionResultWriter.class);

    @NotNull
    private final String outputDir;
    @NotNull
    private final RefGenomeVersion refGenomeVersion;
    @NotNull
    private final IndexedFastaSequenceFile refSequence;

    public ExtractionResultWriter(@NotNull final String outputDir, @NotNull final RefGenomeVersion refGenomeVersion,
            @NotNull final IndexedFastaSequenceFile refSequence) {
        this.outputDir = outputDir;
        this.refGenomeVersion = refGenomeVersion;
        this.refSequence = refSequence;
    }

    public void write(@NotNull ExtractionResult result) throws IOException {
        LOGGER.info("Writing SERVE output to {}", outputDir);

        String hotspotVcf = KnownHotspotFile.knownHotspotVcfPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} known hotspots to {}", result.knownHotspots().size(), hotspotVcf);
        KnownHotspotFile.write(hotspotVcf, refSequence, result.knownHotspots());

        String codonTsv = KnownCodonFile.knownCodonTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} known codons to {}", result.knownCodons().size(), codonTsv);
        KnownCodonFile.write(codonTsv, result.knownCodons());

        String exonTsv = KnownExonFile.knownExonTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} known exons to {}", result.knownExons().size(), exonTsv);
        KnownExonFile.write(exonTsv, result.knownExons());

        String copyNumberTsv = KnownCopyNumberFile.knownCopyNumberTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} known copy numbers to {}", result.knownCopyNumbers().size(), copyNumberTsv);
        KnownCopyNumberFile.write(copyNumberTsv, result.knownCopyNumbers());

        String fusionPairTsv = KnownFusionPairFile.knownFusionPairTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} known fusion pairs to {}", result.knownFusionPairs().size(), fusionPairTsv);
        KnownFusionPairFile.write(fusionPairTsv, result.knownFusionPairs());

        String actionableHotspotTsv = ActionableHotspotFile.actionableHotspotTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} actionable hotspots to {}", result.actionableHotspots().size(), actionableHotspotTsv);
        ActionableHotspotFile.write(actionableHotspotTsv, result.actionableHotspots());

        String actionableRangeTsv = ActionableRangeFile.actionableRangeTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} actionable ranges to {}", result.actionableRanges().size(), actionableRangeTsv);
        ActionableRangeFile.write(actionableRangeTsv, result.actionableRanges());

        String actionableGeneTsv = ActionableGeneFile.actionableGeneTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} actionable genes to {}", result.actionableGenes().size(), actionableGeneTsv);
        ActionableGeneFile.write(actionableGeneTsv, result.actionableGenes());

        String actionableFusionTsv = ActionableFusionFile.actionableFusionTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} actionable fusions to {}", result.actionableFusions().size(), actionableFusionTsv);
        ActionableFusionFile.write(actionableFusionTsv, result.actionableFusions());

        String actionableCharacteristicTsv = ActionableCharacteristicFile.actionableCharacteristicTsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} actionable tumor characteristics to {}",
                result.actionableCharacteristics().size(),
                actionableCharacteristicTsv);
        ActionableCharacteristicFile.write(actionableCharacteristicTsv, result.actionableCharacteristics());

        String actionableHLATsv = ActionableHLAFile.actionableHLATsvPath(outputDir, refGenomeVersion);
        LOGGER.info(" Writing {} actionable hla to {}", result.actionableHLA().size(), actionableHLATsv);
        ActionableHLAFile.write(actionableHLATsv, result.actionableHLA());

        String eventInterpretationTsv = EventInterpretationFile.eventInterpretationTsv(outputDir);
        LOGGER.info(" Writing {} event interpretations to {}", result.eventInterpretations().size(), eventInterpretationTsv);
        EventInterpretationFile.write(eventInterpretationTsv, result.eventInterpretations());
    }
}
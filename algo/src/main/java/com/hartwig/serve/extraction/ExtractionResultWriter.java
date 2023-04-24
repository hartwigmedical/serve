package com.hartwig.serve.extraction;

import java.io.IOException;

import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.serialization.ActionableCharacteristicFile;
import com.hartwig.serve.datamodel.serialization.ActionableFusionFile;
import com.hartwig.serve.datamodel.serialization.ActionableGeneFile;
import com.hartwig.serve.datamodel.serialization.ActionableHLAFile;
import com.hartwig.serve.datamodel.serialization.ActionableHotspotFile;
import com.hartwig.serve.datamodel.serialization.ActionableRangeFile;
import com.hartwig.serve.datamodel.serialization.KnownCodonFile;
import com.hartwig.serve.datamodel.serialization.KnownCopyNumberFile;
import com.hartwig.serve.datamodel.serialization.KnownExonFile;
import com.hartwig.serve.datamodel.serialization.KnownFusionFile;
import com.hartwig.serve.datamodel.serialization.KnownGeneFile;
import com.hartwig.serve.datamodel.serialization.KnownHotspotFile;
import com.hartwig.serve.extraction.events.EventInterpretationFile;
import com.hartwig.serve.extraction.hotspot.KnownHotspotVCF;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class ExtractionResultWriter {

    private static final Logger LOGGER = LogManager.getLogger(ExtractionResultWriter.class);

    @NotNull
    private final String outputDir;
    @NotNull
    private final RefGenome refGenome;
    @NotNull
    private final IndexedFastaSequenceFile refSequence;

    public ExtractionResultWriter(@NotNull final String outputDir, @NotNull final RefGenome refGenome,
            @NotNull final IndexedFastaSequenceFile refSequence) {
        this.outputDir = outputDir;
        this.refGenome = refGenome;
        this.refSequence = refSequence;
    }

    public void write(@NotNull ExtractionResult result) throws IOException {
        LOGGER.info("Writing SERVE output to {}", outputDir);

        // We also write a hotspot VCF to be used in SAGE.
        String hotspotVcf = KnownHotspotVCF.knownHotspotVcfPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known hotspots to {}", result.knownHotspots().size(), hotspotVcf);
        KnownHotspotVCF.write(hotspotVcf, refSequence, result.knownHotspots());

        String hotspotTsv = KnownHotspotFile.knownHotspotTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known hotspots to {}", result.knownHotspots().size(), hotspotTsv);
        KnownHotspotFile.write(hotspotTsv, result.knownHotspots());

        String codonTsv = KnownCodonFile.knownCodonTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known codons to {}", result.knownCodons().size(), codonTsv);
        KnownCodonFile.write(codonTsv, result.knownCodons());

        String exonTsv = KnownExonFile.knownExonTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known exons to {}", result.knownExons().size(), exonTsv);
        KnownExonFile.write(exonTsv, result.knownExons());

        String geneTsv = KnownGeneFile.knownGeneTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known genes to {}", result.knownGenes().size(), geneTsv);
        KnownGeneFile.write(geneTsv, result.knownGenes());

        String copyNumberTsv = KnownCopyNumberFile.knownCopyNumberTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known copy numbers to {}", result.knownCopyNumbers().size(), copyNumberTsv);
        KnownCopyNumberFile.write(copyNumberTsv, result.knownCopyNumbers());

        String fusionTsv = KnownFusionFile.knownFusionTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known fusions to {}", result.knownFusions().size(), fusionTsv);
        KnownFusionFile.write(fusionTsv, result.knownFusions());

        String actionableHotspotTsv = ActionableHotspotFile.actionableHotspotTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} actionable hotspots to {}", result.actionableHotspots().size(), actionableHotspotTsv);
        ActionableHotspotFile.write(actionableHotspotTsv, result.actionableHotspots());

        String actionableRangeTsv = ActionableRangeFile.actionableRangeTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} actionable ranges to {}", result.actionableRanges().size(), actionableRangeTsv);
        ActionableRangeFile.write(actionableRangeTsv, result.actionableRanges());

        String actionableGeneTsv = ActionableGeneFile.actionableGeneTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} actionable genes to {}", result.actionableGenes().size(), actionableGeneTsv);
        ActionableGeneFile.write(actionableGeneTsv, result.actionableGenes());

        String actionableFusionTsv = ActionableFusionFile.actionableFusionTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} actionable fusions to {}", result.actionableFusions().size(), actionableFusionTsv);
        ActionableFusionFile.write(actionableFusionTsv, result.actionableFusions());

        String actionableCharacteristicTsv = ActionableCharacteristicFile.actionableCharacteristicTsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} actionable tumor characteristics to {}",
                result.actionableCharacteristics().size(),
                actionableCharacteristicTsv);
        ActionableCharacteristicFile.write(actionableCharacteristicTsv, result.actionableCharacteristics());

        String actionableHLATsv = ActionableHLAFile.actionableHLATsvPath(outputDir, refGenome);
        LOGGER.info(" Writing {} actionable hla to {}", result.actionableHLA().size(), actionableHLATsv);
        ActionableHLAFile.write(actionableHLATsv, result.actionableHLA());

        String eventInterpretationTsv = EventInterpretationFile.eventInterpretationTsv(outputDir);
        LOGGER.info(" Writing {} event interpretations to {}", result.eventInterpretations().size(), eventInterpretationTsv);
        EventInterpretationFile.write(eventInterpretationTsv, result.eventInterpretations());
    }
}
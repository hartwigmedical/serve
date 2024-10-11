package com.hartwig.serve.extraction;

import java.io.IOException;

import com.hartwig.serve.datamodel.ImmutableActionableEvents;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.ImmutableServeRecord;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.serialization.ServeJson;
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
    @NotNull
    private final String serveVersion;

    public ExtractionResultWriter(
            @NotNull final String outputDir,
            @NotNull final RefGenome refGenome,
            @NotNull final IndexedFastaSequenceFile refSequence,
            @NotNull String serveVersion
    ) {
        this.outputDir = outputDir;
        this.refGenome = refGenome;
        this.refSequence = refSequence;
        this.serveVersion = serveVersion;
    }

    public void write(@NotNull ExtractionResult result) throws IOException {
        LOGGER.info("Writing SERVE output to {}", outputDir);

        // We also write a hotspot VCF to be used in SAGE.
        String hotspotVcf = KnownHotspotVCF.knownHotspotVcfPath(outputDir, refGenome);
        LOGGER.info(" Writing {} known hotspots to {}", result.knownHotspots().size(), hotspotVcf);
        KnownHotspotVCF.write(hotspotVcf, refSequence, result.knownHotspots());

        String eventInterpretationTsv = EventInterpretationFile.eventInterpretationTsv(outputDir);
        LOGGER.info(" Writing {} event interpretations to {}", result.eventInterpretations().size(), eventInterpretationTsv);
        EventInterpretationFile.write(eventInterpretationTsv, result.eventInterpretations());

        ServeRecord serveRecord = toServeRecord(result);
        String filepath = ServeJson.jsonFilePath(outputDir, refGenome);
        LOGGER.info(" Writing {}", filepath);
        ServeJson.write(serveRecord, filepath);
    }

    @NotNull
    private ServeRecord toServeRecord(@NotNull ExtractionResult result) {
        return ImmutableServeRecord.builder()
                .refGenomeVersion(result.refGenomeVersion())
                .serveVersion(serveVersion)
                .knownEvents(ImmutableKnownEvents.builder()
                        .hotspots(result.knownHotspots())
                        .codons(result.knownCodons())
                        .exons(result.knownExons())
                        .genes(result.knownGenes())
                        .copyNumbers(result.knownCopyNumbers())
                        .fusions(result.knownFusions())
                        .build())
                .actionableEvents(ImmutableActionableEvents.builder()
                        .hotspots(result.actionableHotspots())
                        .codons(result.actionableCodons())
                        .exons(result.actionableExons())
                        .genes(result.actionableGenes())
                        .fusions(result.actionableFusions())
                        .characteristics(result.actionableCharacteristics())
                        .hla(result.actionableHLA())
                        .build())
                .build();
    }
}
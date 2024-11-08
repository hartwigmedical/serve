package com.hartwig.serve.extraction;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.ImmutableServeDatabase;
import com.hartwig.serve.datamodel.ImmutableServeRecord;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.serialization.ServeJson;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.EventInterpretationFile;
import com.hartwig.serve.extraction.hotspot.KnownHotspotVCF;
import com.hartwig.serve.refgenome.RefGenomeManager;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ExtractionResultWriter {

    private static final Logger LOGGER = LogManager.getLogger(ExtractionResultWriter.class);

    @NotNull
    private final String serveVersion;
    @NotNull
    private final RefGenomeManager refGenomeManager;
    @NotNull
    private final String outputDir;

    public ExtractionResultWriter(@NotNull final String serveVersion, @NotNull final RefGenomeManager refGenomeManager,
            @NotNull final String outputDir) {
        this.serveVersion = serveVersion;
        this.refGenomeManager = refGenomeManager;
        this.outputDir = outputDir;
    }

    public void write(@NotNull Map<RefGenome, ExtractionResult> resultMap) throws IOException {
        LOGGER.info("Writing SERVE output to {}", outputDir);

        // Hotspot VCF is generated to be used in SAGE for every version of ref genome.
        for (Map.Entry<RefGenome, ExtractionResult> entry : resultMap.entrySet()) {
            RefGenome refGenome = entry.getKey();
            Set<KnownHotspot> hotspots =
                    entry.getValue().knownEvents() != null ? entry.getValue().knownEvents().hotspots() : Sets.newHashSet();
            String hotspotVcf = KnownHotspotVCF.knownHotspotVcfPath(outputDir, refGenome);
            LOGGER.info(" Writing {} known hotspots to {}", hotspots.size(), hotspotVcf);
            KnownHotspotVCF.write(hotspotVcf, refGenomeManager.refSequenceForRefGenome(refGenome), hotspots);
        }

        // Event interpretation TSV is generated for internal analysis of SERVE. We assume they are identical per ref genome.
        String eventInterpretationTsv = EventInterpretationFile.eventInterpretationTsv(outputDir);
        Set<EventInterpretation> eventInterpretations =
                !resultMap.values().isEmpty() ? resultMap.values().iterator().next().eventInterpretations() : Sets.newHashSet();
        LOGGER.info(" Writing {} event interpretations to {}", eventInterpretations.size(), eventInterpretationTsv);
        EventInterpretationFile.write(eventInterpretationTsv, eventInterpretations);

        ImmutableServeDatabase.Builder databaseBuilder = ImmutableServeDatabase.builder().version(serveVersion);

        for (Map.Entry<RefGenome, ExtractionResult> entry : resultMap.entrySet()) {
            databaseBuilder.putRecords(entry.getKey(), toServeRecord(entry.getValue()));
        }

        String filepath = ServeJson.jsonFilePath(outputDir);
        LOGGER.info(" Writing SERVE database to '{}'", filepath);
        ServeJson.write(databaseBuilder.build(), filepath);
    }

    @NotNull
    private static ServeRecord toServeRecord(@NotNull ExtractionResult result) {
        return ImmutableServeRecord.builder()
                .knownEvents(result.knownEvents() != null ? result.knownEvents() : ImmutableKnownEvents.builder().build())
                .evidences(result.evidences() != null ? result.evidences() : Lists.newArrayList())
                .trials(result.trials() != null ? result.trials() : Lists.newArrayList())
                .build();
    }
}
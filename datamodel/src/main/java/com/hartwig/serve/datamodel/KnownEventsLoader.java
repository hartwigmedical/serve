package com.hartwig.serve.datamodel;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.KnownCodonFile;
import com.hartwig.serve.datamodel.serialization.KnownCopyNumberFile;
import com.hartwig.serve.datamodel.serialization.KnownExonFile;
import com.hartwig.serve.datamodel.serialization.KnownFusionFile;
import com.hartwig.serve.datamodel.serialization.KnownHotspotFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class KnownEventsLoader {

    private static final Logger LOGGER = LogManager.getLogger(KnownEventsLoader.class);

    private KnownEventsLoader() {
    }

    @NotNull
    public static KnownEvents readFromDir(@NotNull String actionabilityDir, @NotNull RefGenomeVersion refGenomeVersion) throws IOException {
        LOGGER.info("Loading SERVE known files from {} using ref genome version '{}'", actionabilityDir, refGenomeVersion);

        String knownHotspotTsv = KnownHotspotFile.knownHotspotTsvPath(actionabilityDir, refGenomeVersion);
        List<KnownHotspot> hotspots = KnownHotspotFile.read(knownHotspotTsv);
        LOGGER.info(" Loaded {} known hotspots from {}", hotspots.size(), knownHotspotTsv);

        String knownCodonTsv = KnownCodonFile.knownCodonTsvPath(actionabilityDir, refGenomeVersion);
        List<KnownCodon> knownCodons = KnownCodonFile.read(knownCodonTsv);
        LOGGER.info(" Loaded {} known codon from {}", knownCodons.size(), knownCodonTsv);

        String knownExonTsv = KnownExonFile.knownExonTsvPath(actionabilityDir, refGenomeVersion);
        List<KnownExon> knownExons = KnownExonFile.read(knownExonTsv);
        LOGGER.info(" Loaded {} known exons from {}", knownExons.size(), knownExonTsv);

        String knownCopyNumberTsv = KnownCopyNumberFile.knownCopyNumberTsvPath(actionabilityDir, refGenomeVersion);
        List<KnownCopyNumber> knownCopyNumbers = KnownCopyNumberFile.read(knownCopyNumberTsv);
        LOGGER.info(" Loaded {} known gene copy numbers from {}", knownCopyNumbers.size(), knownCopyNumberTsv);

        String knownFusionTsv = KnownFusionFile.knownFusionTsvPath(actionabilityDir, refGenomeVersion);
        List<KnownFusion> knownFusions = KnownFusionFile.read(knownFusionTsv);
        LOGGER.info(" Loaded {} known fusions from {}", knownFusions.size(), knownFusionTsv);

        return ImmutableKnownEvents.builder()
                .hotspots(hotspots)
                .codons(knownCodons)
                .exons(knownExons)
                .copyNumbers(knownCopyNumbers)
                .fusionPairs(knownFusions)
                .build();
    }
}

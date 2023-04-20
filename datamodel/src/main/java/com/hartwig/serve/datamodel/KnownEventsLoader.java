package com.hartwig.serve.datamodel;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.serialization.KnownCodonFile;
import com.hartwig.serve.datamodel.serialization.KnownCopyNumberFile;
import com.hartwig.serve.datamodel.serialization.KnownExonFile;
import com.hartwig.serve.datamodel.serialization.KnownFusionFile;
import com.hartwig.serve.datamodel.serialization.KnownGeneFile;
import com.hartwig.serve.datamodel.serialization.KnownHotspotFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class KnownEventsLoader {

    private static final Logger LOGGER = LogManager.getLogger(KnownEventsLoader.class);

    private KnownEventsLoader() {
    }

    @NotNull
    public static KnownEvents readFromDir(@NotNull String knownEventDir, @NotNull RefGenome refGenome) throws IOException {
        LOGGER.info("Loading SERVE known files from {} using ref genome version '{}'", knownEventDir, refGenome);

        String knownHotspotTsv = KnownHotspotFile.knownHotspotTsvPath(knownEventDir, refGenome);
        List<KnownHotspot> hotspots = KnownHotspotFile.read(knownHotspotTsv);
        LOGGER.info(" Loaded {} known hotspots from {}", hotspots.size(), knownHotspotTsv);

        String knownCodonTsv = KnownCodonFile.knownCodonTsvPath(knownEventDir, refGenome);
        List<KnownCodon> codons = KnownCodonFile.read(knownCodonTsv);
        LOGGER.info(" Loaded {} known codon from {}", codons.size(), knownCodonTsv);

        String knownExonTsv = KnownExonFile.knownExonTsvPath(knownEventDir, refGenome);
        List<KnownExon> exons = KnownExonFile.read(knownExonTsv);
        LOGGER.info(" Loaded {} known exons from {}", exons.size(), knownExonTsv);

        String knownCopyNumberTsv = KnownCopyNumberFile.knownCopyNumberTsvPath(knownEventDir, refGenome);
        List<KnownCopyNumber> copyNumbers = KnownCopyNumberFile.read(knownCopyNumberTsv);
        LOGGER.info(" Loaded {} known gene copy numbers from {}", copyNumbers.size(), knownCopyNumberTsv);

        String knownFusionTsv = KnownFusionFile.knownFusionTsvPath(knownEventDir, refGenome);
        List<KnownFusion> fusions = KnownFusionFile.read(knownFusionTsv);
        LOGGER.info(" Loaded {} known fusions from {}", fusions.size(), knownFusionTsv);

        String knownGeneTsv = KnownGeneFile.knownGeneTsvPath(knownEventDir, refGenome);
        List<KnownGene> genes = KnownGeneFile.read(knownGeneTsv);
        LOGGER.info(" Loaded {} known genes from {}", fusions.size(), knownGeneTsv);

        return ImmutableKnownEvents.builder()
                .hotspots(hotspots)
                .codons(codons)
                .exons(exons)
                .genes(genes)
                .copyNumbers(copyNumbers)
                .fusions(fusions)
                .build();
    }
}

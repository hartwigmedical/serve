package com.hartwig.serve.datamodel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.serialization.ActionableCharacteristicFile;
import com.hartwig.serve.datamodel.serialization.ActionableFusionFile;
import com.hartwig.serve.datamodel.serialization.ActionableGeneFile;
import com.hartwig.serve.datamodel.serialization.ActionableHLAFile;
import com.hartwig.serve.datamodel.serialization.ActionableHotspotFile;
import com.hartwig.serve.datamodel.serialization.ActionableRangeFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ActionableEventsLoader {

    private static final Logger LOGGER = LogManager.getLogger(ActionableEventsLoader.class);

    private ActionableEventsLoader() {
    }

    @NotNull
    public static ActionableEvents readFromDir(@NotNull String actionabilityDir, @NotNull RefGenome refGenome) throws IOException {
        return readFromDir(Path.of(actionabilityDir), refGenome);
    }

    @NotNull
    public static ActionableEvents readFromDir(@NotNull Path actionabilityPath, @NotNull RefGenome refGenome) throws IOException {
        LOGGER.info("Loading SERVE actionability files from {} using ref genome version '{}'", actionabilityPath, refGenome);

        Path actionableHotspotTsv = ActionableHotspotFile.actionableHotspotTsvPath(actionabilityPath, refGenome);
        List<ActionableHotspot> hotspots = ActionableHotspotFile.read(actionableHotspotTsv);
        LOGGER.info(" Loaded {} actionable hotspots from {}", hotspots.size(), actionableHotspotTsv);

        Path actionableCodonTsv = ActionableRangeFile.actionableCodonTsvPath(actionabilityPath, refGenome);
        List<ActionableRange> codons = ActionableRangeFile.read(actionableCodonTsv);
        LOGGER.info(" Loaded {} actionable codons from {}", codons.size(), actionableCodonTsv);

        Path actionableExonTsv = ActionableRangeFile.actionableExonTsvPath(actionabilityPath, refGenome);
        List<ActionableRange> exons = ActionableRangeFile.read(actionableExonTsv);
        LOGGER.info(" Loaded {} actionable exons from {}", exons.size(), actionableExonTsv);

        Path actionableGeneTsv = ActionableGeneFile.actionableGeneTsvPath(actionabilityPath, refGenome);
        List<ActionableGene> genes = ActionableGeneFile.read(actionableGeneTsv);
        LOGGER.info(" Loaded {} actionable genes from {}", genes.size(), actionableGeneTsv);

        Path actionableFusionTsv = ActionableFusionFile.actionableFusionTsvPath(actionabilityPath, refGenome);
        List<ActionableFusion> fusions = ActionableFusionFile.read(actionableFusionTsv);
        LOGGER.info(" Loaded {} actionable fusions from {}", fusions.size(), actionableFusionTsv);

        Path actionableCharacteristicTsv = ActionableCharacteristicFile.actionableCharacteristicTsvPath(actionabilityPath, refGenome);
        List<ActionableCharacteristic> characteristics = ActionableCharacteristicFile.read(actionableCharacteristicTsv);
        LOGGER.info(" Loaded {} actionable tumor characteristics from {}", characteristics.size(), actionableCharacteristicTsv);

        Path actionableHLATsv = ActionableHLAFile.actionableHLATsvPath(actionabilityPath, refGenome);
        List<ActionableHLA> HLAs = ActionableHLAFile.read(actionableHLATsv);
        LOGGER.info(" Loaded {} actionable hla from {}", HLAs.size(), actionableHLATsv);

        return ImmutableActionableEvents.builder()
                .hotspots(hotspots)
                .codons(codons)
                .exons(exons)
                .genes(genes)
                .fusions(fusions)
                .characteristics(characteristics)
                .hla(HLAs)
                .build();
    }
}
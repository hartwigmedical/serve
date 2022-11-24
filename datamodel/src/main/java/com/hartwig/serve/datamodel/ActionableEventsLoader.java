package com.hartwig.serve.datamodel;

import java.io.IOException;
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
    public static ActionableEvents readFromDir(@NotNull String actionabilityDir, @NotNull RefGenome refGenome)
            throws IOException {
        LOGGER.info("Loading SERVE actionability files from {} using ref genome version '{}'", actionabilityDir, refGenome);

        String actionableHotspotTsv = ActionableHotspotFile.actionableHotspotTsvPath(actionabilityDir, refGenome);
        List<ActionableHotspot> hotspots = ActionableHotspotFile.read(actionableHotspotTsv);
        LOGGER.info(" Loaded {} actionable hotspots from {}", hotspots.size(), actionableHotspotTsv);

        String actionableRangeTsv = ActionableRangeFile.actionableRangeTsvPath(actionabilityDir, refGenome);
        List<ActionableRange> ranges = ActionableRangeFile.read(actionableRangeTsv);
        LOGGER.info(" Loaded {} actionable ranges from {}", ranges.size(), actionableRangeTsv);

        String actionableGeneTsv = ActionableGeneFile.actionableGeneTsvPath(actionabilityDir, refGenome);
        List<ActionableGene> genes = ActionableGeneFile.read(actionableGeneTsv);
        LOGGER.info(" Loaded {} actionable genes from {}", genes.size(), actionableGeneTsv);

        String actionableFusionTsv = ActionableFusionFile.actionableFusionTsvPath(actionabilityDir, refGenome);
        List<ActionableFusion> fusions = ActionableFusionFile.read(actionableFusionTsv);
        LOGGER.info(" Loaded {} actionable fusions from {}", fusions.size(), actionableFusionTsv);

        String actionableCharacteristicTsv =
                ActionableCharacteristicFile.actionableCharacteristicTsvPath(actionabilityDir, refGenome);
        List<ActionableCharacteristic> characteristics = ActionableCharacteristicFile.read(actionableCharacteristicTsv);
        LOGGER.info(" Loaded {} actionable tumor characteristics from {}", characteristics.size(), actionableCharacteristicTsv);

        String actionableHLATsv = ActionableHLAFile.actionableHLATsvPath(actionabilityDir, refGenome);
        List<ActionableHLA> HLAs = ActionableHLAFile.read(actionableHLATsv);
        LOGGER.info(" Loaded {} actionable hla from {}", HLAs.size(), actionableHLATsv);

        return ImmutableActionableEvents.builder()
                .hotspots(hotspots)
                .ranges(ranges)
                .genes(genes)
                .fusions(fusions)
                .characteristics(characteristics)
                .hla(HLAs)
                .build();
    }
}
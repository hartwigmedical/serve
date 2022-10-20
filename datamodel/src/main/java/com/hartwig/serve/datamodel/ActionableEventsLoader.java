package com.hartwig.serve.datamodel;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristicFile;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ActionableFusionFile;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.ActionableGeneFile;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspotFile;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ActionableHLAFile;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ActionableRangeFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ActionableEventsLoader {

    private static final Logger LOGGER = LogManager.getLogger(ActionableEventsLoader.class);

    private ActionableEventsLoader() {
    }

    @NotNull
    public static ActionableEvents readFromDir(@NotNull String actionabilityDir, @NotNull RefGenomeVersion refGenomeVersion)
            throws IOException {
        LOGGER.info("Loading SERVE actionability files from {} using ref genome version '{}'", actionabilityDir, refGenomeVersion);

        String actionableHotspotTsv = ActionableHotspotFile.actionableHotspotTsvPath(actionabilityDir, refGenomeVersion);
        List<ActionableHotspot> hotspots = ActionableHotspotFile.read(actionableHotspotTsv);
        LOGGER.info(" Loaded {} actionable hotspots from {}", hotspots.size(), actionableHotspotTsv);

        String actionableRangeTsv = ActionableRangeFile.actionableRangeTsvPath(actionabilityDir, refGenomeVersion);
        List<ActionableRange> ranges = ActionableRangeFile.read(actionableRangeTsv);
        LOGGER.info(" Loaded {} actionable ranges from {}", ranges.size(), actionableRangeTsv);

        String actionableGeneTsv = ActionableGeneFile.actionableGeneTsvPath(actionabilityDir, refGenomeVersion);
        List<ActionableGene> genes = ActionableGeneFile.read(actionableGeneTsv);
        LOGGER.info(" Loaded {} actionable genes from {}", genes.size(), actionableGeneTsv);

        String actionableFusionTsv = ActionableFusionFile.actionableFusionTsvPath(actionabilityDir, refGenomeVersion);
        List<ActionableFusion> fusions = ActionableFusionFile.read(actionableFusionTsv);
        LOGGER.info(" Loaded {} actionable fusions from {}", fusions.size(), actionableFusionTsv);

        String actionableCharacteristicTsv =
                ActionableCharacteristicFile.actionableCharacteristicTsvPath(actionabilityDir, refGenomeVersion);
        List<ActionableCharacteristic> characteristics = ActionableCharacteristicFile.read(actionableCharacteristicTsv);
        LOGGER.info(" Loaded {} actionable tumor characteristics from {}", characteristics.size(), actionableCharacteristicTsv);

        String actionableHLATsv = ActionableHLAFile.actionableHLATsvPath(actionabilityDir, refGenomeVersion);
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
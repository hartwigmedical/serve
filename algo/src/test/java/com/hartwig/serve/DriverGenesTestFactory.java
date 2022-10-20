package com.hartwig.serve;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.drivercatalog.DriverCategory;
import com.hartwig.serve.common.drivercatalog.panel.DriverGene;
import com.hartwig.serve.common.drivercatalog.panel.DriverGeneGermlineReporting;
import com.hartwig.serve.common.drivercatalog.panel.ImmutableDriverGene;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DriverGenesTestFactory {

    private DriverGenesTestFactory() {
    }

    @NotNull
    public static List<DriverGene> createDriverGenes(@NotNull String geneTsg, @NotNull String geneOnco) {
        DriverGene driverGeneTsg = builder().gene(geneTsg).likelihoodType(DriverCategory.TSG).build();
        DriverGene driverGeneOnco = builder().gene(geneOnco).likelihoodType(DriverCategory.ONCO).build();

        return Lists.newArrayList(driverGeneTsg, driverGeneOnco);
    }

    @NotNull
    private static ImmutableDriverGene.Builder builder() {
        return ImmutableDriverGene.builder()
                .gene(Strings.EMPTY)
                .reportMissenseAndInframe(false)
                .reportNonsenseAndFrameshift(false)
                .reportSplice(false)
                .reportDeletion(false)
                .reportDisruption(false)
                .reportAmplification(false)
                .reportSomaticHotspot(false)
                .reportGermlineVariant(DriverGeneGermlineReporting.NONE)
                .reportGermlineHotspot(DriverGeneGermlineReporting.NONE)
                .likelihoodType(DriverCategory.ONCO)
                .reportGermlineDisruption(false)
                .reportPGX(false);
    }
}
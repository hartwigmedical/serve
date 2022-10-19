package com.hartwig.serve;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.drivercatalog.DriverCategory;
import com.hartwig.serve.common.drivercatalog.panel.DriverGene;
import com.hartwig.serve.common.drivercatalog.panel.DriverGeneTestFactory;

import org.jetbrains.annotations.NotNull;

public final class DriverGenesTestFactory {

    private DriverGenesTestFactory() {
    }

    @NotNull
    public static List<DriverGene> createDriverGenes(@NotNull String geneTsg, @NotNull String geneOnco) {
        DriverGene driverGeneTsg = DriverGeneTestFactory.builder().gene(geneTsg).likelihoodType(DriverCategory.TSG).build();
        DriverGene driverGeneOnco = DriverGeneTestFactory.builder().gene(geneOnco).likelihoodType(DriverCategory.ONCO).build();

        return Lists.newArrayList(driverGeneTsg, driverGeneOnco);
    }
}
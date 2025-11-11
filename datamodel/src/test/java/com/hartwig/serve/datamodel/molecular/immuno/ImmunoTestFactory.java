package com.hartwig.serve.datamodel.molecular.immuno;

import com.hartwig.serve.datamodel.molecular.MolecularTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ImmunoTestFactory {

    private ImmunoTestFactory() {
    }

    @NotNull
    public static ImmutableActionableHLA.Builder actionableHLABuilder() {
        return ImmutableActionableHLA.builder()
                .from(MolecularTestFactory.createTestActionableEvent())
                .gene(Strings.EMPTY)
                .alleleGroup(Strings.EMPTY)
                .hlaProtein(Strings.EMPTY);
    }
}

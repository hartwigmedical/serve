package com.hartwig.serve.datamodel.immuno;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ImmunoTestFactory {

    private ImmunoTestFactory() {
    }

    @NotNull
    public static ImmutableActionableHLA.Builder actionableHLABuilder() {
        return ImmutableActionableHLA.builder().from(DatamodelTestFactory.createEmptyActionableEvent()).hlaAllele(Strings.EMPTY);
    }

    @NotNull
    public static ActionableHLA createTestActionableImmunoHLAForSource(@NotNull Knowledgebase source) {
        return actionableHLABuilder().source(source).build();
    }
}

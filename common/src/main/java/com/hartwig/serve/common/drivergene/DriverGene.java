package com.hartwig.serve.common.drivergene;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface DriverGene extends Comparable<DriverGene> {

    @NotNull
    String gene();

    boolean reportMissenseAndInframe();

    boolean reportNonsenseAndFrameshift();

    boolean reportSplice();

    boolean reportDeletion();

    boolean reportDisruption();

    boolean reportAmplification();

    boolean reportSomaticHotspot();

    @NotNull
    DriverGeneGermlineReporting reportGermlineVariant();

    @NotNull
    DriverGeneGermlineReporting reportGermlineHotspot();

    @NotNull
    DriverCategory likelihoodType();

    boolean reportGermlineDisruption();

    List<String> additionalReportedTranscripts();

    boolean reportPGX();

    @Override
    default int compareTo(@NotNull final DriverGene o) {
        return gene().compareTo(o.gene());
    }
}

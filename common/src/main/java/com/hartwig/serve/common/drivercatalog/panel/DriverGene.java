package com.hartwig.serve.common.drivercatalog.panel;

import java.util.List;

import com.hartwig.serve.common.drivercatalog.DriverCategory;

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

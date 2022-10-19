package com.hartwig.serve.ckb.datamodel;

import java.time.LocalDate;
import java.util.List;

import com.hartwig.serve.ckb.classification.CkbEventTypeExtractor;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.evidence.Evidence;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.serve.classification.EventType;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbEntry {

    public abstract int profileId();

    @NotNull
    @Value.Derived
    public EventType type() {
        return CkbEventTypeExtractor.classify(this);
    }

    @NotNull
    public abstract LocalDate createDate();

    @NotNull
    public abstract LocalDate updateDate();

    @NotNull
    public abstract String profileName();

    @NotNull
    public abstract List<Variant> variants();

    @NotNull
    public abstract List<Evidence> evidences();

    @NotNull
    public abstract List<ClinicalTrial> clinicalTrials();
}
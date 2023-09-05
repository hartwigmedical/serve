package com.hartwig.serve.ckb.json.reference;

import java.util.List;

import com.hartwig.serve.ckb.json.CkbJsonObject;
import com.hartwig.serve.ckb.json.common.DrugInfo;
import com.hartwig.serve.ckb.json.common.EvidenceInfo;
import com.hartwig.serve.ckb.json.common.GeneInfo;
import com.hartwig.serve.ckb.json.common.TherapyInfo;
import com.hartwig.serve.ckb.json.common.TreatmentApproachInfo;
import com.hartwig.serve.ckb.json.common.VariantInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonReference implements CkbJsonObject {

    public abstract int id();

    @Nullable
    public abstract String pubMedId();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String shortJournalTitle();

    @Nullable
    public abstract String pages();

    @Nullable
    public abstract String url();

    @Nullable
    public abstract String authors();

    @Nullable
    public abstract String journal();

    @Nullable
    public abstract String volume();

    @Nullable
    public abstract String issue();

    @Nullable
    public abstract String date();

    @Nullable
    public abstract String abstractText();

    @Nullable
    public abstract String year();

    @NotNull
    public abstract List<DrugInfo> drugs();

    @NotNull
    public abstract List<GeneInfo> genes();

    @NotNull
    public abstract List<EvidenceInfo> evidence();

    @NotNull
    public abstract List<TherapyInfo> therapies();

    @NotNull
    public abstract List<TreatmentApproachInfo> treatmentApproaches();

    @NotNull
    public abstract List<VariantInfo> variants();
}

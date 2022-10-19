package com.hartwig.serve.ckb.json.drug;

import java.time.LocalDate;
import java.util.List;

import com.hartwig.serve.ckb.json.CkbJsonObject;
import com.hartwig.serve.ckb.json.common.ClinicalTrialInfo;
import com.hartwig.serve.ckb.json.common.DescriptionInfo;
import com.hartwig.serve.ckb.json.common.DrugClassInfo;
import com.hartwig.serve.ckb.json.common.EvidenceInfo;
import com.hartwig.serve.ckb.json.common.GlobalApprovalStatusInfo;
import com.hartwig.serve.ckb.json.common.TherapyInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonDrug implements CkbJsonObject {

    public abstract int id();

    @NotNull
    public abstract String drugName();

    @NotNull
    public abstract List<String> terms();

    @NotNull
    public abstract List<String> synonyms();

    @Nullable
    public abstract String tradeName();

    @NotNull
    public abstract List<DescriptionInfo> descriptions();

    @NotNull
    public abstract List<DrugClassInfo> drugClasses();

    @Nullable
    public abstract String casRegistryNum();

    @Nullable
    public abstract String ncitId();

    @NotNull
    public abstract LocalDate createDate();

    @NotNull
    public abstract List<ClinicalTrialInfo> clinicalTrials();

    @NotNull
    public abstract List<EvidenceInfo> evidence();

    @NotNull
    public abstract List<TherapyInfo> therapies();

    @Nullable
    public abstract List<GlobalApprovalStatusInfo> globalApprovalStatus();
}

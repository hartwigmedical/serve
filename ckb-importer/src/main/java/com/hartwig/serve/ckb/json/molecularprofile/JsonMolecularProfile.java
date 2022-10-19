package com.hartwig.serve.ckb.json.molecularprofile;

import java.time.LocalDate;
import java.util.List;

import com.hartwig.serve.ckb.json.CkbJsonObject;
import com.hartwig.serve.ckb.json.common.ClinicalTrialInfo;
import com.hartwig.serve.ckb.json.common.TreatmentApproachInfo;
import com.hartwig.serve.ckb.json.common.VariantInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonMolecularProfile implements CkbJsonObject {

    public abstract int id();

    @NotNull
    public abstract String profileName();

    @NotNull
    public abstract List<VariantInfo> geneVariants();

    @NotNull
    public abstract List<TreatmentApproachInfo> treatmentApproaches();

    @NotNull
    public abstract LocalDate createDate();

    @NotNull
    public abstract LocalDate updateDate();

    @NotNull
    public abstract JsonMolecularProfileExtendedEvidence complexMolecularProfileEvidence();

    @NotNull
    public abstract JsonMolecularProfileExtendedEvidence treatmentApproachEvidence();

    @NotNull
    public abstract List<ClinicalTrialInfo> variantAssociatedClinicalTrials();

    @NotNull
    public abstract JsonMolecularProfileExtendedEvidence variantLevelEvidence();

    @NotNull
    public abstract JsonMolecularProfileExtendedEvidence extendedEvidence();
}

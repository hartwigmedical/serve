package com.hartwig.serve.ckb.json.clinicaltrial;

import com.hartwig.serve.ckb.json.common.MolecularProfileInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonVariantRequirementDetail {

    @NotNull
    public abstract MolecularProfileInfo molecularProfile();

    @NotNull
    public abstract String requirementType();
}

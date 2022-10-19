package com.hartwig.serve.ckb.json.variant;

import com.hartwig.serve.ckb.json.common.GeneInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonVariantPartnerGene {

    @NotNull
    public abstract GeneInfo gene();
}

package com.hartwig.serve.ckb.json.variant;

import java.util.List;

import com.hartwig.serve.ckb.json.common.VariantInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonCategoryVariantPath {

    @NotNull
    public abstract String variantPath();

    @NotNull
    public abstract List<VariantInfo> variants();
}

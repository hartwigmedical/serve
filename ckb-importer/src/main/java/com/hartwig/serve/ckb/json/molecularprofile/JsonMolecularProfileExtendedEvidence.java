package com.hartwig.serve.ckb.json.molecularprofile;

import java.util.List;

import com.hartwig.serve.ckb.json.common.EvidenceInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonMolecularProfileExtendedEvidence {

    public abstract int totalCount();

    @NotNull
    public abstract List<EvidenceInfo> evidences();
}

package com.hartwig.serve.ckb.json.variant;

import java.time.LocalDate;
import java.util.List;

import com.hartwig.serve.ckb.json.CkbJsonObject;
import com.hartwig.serve.ckb.json.common.DescriptionInfo;
import com.hartwig.serve.ckb.json.common.EvidenceInfo;
import com.hartwig.serve.ckb.json.common.GeneInfo;
import com.hartwig.serve.ckb.json.common.MolecularProfileInfo;
import com.hartwig.serve.ckb.json.common.VariantInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonVariant implements CkbJsonObject {

    public abstract int id();

    @NotNull
    public abstract String fullName();

    @Nullable
    public abstract String impact();

    @Nullable
    public abstract String proteinEffect();

    @NotNull
    public abstract List<DescriptionInfo> descriptions();

    @Nullable
    public abstract String type();

    @NotNull
    public abstract GeneInfo gene();

    @NotNull
    public abstract String variant();

    @Nullable
    public abstract String associatedWithDrugResistance();

    @Nullable
    public abstract String transformingActivity();

    @Nullable
    public abstract String polymorphism();
    
    @NotNull
    public abstract LocalDate createDate();

    @NotNull
    public abstract LocalDate updateDate();

    @Nullable
    public abstract JsonTranscriptCoordinate referenceTranscriptCoordinate();

    @NotNull
    public abstract List<JsonVariantPartnerGene> partnerGenes();

    @NotNull
    public abstract List<JsonCategoryVariantPath> categoryVariantPaths();

    @NotNull
    public abstract List<EvidenceInfo> evidence();

    @NotNull
    public abstract List<EvidenceInfo> extendedEvidence();

    @NotNull
    public abstract List<MolecularProfileInfo> molecularProfiles();

    @NotNull
    public abstract List<JsonTranscriptCoordinate> allTranscriptCoordinates();

    @NotNull
    public abstract List<VariantInfo> memberVariants();
}

package com.hartwig.serve.sources.ckb;

import java.time.LocalDate;
import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.evidence.Evidence;
import com.hartwig.serve.ckb.datamodel.evidence.ImmutableEvidence;
import com.hartwig.serve.ckb.datamodel.indication.ImmutableIndication;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.ckb.datamodel.therapy.ImmutableTherapy;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
import com.hartwig.serve.ckb.datamodel.variant.Gene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableGene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableVariant;
import com.hartwig.serve.ckb.datamodel.variant.Variant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CkbTestFactory {

    private static final LocalDate TEST_DATE = LocalDate.of(2021, 2, 20);

    private CkbTestFactory() {
    }

    @NotNull
    public static ImmutableCkbEntry.Builder builder() {
        return ImmutableCkbEntry.builder().profileId(0).createDate(TEST_DATE).updateDate(TEST_DATE).profileName("");
    }

    @NotNull
    public static CkbEntry createEntryWithGene(@NotNull String geneSymbol) {
        return createEntry(geneSymbol,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "");
    }

    @NotNull
    public static CkbEntry createEntryWithVariant(@NotNull String variant) {
        return createEntry("",
                variant,
                "",
                "",
                "",
                "",
                "",
                "",
                "");
    }

    @NotNull
    public static CkbEntry createEntryWithFullName(@NotNull String fullName) {
        return createEntry("",
                "",
                fullName,
                "",
                "",
                "",
                "",
                "",
                "");
    }

    @NotNull
    public static CkbEntry createEntryWithGeneAndVariant(@NotNull String geneSymbol, @NotNull String variant) {
        return createEntry(geneSymbol,
                variant,
                "",
                "",
                "",
                "",
                "",
                "",
                "");
    }

    @NotNull
    public static CkbEntry createEntryWithClinicalTrial(int profileId, @NotNull ClinicalTrial clinicalTrial) {
        return builder().profileId(profileId).clinicalTrials(List.of(clinicalTrial)).build();
    }

    @NotNull
    public static CkbEntry createEntry(@NotNull String geneSymbol, @NotNull String variant, @NotNull String fullName,
            @NotNull String responseType, @NotNull String evidenceType, @NotNull String therapyName, @NotNull String indicationName,
            @NotNull String level, @NotNull String termId) {
        return builder().addVariants(createVariant(geneSymbol, variant, fullName))
                .addEvidences(createEvidence(responseType, evidenceType, therapyName, indicationName, level, termId))
                .build();
    }

    @NotNull
    public static ClinicalTrial createTrial(@NotNull String recruitment, @NotNull List<VariantRequirementDetail> variantRequirementDetails,
            @NotNull List<Location> locations, @NotNull String nctId, @NotNull String title) {
        return ImmutableClinicalTrial.builder()
                .updateDate(TEST_DATE)
                .nctId(nctId)
                .title(title)
                .indications(List.of(createIndication("AB", "DOID:162")))
                .recruitment(recruitment)
                .variantRequirement("")
                .variantRequirementDetails(variantRequirementDetails)
                .locations(locations)
                .build();
    }

    @NotNull
    private static Evidence createEvidence(@NotNull String responseType, @NotNull String evidenceType, @NotNull String therapyName,
            @NotNull String indicationName, @NotNull String level, @NotNull String termId) {
        return ImmutableEvidence.builder()
                .id(0)
                .therapy(createTherapy(therapyName))
                .indication(createIndication(indicationName, termId))
                .responseType(responseType)
                .relevantTreatmentApproaches(Lists.newArrayList())
                .evidenceType(evidenceType)
                .efficacyEvidence("")
                .approvalStatus("")
                .ampCapAscoEvidenceLevel(level)
                .ampCapAscoInferredTier("")
                .references(Lists.newArrayList())
                .build();
    }

    @NotNull
    private static Therapy createTherapy(@NotNull String therapyName) {
        return ImmutableTherapy.builder()
                .id(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .therapyName(therapyName)
                .drugs(Lists.newArrayList())
                .synonyms(Lists.newArrayList())
                .globalApprovalStatuses(Lists.newArrayList())
                .description("")
                .references(Lists.newArrayList())
                .build();
    }

    @NotNull
    public static Indication createIndication(@NotNull String name, @NotNull String termId) {
        return ImmutableIndication.builder()
                .id(0)
                .name(name)
                .source("")
                .definition("")
                .currentPreferredTerm("")
                .lastUpdateDateFromDO(TEST_DATE)
                .termId(termId)
                .altIds(Lists.newArrayList())
                .build();
    }

    @NotNull
    public static Location createLocation(@NotNull String country, @Nullable String status) {
        return ImmutableLocation.builder().nctId("").city("").country(country).status(status).build();
    }

    @NotNull
    public static VariantRequirementDetail createVariantRequirementDetail(int profileId, @NotNull String requirementType) {
        return ImmutableVariantRequirementDetail.builder().profileId(profileId).requirementType(requirementType).build();
    }

    @NotNull
    private static Variant createVariant(@NotNull String geneSymbol, @NotNull String variant, @NotNull String fullName) {
        return ImmutableVariant.builder()
                .id(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .fullName(fullName)
                .variant(variant)
                .isHotspot(false)
                .gene(createGene(geneSymbol))
                .build();
    }

    @NotNull
    private static Gene createGene(@NotNull String geneSymbol) {
        return ImmutableGene.builder()
                .id(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .geneSymbol(geneSymbol)
                .geneRole("")
                .build();
    }
}
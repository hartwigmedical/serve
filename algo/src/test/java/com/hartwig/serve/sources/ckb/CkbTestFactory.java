package com.hartwig.serve.sources.ckb;

import java.time.LocalDate;
import java.util.List;

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
import com.hartwig.serve.ckb.datamodel.reference.ImmutableReference;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.therapy.ImmutableTherapy;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
import com.hartwig.serve.ckb.datamodel.variant.Gene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableGene;
import com.hartwig.serve.ckb.datamodel.variant.ImmutableVariant;
import com.hartwig.serve.ckb.datamodel.variant.Variant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CkbTestFactory {

    static final LocalDate TEST_CREATE_DATE = LocalDate.of(2021, 2, 20);
    static final LocalDate TEST_UPDATE_DATE = LocalDate.of(2022, 3, 21);
    static final String EFFICACY_EVIDENCE = "efficacy evidence";

    private CkbTestFactory() {
    }

    @NotNull
    public static ImmutableCkbEntry.Builder builder() {
        return ImmutableCkbEntry.builder().profileId(0).createDate(TEST_CREATE_DATE).updateDate(TEST_UPDATE_DATE).profileName("");
    }

    @NotNull
    public static CkbEntry createEntryWithGene(@NotNull String geneSymbol) {
        return createEntry(geneSymbol, "", "", null, "", "", "", "", "", "", "");
    }

    @NotNull
    public static CkbEntry createEntryWithVariant(@NotNull String variant) {
        return createEntry("", variant, "", null, "", "", "", "", "", "", "");
    }

    @NotNull
    public static CkbEntry createEntryWithFullName(@NotNull String fullName) {
        return createEntry("", "", fullName, null, "", "", "", "", "", "", "");
    }

    @NotNull
    public static CkbEntry createEntryWithGeneAndVariant(@NotNull String geneSymbol, @NotNull String variant) {
        return createEntry(geneSymbol, variant, "", null, "", "", "", "", "", "", "");
    }

    @NotNull
    public static CkbEntry createEntryWithClinicalTrial(int profileId, @NotNull String profileName, @NotNull ClinicalTrial clinicalTrial) {
        return builder().profileId(profileId).profileName(profileName).clinicalTrials(List.of(clinicalTrial)).build();
    }

    @NotNull
    public static CkbEntry createEntry(@NotNull String geneSymbol, @NotNull String variant, @NotNull String fullName,
            @Nullable String proteinEffect, @NotNull String responseType, @NotNull String evidenceType, @NotNull String therapyName,
            @NotNull String indicationName, @NotNull String evidenceLevel, @NotNull String approvalStatus, @NotNull String termId) {
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Rotterdam", "EMC");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(0, "required");
        return builder().addVariants(createVariant(geneSymbol, variant, fullName, proteinEffect, null))
                .addEvidences(createEvidence(responseType,
                        evidenceType,
                        therapyName,
                        indicationName,
                        evidenceLevel,
                        approvalStatus,
                        termId))
                .addClinicalTrials(createTrialWithTherapy("NCT0102",
                        "Phase I trial",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")),
                        List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                        "Recruiting",
                        List.of("senior", "child", "adult"),
                        List.of(requirementDetail),
                        List.of(location)))
                .build();
    }

    @NotNull
    public static CkbEntry createCombinedEntry(@NotNull List<Variant> variants) {
        return builder().addAllVariants(variants).addEvidences(createEvidence("", "", "", "", "", "", "")).build();
    }

    @NotNull
    public static CkbEntry createCombinedEntry(@NotNull List<Variant> variants, @NotNull String responseType, @NotNull String evidenceType,
            @NotNull String therapyName, @NotNull String indicationName, @NotNull String evidenceLevel, @NotNull String approvalStatus,
            @NotNull String termId) {
        return builder().addAllVariants(variants)
                .addEvidences(createEvidence(responseType,
                        evidenceType,
                        therapyName,
                        indicationName,
                        evidenceLevel,
                        approvalStatus,
                        termId))
                .build();

    }

    @NotNull
    public static ClinicalTrial createTrial(@NotNull String nctId, @NotNull String title, @NotNull String recruitment,
            @NotNull List<String> ageGroups, @NotNull List<VariantRequirementDetail> variantRequirementDetails,
            @NotNull List<Location> locations) {
        return createTrialWithTherapy(nctId,
                title,
                List.of(),
                List.of(createIndication("AB", "DOID:162")),
                recruitment,
                ageGroups,
                variantRequirementDetails,
                locations);
    }

    @NotNull
    public static ClinicalTrial createTrialWithTherapy(@NotNull String nctId, @NotNull String title, @NotNull List<Therapy> therapies,
            @NotNull List<Indication> indication, @NotNull String recruitment, @NotNull List<String> ageGroups,
            @NotNull List<VariantRequirementDetail> variantRequirementDetails, @NotNull List<Location> locations) {
        return ImmutableClinicalTrial.builder()
                .updateDate(TEST_UPDATE_DATE)
                .nctId(nctId)
                .title(title)
                .therapies(therapies)
                .indications(indication)
                .recruitment(recruitment)
                .ageGroups(ageGroups)
                .gender("both")
                .variantRequirement("")
                .variantRequirementDetails(variantRequirementDetails)
                .locations(locations)
                .build();
    }

    @NotNull
    public static Evidence createEvidence(@NotNull String responseType, @NotNull String evidenceType, @NotNull String therapyName,
            @NotNull String indicationName, @NotNull String level, @NotNull String approvalStatus, @NotNull String termId) {
        return ImmutableEvidence.builder()
                .id(0)
                .therapy(createTherapy(therapyName))
                .indication(createIndication(indicationName, termId))
                .responseType(responseType)
                .evidenceType(evidenceType)
                .efficacyEvidence(EFFICACY_EVIDENCE)
                .approvalStatus(approvalStatus)
                .ampCapAscoEvidenceLevel(level)
                .ampCapAscoInferredTier("")
                .references(List.of())
                .build();
    }

    @NotNull
    public static Therapy createTherapy(@NotNull String therapyName) {
        return ImmutableTherapy.builder()
                .id(0)
                .createDate(TEST_CREATE_DATE)
                .updateDate(TEST_UPDATE_DATE)
                .therapyName(therapyName)
                .drugs(List.of())
                .synonyms(List.of())
                .globalApprovalStatuses(List.of())
                .description("")
                .references(List.of())
                .build();
    }

    @NotNull
    public static Therapy createTherapy(int createYear) {
        return ImmutableTherapy.builder()
                .id(0)
                .createDate(LocalDate.of(createYear, 1, 1))
                .updateDate(LocalDate.of(2021, 1, 1))
                .therapyName("Therapy")
                .drugs(List.of())
                .synonyms(List.of())
                .globalApprovalStatuses(List.of())
                .description("Description")
                .references(List.of())
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
                .lastUpdateDateFromDO(TEST_UPDATE_DATE)
                .termId(termId)
                .altIds(List.of())
                .build();
    }

    @NotNull
    public static Location createLocation(@NotNull String country, @Nullable String status, @NotNull String city,
            @Nullable String facility) {
        return createLocation(country, status, city, null, facility);
    }

    @NotNull
    public static Location createLocation(@Nullable String country, @Nullable String status, @Nullable String city, @Nullable String state,
            @Nullable String facility) {
        return ImmutableLocation.builder().nctId("").city(city).country(country).state(state).status(status).facility(facility).build();
    }

    @NotNull
    public static VariantRequirementDetail createVariantRequirementDetail(int profileId, @NotNull String requirementType) {
        return ImmutableVariantRequirementDetail.builder().profileId(profileId).requirementType(requirementType).build();
    }

    @NotNull
    public static Variant createVariant(@NotNull String geneSymbol, @NotNull String variant, @NotNull String fullName) {
        return createVariant(geneSymbol, variant, fullName, "unknown", null);
    }

    @NotNull
    public static Variant createVariant(@NotNull String geneSymbol, @NotNull String variant, @NotNull String fullName,
            @Nullable String proteinEffect, @Nullable String associatedWithDrugResistance) {
        return ImmutableVariant.builder()
                .id(0)
                .createDate(TEST_CREATE_DATE)
                .updateDate(TEST_UPDATE_DATE)
                .fullName(fullName)
                .variant(variant)
                .isHotspot(false)
                .gene(createGene(geneSymbol))
                .proteinEffect(proteinEffect)
                .associatedWithDrugResistance(associatedWithDrugResistance)
                .build();
    }

    @NotNull
    private static Gene createGene(@NotNull String geneSymbol) {
        return ImmutableGene.builder()
                .id(0)
                .createDate(TEST_CREATE_DATE)
                .updateDate(TEST_UPDATE_DATE)
                .geneSymbol(geneSymbol)
                .geneRole("")
                .build();
    }

    @NotNull
    public static Reference createReference(@Nullable String year) {
        return ImmutableReference.builder()
                .id(0)
                .pubMedId("123456")
                .title("Title")
                .shortJournalTitle("Short Journal Title")
                .pages("1-2")
                .abstractText("Abstract Text")
                .url("http://www.url.com")
                .journal("Journal")
                .authors("Authors")
                .volume("Volume")
                .issue("Issue")
                .date("Date")
                .year(year)
                .build();
    }
}
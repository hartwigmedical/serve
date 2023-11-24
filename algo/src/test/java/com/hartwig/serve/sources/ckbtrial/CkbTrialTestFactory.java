package com.hartwig.serve.sources.ckbtrial;

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

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class CkbTrialTestFactory {

    private static final LocalDate TEST_DATE = LocalDate.of(2021, 2, 20);

    private CkbTrialTestFactory() {
    }

    @NotNull
    public static ClinicalTrial trialWithCountry(@NotNull List<Location> locations) {
        return createTrial(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Lists.newArrayList(), locations);
    }

    @NotNull
    public static ClinicalTrial trialWithRequirementType(@NotNull List<VariantRequirementDetail> variantRequirementDetails) {
        return createTrial(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, variantRequirementDetails, Lists.newArrayList());
    }

    @NotNull
    public static ClinicalTrial trialWithRequirementTypeAndCountryAndRecruitment(@NotNull String therapyName, @NotNull String indicationName, @NotNull String termId) {
        return createTrial(therapyName, "Recruiting", indicationName, termId, Lists.newArrayList(
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("required").build()), Lists.newArrayList(
                ImmutableLocation.builder().nctId("").city("").country("Netherlands").build()));
    }

    @NotNull
    public static ClinicalTrial createTrial(@NotNull String therapyName, @NotNull String recruitment, @NotNull String indicationName, @NotNull String termId, @NotNull List<VariantRequirementDetail> variantRequirementDetails,
            @NotNull List<Location> locations) {
        return ImmutableClinicalTrial.builder()
                .updateDate(TEST_DATE)
                .nctId("nctid")
                .title("title")
                .therapies(List.of(createTherapy(therapyName)))
                .indications(List.of(createIndication(indicationName, termId)))
                .recruitment(recruitment)
                .ageGroups(List.of("adult"))
                .variantRequirement("yes")
                .variantRequirementDetails(variantRequirementDetails)
                .locations(locations)
                .build();
    }

    @NotNull
    public static CkbEntry createEntry(@NotNull String geneSymbol, @NotNull String variant, @NotNull String fullName,
            @NotNull String responseType, @NotNull String evidenceType, @NotNull String therapyName, @NotNull String indicationName,
            @NotNull String level, @NotNull String termId) {
        return ImmutableCkbEntry.builder()
                .profileId(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .profileName(Strings.EMPTY)
                .addVariants(createVariant(geneSymbol, variant, fullName))
                .addEvidences(createEvidence(responseType, evidenceType, therapyName, indicationName, level, termId))
                .clinicalTrials(List.of(trialWithRequirementTypeAndCountryAndRecruitment(therapyName, indicationName, termId)))
                .build();
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
    private static Evidence createEvidence(@NotNull String responseType, @NotNull String evidenceType, @NotNull String therapyName,
            @NotNull String indicationName, @NotNull String level, @NotNull String termId) {
        return ImmutableEvidence.builder()
                .id(0)
                .therapy(createTherapy(therapyName))
                .indication(createIndication(indicationName, termId))
                .responseType(responseType)
                .relevantTreatmentApproaches(org.apache.commons.compress.utils.Lists.newArrayList())
                .evidenceType(evidenceType)
                .efficacyEvidence(Strings.EMPTY)
                .approvalStatus(Strings.EMPTY)
                .ampCapAscoEvidenceLevel(level)
                .ampCapAscoInferredTier(Strings.EMPTY)
                .references(org.apache.commons.compress.utils.Lists.newArrayList())
                .build();
    }

    @NotNull
    private static Therapy createTherapy(@NotNull String therapyName) {
        return ImmutableTherapy.builder()
                .id(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .therapyName(therapyName)
                .drugs(org.apache.commons.compress.utils.Lists.newArrayList())
                .synonyms(org.apache.commons.compress.utils.Lists.newArrayList())
                .globalApprovalStatuses(org.apache.commons.compress.utils.Lists.newArrayList())
                .description(Strings.EMPTY)
                .references(org.apache.commons.compress.utils.Lists.newArrayList())
                .build();
    }

    @NotNull
    private static Indication createIndication(@NotNull String name, @NotNull String termId) {
        return ImmutableIndication.builder()
                .id(0)
                .name(name)
                .source(Strings.EMPTY)
                .definition(Strings.EMPTY)
                .currentPreferredTerm(Strings.EMPTY)
                .lastUpdateDateFromDO(TEST_DATE)
                .termId(termId)
                .altIds(org.apache.commons.compress.utils.Lists.newArrayList())
                .build();
    }

    @NotNull
    private static Gene createGene(@NotNull String geneSymbol) {
        return ImmutableGene.builder()
                .id(0)
                .createDate(TEST_DATE)
                .updateDate(TEST_DATE)
                .geneSymbol(geneSymbol)
                .geneRole(Strings.EMPTY)
                .build();
    }
}

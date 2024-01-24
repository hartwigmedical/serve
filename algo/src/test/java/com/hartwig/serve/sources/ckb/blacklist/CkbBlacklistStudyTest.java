package com.hartwig.serve.sources.ckb.blacklist;


import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
import com.hartwig.serve.sources.ckb.CkbTestFactory;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CkbBlacklistStudyTest {

    @Test
    public void canBlacklistWholeStudy() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyEntryList(CkbBlacklistStudyReason.STUDY_WHOLE, "NCT0102"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrial("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial");
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, Strings.EMPTY, clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistStudy.reportUnusedBlacklistEntries();
    }

    @Test
    public void canBlacklistStudyTherapy() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyTherapyEntryList(CkbBlacklistStudyReason.STUDY_THERAPY, "NCT0102", "AB"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        Therapy therapy = CkbTestFactory.createTherapy("AB");
        Indication indication = CkbTestFactory.createIndication("Solid tumor", "162");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial", List.of(therapy), List.of(indication));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, Strings.EMPTY, clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistStudy.reportUnusedBlacklistEntries();
    }

    @Test
    public void canBlacklistStudyCancerType() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyCancerTypeEntryList(CkbBlacklistStudyReason.STUDY_CANCER_TYPE, "NCT0102", "AB", "Solid tumor"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        Therapy therapy = CkbTestFactory.createTherapy("AB");
        Indication indication = CkbTestFactory.createIndication("Solid tumor", "162");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial", List.of(therapy), List.of(indication));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, Strings.EMPTY, clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistStudy.reportUnusedBlacklistEntries();
    }

    @Test
    public void canBlacklistStudyMolecularProfile() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyMolecularProfileEntryList(CkbBlacklistStudyReason.STUDY_MOLECULAR_PROFILE, "NCT0102", "AB", "Solid tumor", "ERBB2 amp"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        Therapy therapy = CkbTestFactory.createTherapy("AB");
        Indication indication = CkbTestFactory.createIndication("Solid tumor", "162");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial", List.of(therapy), List.of(indication));

        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, "ERBB2 amp", clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistStudy.reportUnusedBlacklistEntries();
    }

    @Test
    public void canBlacklistAllMolecularProfile() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyMolecularProfileEntryList(CkbBlacklistStudyReason.ALL_MOLECULAR_PROFILE, "NCT0102", "AB", "Solid tumor", "ERBB2 amp"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        Therapy therapy = CkbTestFactory.createTherapy("AB");
        Indication indication = CkbTestFactory.createIndication("Solid tumor", "162");
        ClinicalTrial clinicalTrial1 =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial", List.of(therapy), List.of(indication));

        ClinicalTrial clinicalTrial2 =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0405", "Phase I trial", List.of(therapy), List.of(indication));


        List<ClinicalTrial> clinicalTrials = Lists.newArrayList(clinicalTrial1, clinicalTrial2);

        CkbEntry entry = CkbTestFactory.createEntryWithMultipleClinicalTrial(profileId, "ERBB2 amp", clinicalTrials);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistStudy.reportUnusedBlacklistEntries();
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyEntryList(@NotNull CkbBlacklistStudyReason reason, @NotNull String nctId) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).build());
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyTherapyEntryList(@NotNull CkbBlacklistStudyReason reason, @NotNull String nctId, @Nullable String therapy) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).therapy(therapy).build());
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyCancerTypeEntryList(@NotNull CkbBlacklistStudyReason reason, @NotNull String nctId, @Nullable String therapy, @Nullable String cancerType) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).therapy(therapy).cancerType(cancerType).build());
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyMolecularProfileEntryList(@NotNull CkbBlacklistStudyReason reason, @NotNull String nctId, @Nullable String therapy, @Nullable String cancerType, @Nullable String molecularProfile) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).therapy(therapy).cancerType(cancerType).molecularProfile(molecularProfile).build());
    }
}
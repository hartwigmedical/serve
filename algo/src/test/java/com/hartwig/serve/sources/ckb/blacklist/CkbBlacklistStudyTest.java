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
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyEntryList(CkbBlacklistReason.STUDY_WHOLE, "NCT0102"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrial("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial");
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, Strings.EMPTY, clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());
    }

    @Test
    public void canBlacklistStudyTherapy() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyTherapyEntryList(CkbBlacklistReason.STUDY_THERAPY, "NCT0102", "AB"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        Therapy therapy = CkbTestFactory.createTherapy("AB");
        Indication indication = CkbTestFactory.createIndication("Solid tumor", "162");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial", List.of(therapy), List.of(indication));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, Strings.EMPTY, clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());
    }

    @Test
    public void canBlacklistStudyCancerType() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyCancerTypeEntryList(CkbBlacklistReason.STUDY_CANCER_TYPE, "NCT0102", "AB", "Solid tumor"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        Therapy therapy = CkbTestFactory.createTherapy("AB");
        Indication indication = CkbTestFactory.createIndication("Solid tumor", "162");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial", List.of(therapy), List.of(indication));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, Strings.EMPTY, clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());
    }

    @Test
    public void canBlacklistStudyMolecularProfile() {
        int profileId = 1;
        CkbBlacklistStudy ckbBlacklistStudy = new CkbBlacklistStudy(createBlacklistStudyMolecularProfileEntryList(CkbBlacklistReason.STUDY_MOLECULAR_PROFILE, "NCT0102", "AB", "Solid tumor", "ERBB2 amp"));
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        Therapy therapy = CkbTestFactory.createTherapy("AB");
        Indication indication = CkbTestFactory.createIndication("Solid tumor", "162");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial", List.of(therapy), List.of(indication));

        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, "ERBB2 amp", clinicalTrial);
        assertTrue(ckbBlacklistStudy.run(Lists.newArrayList(entry)).isEmpty());
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyEntryList(@NotNull CkbBlacklistReason reason, @NotNull String nctId) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).build());
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyTherapyEntryList(@NotNull CkbBlacklistReason reason, @NotNull String nctId, @Nullable String therapy) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).therapy(therapy).build());
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyCancerTypeEntryList(@NotNull CkbBlacklistReason reason, @NotNull String nctId, @Nullable String therapy, @Nullable String cancerType) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).therapy(therapy).cancerType(cancerType).build());
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> createBlacklistStudyMolecularProfileEntryList(@NotNull CkbBlacklistReason reason, @NotNull String nctId, @Nullable String therapy, @Nullable String cancerType, @Nullable String molecularProfile) {
        return Lists.newArrayList(ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).therapy(therapy).cancerType(cancerType).molecularProfile(molecularProfile).build());
    }
}
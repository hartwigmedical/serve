package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    @Test
    public void shouldIgnoreNonActionableKrasDeletionTrial() {
        Location location = ImmutableLocation.builder().nctId("").city("").country("United States").status("Recruiting").build();
        VariantRequirementDetail requirementType =
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("required").build();
        CkbEntry entryDeletion = CkbTestFactory.createEntryWithClinicalTrial("KRAS",
                "deletion",
                "KRAS deletion",
                "AB",
                "AB",
                "DOID:162",
                location,
                "Recruiting",
                requirementType);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> entryDeletionSet = actionableTrialFactory.create(entryDeletion, "KRAS", "gene");
        assertEquals(0, entryDeletionSet.size());
    }

    @Test
    public void canDetermineIfHasVariantRequirementTypeToInclude() {
        ClinicalTrial trialWithRequiredType =
                CkbTestFactory.createTrialWithRequirementType(Lists.newArrayList(ImmutableVariantRequirementDetail.builder()
                        .profileId(0)
                        .requirementType("required")
                        .build()));
        ClinicalTrial trialWithExcludedType =
                CkbTestFactory.createTrialWithRequirementType(Lists.newArrayList(ImmutableVariantRequirementDetail.builder()
                        .profileId(0)
                        .requirementType("excluded")
                        .build()));
        CkbEntry entry =
                CkbTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        assertTrue(ActionableTrialFactory.hasVariantRequirementTypeToInclude(trialWithRequiredType.variantRequirementDetails(), entry));
        assertFalse(ActionableTrialFactory.hasVariantRequirementTypeToInclude(trialWithExcludedType.variantRequirementDetails(), entry));
    }

    @Test
    public void canDetermineIfHasCountryToIncludeWithPotentiallyOpenRecruitmentType() {
        ClinicalTrial notOpenDutchTrial =
                CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                        .nctId("")
                        .city("")
                        .country("Netherlands")
                        .status("Not yet recruiting")
                        .build()), "Recruiting");
        ClinicalTrial openDutchTrial =
                CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                        .nctId("")
                        .city("")
                        .country("Netherlands")
                        .status("Recruiting")
                        .build()), "Recruiting");
        ClinicalTrial americanTrial = CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                .nctId("")
                .city("")
                .country("United States")
                .status("Recruiting")
                .build()), "Recruiting");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(notOpenDutchTrial).size());
        assertEquals(1, ActionableTrialFactory.countriesToInclude(openDutchTrial).size());
        assertEquals(0, ActionableTrialFactory.countriesToInclude(americanTrial).size());
    }
}
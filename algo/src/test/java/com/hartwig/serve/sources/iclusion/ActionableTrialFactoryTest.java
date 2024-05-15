package com.hartwig.serve.sources.iclusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.curation.DoidLookupTestFactory;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.datamodel.IclusionTumorLocation;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionTumorLocation;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    @Test
    public void canCreateMultipleActionableTrials() {
        String location1 = "loc1";
        String loc1Doid1 = CancerTypeConstants.CANCER_DOID;
        String loc1Doid2 = "loc1Doid2";
        String location2 = "loc2";
        String loc2Doid1 = "loc2Doid2";
        String blacklistLocation1 = "blacklistLocation";
        String blacklistDoid1 = "blacklistDoid";
        String treatment = Strings.EMPTY;
        String trialNCT = "nct";
        IclusionTumorLocation loc1 =
                ImmutableIclusionTumorLocation.builder().primaryTumorLocation(location1).addDoids(loc1Doid1).addDoids(loc1Doid2).build();
        IclusionTumorLocation loc2 = ImmutableIclusionTumorLocation.builder().primaryTumorLocation(location2).addDoids(loc2Doid1).build();
        IclusionTumorLocation blacklist =
                ImmutableIclusionTumorLocation.builder().primaryTumorLocation(blacklistLocation1).addDoids(blacklistDoid1).build();
        IclusionTrial trial = IclusionTestFactory.trialWithTumors(trialNCT, Lists.newArrayList(loc1, loc2), Lists.newArrayList(blacklist));

        ActionableTrialFactory factory = new ActionableTrialFactory(DoidLookupTestFactory.dummy());
        List<ActionableTrial> actionableTrials = factory.toActionableTrials(trial, Strings.EMPTY);

        assertEquals(3, actionableTrials.size());
        assertEquals(trialNCT, DatamodelTestFactory.extractClinicalTrial(actionableTrials.get(0)).studyNctId());
        assertEquals(location1, actionableTrials.get(0).applicableCancerType().name());
        assertEquals(loc1Doid1, actionableTrials.get(0).applicableCancerType().doid());
        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                ImmutableCancerType.builder().name(blacklistLocation1).doid(blacklistDoid1).build(),
                CancerTypeConstants.BONE_MARROW_TYPE,
                CancerTypeConstants.LEUKEMIA_TYPE), actionableTrials.get(0).blacklistCancerTypes());

        assertEquals(trialNCT, DatamodelTestFactory.extractClinicalTrial(actionableTrials.get(1)).studyNctId());
        assertEquals(location1, actionableTrials.get(1).applicableCancerType().name());
        assertEquals(loc1Doid2, actionableTrials.get(1).applicableCancerType().doid());
        assertEquals(Sets.newHashSet(ImmutableCancerType.builder().name(blacklistLocation1).doid(blacklistDoid1).build()),
                actionableTrials.get(1).blacklistCancerTypes());

        assertEquals(trialNCT, DatamodelTestFactory.extractClinicalTrial(actionableTrials.get(2)).studyNctId());
        assertEquals(location2, actionableTrials.get(2).applicableCancerType().name());
        assertEquals(loc2Doid1, actionableTrials.get(2).applicableCancerType().doid());
        assertEquals(Sets.newHashSet(ImmutableCancerType.builder().name(blacklistLocation1).doid(blacklistDoid1).build()),
                actionableTrials.get(2).blacklistCancerTypes());
    }

    @Test
    public void canCreateTrialsWithoutBlacklist() {
        ActionableTrialFactory factory = new ActionableTrialFactory(DoidLookupTestFactory.dummy());

        IclusionTumorLocation location = ImmutableIclusionTumorLocation.builder().primaryTumorLocation("location").addDoids("doid").build();

        IclusionTrial trialWithoutBlacklist =
                IclusionTestFactory.trialWithTumors("trial", Lists.newArrayList(location), Lists.newArrayList());
        List<ActionableTrial> actionableTrialsWithoutBlacklist = factory.toActionableTrials(trialWithoutBlacklist, Strings.EMPTY);
        assertEquals(1, actionableTrialsWithoutBlacklist.size());
        assertEquals("nct", DatamodelTestFactory.extractClinicalTrial(actionableTrialsWithoutBlacklist.get(0)).studyNctId());
        assertEquals("location", actionableTrialsWithoutBlacklist.get(0).applicableCancerType().name());
        assertEquals("doid", actionableTrialsWithoutBlacklist.get(0).applicableCancerType().doid());
        assertTrue(actionableTrialsWithoutBlacklist.get(0).blacklistCancerTypes().isEmpty());
    }

    @Test
    public void canAddBlacklistingOnTrialsForCancer() {
        ActionableTrialFactory factory = new ActionableTrialFactory(DoidLookupTestFactory.dummy());

        IclusionTumorLocation location = ImmutableIclusionTumorLocation.builder()
                .primaryTumorLocation("cancer")
                .addDoids(CancerTypeConstants.CANCER_DOID)
                .addDoids("another doid")
                .build();

        IclusionTrial trialOnCancer = IclusionTestFactory.trialWithTumors("trial", Lists.newArrayList(location), Lists.newArrayList());
        List<ActionableTrial> actionableTrialsWithCancer = factory.toActionableTrials(trialOnCancer, Strings.EMPTY);
        assertEquals(2, actionableTrialsWithCancer.size());
        assertEquals("nct", DatamodelTestFactory.extractClinicalTrial(actionableTrialsWithCancer.get(0)).studyNctId());
        assertEquals("cancer", actionableTrialsWithCancer.get(0).applicableCancerType().name());
        assertEquals(CancerTypeConstants.CANCER_DOID, actionableTrialsWithCancer.get(0).applicableCancerType().doid());
        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                CancerTypeConstants.BONE_MARROW_TYPE,
                CancerTypeConstants.LEUKEMIA_TYPE), actionableTrialsWithCancer.get(0).blacklistCancerTypes());
    }

    @Test
    public void canCurateDoids() {
        assertEquals("0060463", ActionableTrialFactory.curateDoid("0060463"));
        assertEquals(CancerTypeConstants.CANCER_DOID, ActionableTrialFactory.curateDoid("0050686"));
        assertEquals(CancerTypeConstants.ORGAN_SYSTEM_CANCER_DOID, ActionableTrialFactory.curateDoid("UNKNOWN"));
        assertEquals(CancerTypeConstants.ORGAN_SYSTEM_CANCER_DOID, ActionableTrialFactory.curateDoid("MESH: D009382"));
    }
}
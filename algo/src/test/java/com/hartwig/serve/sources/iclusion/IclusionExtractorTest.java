package com.hartwig.serve.sources.iclusion;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.curation.DoidLookupTestFactory;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.iclusion.classification.IclusionClassificationConfig;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.datamodel.IclusionTumorLocation;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionMutation;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionTumorLocation;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;

import org.junit.Test;

public class IclusionExtractorTest {

    @Test
    public void canExtractFromIclusionEntries() {
        EventClassifierConfig config = IclusionClassificationConfig.build();
        IclusionExtractor extractor = IclusionExtractorFactory.buildIclusionExtractor(config,
                RefGenomeResourceTestFactory.buildTestResource37(),
                DoidLookupTestFactory.dummy());

        IclusionTumorLocation loc1 = ImmutableIclusionTumorLocation.builder().primaryTumorLocation("ptum").addDoids("162").build();

        List<IclusionTrial> entries = Lists.newArrayList();
        entries.add(IclusionTestFactory.trialWithMutationsAndTumorLocation("trial",
                Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(ImmutableIclusionMutation.builder()
                        .gene("KIT")
                        .name("KIT AMPLIFICATION")
                        .negation(true)
                        .build()))),
                Lists.newArrayList(loc1)));
        entries.add(IclusionTestFactory.trialWithMutationsAndTumorLocation("trial",
                Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(ImmutableIclusionMutation.builder()
                        .gene("BRAF")
                        .name("V600E")
                        .negation(false)
                        .build()))),
                Lists.newArrayList(loc1)));
        entries.add(IclusionTestFactory.trialWithMutationsAndTumorLocation("trial",
                Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(ImmutableIclusionMutation.builder()
                        .gene("NTRK3")
                        .name("NTRK3 FUSION")
                        .negation(true)
                        .build()))),
                Lists.newArrayList(loc1)));
        entries.add(IclusionTestFactory.trialWithMutationsAndTumorLocation("trial",
                Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(ImmutableIclusionMutation.builder()
                        .gene("BRAF")
                        .name("V600")
                        .negation(true)
                        .build()))),
                Lists.newArrayList(loc1)));
        entries.add(IclusionTestFactory.trialWithMutationsAndTumorLocation("trial",
                Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(ImmutableIclusionMutation.builder()
                        .gene("BRAF")
                        .name("EXON 1 DELETION")
                        .negation(true)
                        .build()))),
                Lists.newArrayList(loc1)));
        entries.add(IclusionTestFactory.trialWithMutationsAndTumorLocation("trial",
                Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(ImmutableIclusionMutation.builder()
                        .gene("ALK")
                        .name("EML4-ALK Fusion")
                        .negation(true)
                        .build()))),
                Lists.newArrayList(loc1)));
        entries.add(IclusionTestFactory.trialWithMutationsAndTumorLocation("trial",
                Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(ImmutableIclusionMutation.builder()
                        .gene("-")
                        .name("MSI_HIGH")
                        .negation(true)
                        .build()))),
                Lists.newArrayList(loc1)));

        ExtractionResult result = extractor.extract(entries);

        // Iclusion doesn't extract known events
        assertEquals(0, result.knownHotspots().size());
        assertEquals(0, result.knownCopyNumbers().size());
        assertEquals(0, result.knownFusions().size());

        assertEquals(1, result.actionableHotspots().size());
        assertEquals(2, result.actionableRanges().size());
        assertEquals(2, result.actionableGenes().size());
        assertEquals(1, result.actionableFusions().size());
        assertEquals(1, result.actionableCharacteristics().size());
    }
}
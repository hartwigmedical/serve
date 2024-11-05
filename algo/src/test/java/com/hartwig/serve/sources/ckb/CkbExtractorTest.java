package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;
import com.hartwig.serve.sources.ckb.filter.CkbFilteringTestFactory;
import com.hartwig.serve.sources.ckb.region.ImmutableCkbRegion;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachTestFactory;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbExtractorTest {

    @Test
    public void canExtractEvidenceFromCkbEntries() {
        CkbExtractor evidenceExtractor = CkbExtractorFactory.createExtractor(CkbClassificationConfig.build(),
                RefGenomeResourceTestFactory.buildTestResource37(),
                TreatmentApproachTestFactory.createEmptyCurator(),
                CkbFilteringTestFactory.createEmptyEvidenceFilterModel());

        ExtractionResult evidenceResult = evidenceExtractor.extract(createCkbEntryTestDatabase());
        assertEquals(1, evidenceResult.knownHotspots().size());
        assertEquals(3, evidenceResult.knownGenes().size());
        assertEquals(1, evidenceResult.knownCopyNumbers().size());
        assertEquals(1, evidenceResult.knownFusions().size());
        assertEquals(1, evidenceResult.actionableHotspots().size());
        assertEquals(1, evidenceResult.actionableCodons().size());
        assertEquals(1, evidenceResult.actionableExons().size());
        assertEquals(2, evidenceResult.actionableGenes().size());
        assertEquals(1, evidenceResult.actionableFusions().size());
        assertEquals(1, evidenceResult.actionableCharacteristics().size());
    }

    @Test
    public void canExtractTrialsFromCkbEntries() {
        CkbExtractor trialExtractor = CkbExtractorFactory.createTrialExtractor(CkbClassificationConfig.build(),
                RefGenomeResourceTestFactory.buildTestResource37(),
                CkbFilteringTestFactory.createEmptyTrialFilterModel(),
                Set.of(ImmutableCkbRegion.builder().country("netherlands").states(Collections.emptySet()).build()));

        ExtractionResult trialResult = trialExtractor.extract(createCkbEntryTestDatabase());
        assertEquals(0, trialResult.knownHotspots().size());
        assertEquals(0, trialResult.knownGenes().size());
        assertEquals(0, trialResult.knownCopyNumbers().size());
        assertEquals(0, trialResult.knownFusions().size());
        assertEquals(1, trialResult.actionableHotspots().size());
        assertEquals(1, trialResult.actionableCodons().size());
        assertEquals(1, trialResult.actionableExons().size());
        assertEquals(2, trialResult.actionableGenes().size());
        assertEquals(1, trialResult.actionableFusions().size());
        assertEquals(1, trialResult.actionableCharacteristics().size());
    }

    @Test
    public void canCurateCodons() {
        EventExtractorOutput extractorOutput = ImmutableEventExtractorOutput.builder()
                .codons(List.of(ImmutableCodonAnnotation.builder()
                                .from(RangeTestFactory.createTestRangeAnnotation())
                                .gene("BRAF")
                                .chromosome("1")
                                .start(140753335)
                                .end(140753337)
                                .applicableMutationType(MutationType.ANY)
                                .inputTranscript("A")
                                .inputCodonRank(600)
                                .build(),
                        ImmutableCodonAnnotation.builder()
                                .from(RangeTestFactory.createTestRangeAnnotation())
                                .gene("KRAS")
                                .chromosome("1")
                                .start(10)
                                .end(20)
                                .applicableMutationType(MutationType.ANY)
                                .inputTranscript("transcript")
                                .inputCodonRank(600)
                                .build()))
                .build();

        List<CodonAnnotation> curatedCodons = CkbExtractor.curateCodons(extractorOutput).codons();

        assertNotNull(curatedCodons);

        CodonAnnotation codon1 = findByGene(curatedCodons, "BRAF");
        assertEquals(140753335, codon1.start());
        assertEquals(140753337, codon1.end());
        assertEquals("ENST00000646891", codon1.inputTranscript());

        CodonAnnotation codon2 = findByGene(curatedCodons, "KRAS");
        assertEquals("KRAS", codon2.gene());
        assertEquals(10, codon2.start());
        assertEquals(20, codon2.end());
        assertEquals("transcript", codon2.inputTranscript());
    }

    @NotNull
    private static CodonAnnotation findByGene(@NotNull Iterable<CodonAnnotation> codons, @NotNull String geneToFind) {
        for (CodonAnnotation codon : codons) {
            if (codon.gene().equals(geneToFind)) {
                return codon;
            }
        }

        throw new IllegalStateException("Could not find gene " + geneToFind);
    }

    @NotNull
    private static List<CkbEntry> createCkbEntryTestDatabase() {
        List<CkbEntry> ckbEntries = Lists.newArrayList();
        ckbEntries.add(createWithOpenMolecularTrial("KIT", "amp", "KIT amp", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("BRAF", "V600E", "BRAF V600E", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("NTRK3", "fusion promiscuous", "NTRK3 fusion promiscuous", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("BRAF", "V600", "BRAF V600", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("BRAF", "exon 1 deletion", "BRAF exon 1 deletion", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("-", "MSI high", "MSI high", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("ALK", "EML4-ALK", "EML4-ALK Fusion", "sensitive", "Actionable"));

        return ckbEntries;
    }

    @NotNull
    private static CkbEntry createWithOpenMolecularTrial(@NotNull String gene, @NotNull String variant, @NotNull String fullName,
            @NotNull String responseType, @NotNull String evidenceType) {
        CkbEntry baseEntry = CkbTestFactory.createEntry(gene,
                variant,
                fullName,
                responseType,
                evidenceType,
                "any treatment",
                "any indication",
                "A",
                "Guideline",
                "DOID:162");

        return CkbTestFactory.builder()
                .from(baseEntry)
                .clinicalTrials(List.of(CkbTestFactory.createTrialWithTherapy("Recruiting",
                        List.of(ImmutableVariantRequirementDetail.builder()
                                .profileId(baseEntry.profileId())
                                .requirementType("required")
                                .build()),
                        List.of(CkbTestFactory.createLocation("Netherlands", null, "Rotterdam", "EMC")),
                        "nctid",
                        "title",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")),
                        List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                        List.of("senior", "child", "adult"))))
                .build();
    }
}
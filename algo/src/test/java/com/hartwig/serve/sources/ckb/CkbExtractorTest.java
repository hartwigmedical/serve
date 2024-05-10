package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistFactory;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudyTest;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachFactory;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbExtractorTest {

    @Test
    public void canExtractEvidenceFromCkbEntries() {
        CkbExtractor evidenceExtractor = CkbExtractorFactory.createEvidenceExtractor(CkbClassificationConfig.build(),
                RefGenomeResourceTestFactory.buildTestResource37(),
                TreatmentApproachFactory.createEmptyCurator(), CkbBlacklistFactory.createCkbBlacklistEvidenceEmpty());

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
                RefGenomeResourceTestFactory.buildTestResource37(), CkbBlacklistStudyTest.createCkbBlacklistStudyEmpty());

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
        List<CodonAnnotation> codonAnnotations = Lists.newArrayList();
        CodonAnnotation codonAnnotation1 = ImmutableCodonAnnotation.builder()
                .from(RangeTestFactory.createTestRangeAnnotation())
                .gene("BRAF")
                .chromosome("1")
                .start(10)
                .end(20)
                .applicableMutationType(MutationType.ANY)
                .inputTranscript("A")
                .inputCodonRank(600)
                .build();

        CodonAnnotation codonAnnotation2 = ImmutableCodonAnnotation.builder()
                .from(RangeTestFactory.createTestRangeAnnotation())
                .gene("KRAS")
                .chromosome("1")
                .start(10)
                .end(20)
                .applicableMutationType(MutationType.ANY)
                .inputTranscript("transcript")
                .inputCodonRank(600)
                .build();

        codonAnnotations.add(codonAnnotation1);
        codonAnnotations.add(codonAnnotation2);

        List<CodonAnnotation> curatedCodons = CkbExtractor.curateCodons(codonAnnotations);

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
                "DOID:162");

        return CkbTestFactory.builder()
                .from(baseEntry)
                .clinicalTrials(List.of(CkbTestFactory.createTrialWithTerapy("Recruiting",
                        List.of(ImmutableVariantRequirementDetail.builder()
                                .profileId(baseEntry.profileId())
                                .requirementType("required")
                                .build()),
                        List.of(CkbTestFactory.createLocation("Netherlands", null)),
                        "nctid",
                        "title", List.of(CkbTestFactory.createTherapy("Nivolumab")),
                        List.of(CkbTestFactory.createIndication("test", "JAX:10000006")))))
                .build();
    }
}
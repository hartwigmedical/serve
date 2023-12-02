package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.common.classification.EventClassifierConfig;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachTestFactory;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbExtractorTest {

    @Test
    public void canExtractFromCkbEntries() {
        EventClassifierConfig config = CkbClassificationConfig.build();
        CkbExtractor extractor = CkbExtractorFactory.createEvidenceExtractor(config,
                RefGenomeResourceTestFactory.buildTestResource37(),
                TreatmentApproachTestFactory.createEmptyCurator());

        List<CkbEntry> ckbEntries = Lists.newArrayList();
        ckbEntries.add(create("KIT", "amp", "KIT amp", "sensitive", "Actionable"));
        ckbEntries.add(create("BRAF", "V600E", "BRAF V600E", "sensitive", "Actionable"));
        ckbEntries.add(create("NTRK3", "fusion promiscuous", "NTRK3 fusion promiscuous", "sensitive", "Actionable"));
        ckbEntries.add(create("BRAF", "V600", "BRAF V600", "sensitive", "Actionable"));
        ckbEntries.add(create("BRAF", "exon 1 deletion", "BRAF exon 1 deletion", "sensitive", "Actionable"));
        ckbEntries.add(create("-", "MSI high", "MSI high", "sensitive", "Actionable"));
        ckbEntries.add(create("ALk", "EML4-ALK", "EML4-ALK Fusion", "sensitive", "Actionable"));

        ExtractionResult result = extractor.extract(ckbEntries);
        assertEquals(1, result.knownHotspots().size());
        assertEquals(3, result.knownGenes().size());
        assertEquals(1, result.knownCopyNumbers().size());
        assertEquals(1, result.knownFusions().size());
        assertEquals(1, result.actionableHotspots().size());
        assertEquals(1, result.actionableCodons().size());
        assertEquals(1, result.actionableExons().size());
        assertEquals(2, result.actionableGenes().size());
        assertEquals(1, result.actionableFusions().size());
        assertEquals(1, result.actionableCharacteristics().size());
    }

    @NotNull
    private static CkbEntry create(@NotNull String gene, @NotNull String variant, @NotNull String fullName, @NotNull String evidenceType,
            @NotNull String responseType) {
        return CkbTestFactory.createEntry(gene, variant, fullName, evidenceType, responseType, "AB", "cancer", "A", "DOID:162");
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
}
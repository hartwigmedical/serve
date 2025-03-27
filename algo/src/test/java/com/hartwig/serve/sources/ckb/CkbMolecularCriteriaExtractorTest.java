package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import com.hartwig.serve.datamodel.molecular.MutationType;
import com.hartwig.serve.datamodel.molecular.range.RangeTestFactory;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbMolecularCriteriaExtractorTest {

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

        List<CodonAnnotation> curatedCodons = CkbMolecularCriteriaExtractor.curateCodons(extractorOutput).codons();

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
}
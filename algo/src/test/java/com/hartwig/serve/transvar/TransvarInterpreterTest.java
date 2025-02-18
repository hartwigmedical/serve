package com.hartwig.serve.transvar;

import static com.hartwig.serve.transvar.TransvarTestFactory.testInterpreter37;
import static com.hartwig.serve.transvar.TransvarTestFactory.testInterpreter38;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.serve.common.ensemblcache.Strand;
import com.hartwig.serve.extraction.variant.ImmutableVariant;
import com.hartwig.serve.extraction.variant.Variant;
import com.hartwig.serve.transvar.datamodel.ImmutableTransvarComplexInsertDelete;
import com.hartwig.serve.transvar.datamodel.ImmutableTransvarDeletion;
import com.hartwig.serve.transvar.datamodel.ImmutableTransvarDuplication;
import com.hartwig.serve.transvar.datamodel.ImmutableTransvarFrameshift;
import com.hartwig.serve.transvar.datamodel.ImmutableTransvarInsertion;
import com.hartwig.serve.transvar.datamodel.ImmutableTransvarRecord;
import com.hartwig.serve.transvar.datamodel.ImmutableTransvarSnvMnv;
import com.hartwig.serve.transvar.datamodel.TransvarRecord;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class TransvarInterpreterTest {

    @Test
    public void canLookupTrinucleotides() {
        // Serine (S)
        assertEquals(6, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("TCT", Strand.FORWARD).size());
        assertEquals(6, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("AGA", Strand.REVERSE).size());

        // Valine (V)
        assertEquals(4, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("GTC", Strand.FORWARD).size());
        assertEquals(4, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("GAC", Strand.REVERSE).size());

        // Tyrosine (Y)
        assertEquals(2, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("TAC", Strand.FORWARD).size());
        assertEquals(2, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("GTA", Strand.REVERSE).size());

        // Does not exist -> no trinucleotides found!
        assertEquals(0, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("???", Strand.FORWARD).size());
        assertEquals(0, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("???", Strand.REVERSE).size());

        // No trinucleotide -> return none.
        assertEquals(0, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("TCTC", Strand.FORWARD).size());
        assertEquals(0, TransvarInterpreter.allTrinucleotidesForSameAminoAcid("GAGA", Strand.REVERSE).size());
    }

    @Test
    public void canConvertSnvToVariants() {
        TransvarRecord record = baseRecord().gdnaPosition(10)
                .annotation(ImmutableTransvarSnvMnv.builder()
                        .gdnaRef("A")
                        .gdnaAlt("C")
                        .referenceCodon("TTA")
                        .addCandidateCodons("GTA", "GTC", "GTG", "GTT")
                        .build())
                .build();

        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.REVERSE);

        assertEquals(4, variants.size());

        assertVariant(baseVariant().position(10).ref("A").alt("C").build(), variants.get(0));
        assertVariant(baseVariant().position(8).ref("TAA").alt("GAC").build(), variants.get(1));
        assertVariant(baseVariant().position(8).ref("TAA").alt("CAC").build(), variants.get(2));
        assertVariant(baseVariant().position(8).ref("TAA").alt("AAC").build(), variants.get(3));
    }

    @Test
    public void canConvertMnvForwardStrandToVariants() {
        TransvarRecord record = baseRecord().gdnaPosition(10)
                .annotation(ImmutableTransvarSnvMnv.builder()
                        .gdnaRef("TA")
                        .gdnaAlt("GC")
                        .referenceCodon("TAC")
                        .addCandidateCodons("GCA", "GCC", "GCG", "GCT")
                        .build())
                .build();

        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.FORWARD);

        assertEquals(4, variants.size());

        assertVariant(baseVariant().position(10).ref("TAC").alt("GCA").build(), variants.get(0));
        assertVariant(baseVariant().position(10).ref("TA").alt("GC").build(), variants.get(1));
        assertVariant(baseVariant().position(10).ref("TAC").alt("GCG").build(), variants.get(2));
        assertVariant(baseVariant().position(10).ref("TAC").alt("GCT").build(), variants.get(3));
    }

    @Test
    public void canConvertMnvReverseStrandToVariants() {
        TransvarRecord record = baseRecord().gdnaPosition(10)
                .annotation(ImmutableTransvarSnvMnv.builder()
                        .gdnaRef("CA")
                        .gdnaAlt("GC")
                        .referenceCodon("TGG")
                        .addCandidateCodons("GCA", "GCC", "GCG", "GCT")
                        .build())
                .build();

        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.REVERSE);

        assertEquals(4, variants.size());

        assertVariant(baseVariant().position(9).ref("CCA").alt("TGC").build(), variants.get(0));
        assertVariant(baseVariant().position(9).ref("CCA").alt("GGC").build(), variants.get(1));
        assertVariant(baseVariant().position(10).ref("CA").alt("GC").build(), variants.get(2));
        assertVariant(baseVariant().position(9).ref("CCA").alt("AGC").build(), variants.get(3));
    }

    @Test
    public void canInterpretSnvSpanningMultipleExons() {
        TransvarRecord record = baseRecord().variantSpanMultipleExons(true)
                .gdnaPosition(10)
                .annotation(ImmutableTransvarSnvMnv.builder()
                        .gdnaRef("A")
                        .gdnaAlt("C")
                        .referenceCodon("TTA")
                        .addCandidateCodons("GTA", "GTC", "GTG", "GTT")
                        .build())
                .build();

        List<Variant> variants1 = testInterpreter37().convertRecordToVariants(record, Strand.REVERSE);

        assertEquals(1, variants1.size());

        assertVariant(baseVariant().position(10).ref("A").alt("C").build(), variants1.get(0));

        TransvarRecord record2 = baseRecord().variantSpanMultipleExons(true)
                .gdnaPosition(10)
                .annotation(ImmutableTransvarSnvMnv.builder()
                        .gdnaRef("TTA")
                        .gdnaAlt("GTA")
                        .referenceCodon("TTA")
                        .addCandidateCodons("GTA")
                        .build())
                .build();

        List<Variant> variants2 = testInterpreter37().convertRecordToVariants(record2, Strand.FORWARD);

        assertEquals(0, variants2.size());
    }

    @Test
    public void canConvertDeletionToVariants() {
        TransvarRecord record = baseRecord().gdnaPosition(5)
                .annotation(ImmutableTransvarDeletion.builder().deletedBaseCount(3).leftAlignedGDNAPosition(5).build())
                .build();

        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.FORWARD);

        assertEquals(1, variants.size());

        assertVariant(baseVariant().position(4).ref("CGAT").alt("C").build(), variants.get(0));
    }

    @Test
    public void canConvertDeletionWithMultipleAlignmentsToVariants() {
        // In this situation the mutation is "GATCGATC -> GATC",
        //  Normally the unaligned DNA would be 1 here but that would imply we need to read the 0th ref base.
        TransvarRecord record = baseRecord().gdnaPosition(5)
                .annotation(ImmutableTransvarDeletion.builder().deletedBaseCount(4).leftAlignedGDNAPosition(2).build())
                .build();

        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.FORWARD);

        assertEquals(4, variants.size());

        assertVariant(baseVariant().position(1).ref("GATCG").alt("G").build(), variants.get(0));
        assertVariant(baseVariant().position(2).ref("ATCGA").alt("A").build(), variants.get(1));
        assertVariant(baseVariant().position(3).ref("TCGAT").alt("T").build(), variants.get(2));
        assertVariant(baseVariant().position(4).ref("CGATC").alt("C").build(), variants.get(3));
    }

    @Test
    public void canConvertInsertionsToVariants() {
        TransvarRecord forwardRecord = baseRecord().gdnaPosition(5)
                .annotation(ImmutableTransvarInsertion.builder().insertedBases("GAA").leftAlignedGDNAPosition(4).build())
                .build();

        List<Variant> forwardVariants = testInterpreter37().convertRecordToVariants(forwardRecord, Strand.FORWARD);

        assertEquals(2, forwardVariants.size());

        assertVariant(baseVariant().position(5).ref("G").alt("GGAG").build(), forwardVariants.get(0));
        assertVariant(baseVariant().position(5).ref("G").alt("GGAA").build(), forwardVariants.get(1));

        TransvarRecord reverseRecord = baseRecord().gdnaPosition(5)
                .annotation(ImmutableTransvarInsertion.builder().insertedBases("TGC").leftAlignedGDNAPosition(5).build())
                .build();

        List<Variant> reverseVariants = testInterpreter37().convertRecordToVariants(reverseRecord, Strand.REVERSE);

        assertEquals(4, reverseVariants.size());

        assertVariant(baseVariant().position(5).ref("G").alt("GAGC").build(), reverseVariants.get(0));
        assertVariant(baseVariant().position(5).ref("G").alt("GTGC").build(), reverseVariants.get(1));
        assertVariant(baseVariant().position(5).ref("G").alt("GGGC").build(), reverseVariants.get(2));
        assertVariant(baseVariant().position(5).ref("G").alt("GCGC").build(), reverseVariants.get(3));
    }

    @Test
    public void canConvertComplexDeletionInsertionsToVariants() {
        TransvarRecord oneAminoAcidInsert = baseRecord().gdnaPosition(2)
                .annotation(ImmutableTransvarComplexInsertDelete.builder()
                        .deletedBaseCount(6)
                        .insertedSequence("AAT")
                        .addCandidateAlternativeCodons("AAT")
                        .addCandidateAlternativeCodons("GAT")
                        .build())
                .build();

        List<Variant> variants1 = testInterpreter37().convertRecordToVariants(oneAminoAcidInsert, Strand.FORWARD);

        assertEquals(2, variants1.size());

        assertVariant(baseVariant().position(2).ref("ATCG").alt("A").build(), variants1.get(0));
        assertVariant(baseVariant().position(2).ref("ATCG").alt("G").build(), variants1.get(1));

        TransvarRecord twoAminoAcidInsert = baseRecord().gdnaPosition(2)
                .annotation(ImmutableTransvarComplexInsertDelete.builder()
                        .deletedBaseCount(3)
                        .insertedSequence("AGGGTC")
                        .addCandidateAlternativeCodons("AGGGTC")
                        .addCandidateAlternativeCodons("ATTTTC")
                        .build())
                .build();

        List<Variant> variants2 = testInterpreter37().convertRecordToVariants(twoAminoAcidInsert, Strand.FORWARD);

        assertEquals(1, variants2.size());

        assertVariant(baseVariant().position(2).ref("A").alt("AGGG").build(), variants2.get(0));
    }

    @Test
    public void canConvertComplexDeletionInsertionOnReverseStrand() {
        TransvarRecord oneAminoAcidInsert = baseRecord().gdnaPosition(2)
                .annotation(ImmutableTransvarComplexInsertDelete.builder()
                        .deletedBaseCount(3)
                        .insertedSequence("TAA")
                        .addCandidateAlternativeCodons("TTA")
                        .addCandidateAlternativeCodons("GCG")
                        .build())
                .build();

        List<Variant> variants = testInterpreter37().convertRecordToVariants(oneAminoAcidInsert, Strand.REVERSE);

        assertEquals(2, variants.size());

        assertVariant(baseVariant().position(2).ref("ATC").alt("TAA").build(), variants.get(0));
        assertVariant(baseVariant().position(2).ref("AT").alt("CG").build(), variants.get(1));
    }

    @Test
    public void canReduceComplexityOnComplexDelInsVariants() {
        Variant variant = baseVariant().chromosome("1").position(10).ref("ATGTTA").alt("ATCCTA").build();

        Variant simplifiedVariant = testInterpreter37().reduceComplexityForComplexInsDel(variant);

        assertEquals(12, simplifiedVariant.position());
        assertEquals("GT", simplifiedVariant.ref());
        assertEquals("CC", simplifiedVariant.alt());
    }

    @Test
    public void canConvertDuplicationToVariant37() {
        TransvarRecord record =
                baseRecord().gdnaPosition(5).annotation(ImmutableTransvarDuplication.builder().duplicatedBaseCount(3).build()).build();

        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.FORWARD);

        assertEquals(1, variants.size());

        assertVariant(baseVariant().position(4).ref("C").alt("CGAT").build(), variants.get(0));
    }

    @Test
    public void canConvertDuplicationToVariant38() {
        TransvarRecord record =
                baseRecord().gdnaPosition(5).annotation(ImmutableTransvarDuplication.builder().duplicatedBaseCount(3).build()).build();

        List<Variant> variants = testInterpreter38().convertRecordToVariants(record, Strand.FORWARD);

        assertEquals(1, variants.size());

        assertVariant(baseVariant().chromosome("chr1").position(4).ref("C").alt("CGAT").build(), variants.get(0));
    }

    @Test
    public void canConvertFrameshiftToVariantOnForwardStrand() {
        TransvarRecord record = baseRecord().gdnaPosition(2)
                .annotation(ImmutableTransvarFrameshift.builder().isFrameshiftInsideStartCodon(false).build())
                .build();
        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.FORWARD);

        assertEquals(10, variants.size());
        assertVariant(baseVariant().position(2).ref("A").alt("AG").build(), variants.get(0));
        assertVariant(baseVariant().position(2).ref("A").alt("AA").build(), variants.get(1));
        assertVariant(baseVariant().position(2).ref("A").alt("AT").build(), variants.get(2));
        assertVariant(baseVariant().position(2).ref("A").alt("AC").build(), variants.get(3));

        assertVariant(baseVariant().position(3).ref("T").alt("TG").build(), variants.get(4));
        assertVariant(baseVariant().position(3).ref("T").alt("TA").build(), variants.get(5));
        assertVariant(baseVariant().position(3).ref("T").alt("TT").build(), variants.get(6));

        assertVariant(baseVariant().position(2).ref("AT").alt("A").build(), variants.get(7));

        assertVariant(baseVariant().position(2).ref("ATC").alt("A").build(), variants.get(8));
        assertVariant(baseVariant().position(3).ref("TCG").alt("T").build(), variants.get(9));
    }

    @Test
    public void canConvertFrameshiftToVariantOnReverseStrand() {
        TransvarRecord record = baseRecord().gdnaPosition(6)
                .annotation(ImmutableTransvarFrameshift.builder().isFrameshiftInsideStartCodon(false).build())
                .build();
        List<Variant> variants = testInterpreter37().convertRecordToVariants(record, Strand.REVERSE);

        assertEquals(10, variants.size());
        assertVariant(baseVariant().position(5).ref("G").alt("GA").build(), variants.get(0));
        assertVariant(baseVariant().position(5).ref("G").alt("GT").build(), variants.get(1));
        assertVariant(baseVariant().position(5).ref("G").alt("GC").build(), variants.get(2));

        assertVariant(baseVariant().position(6).ref("A").alt("AG").build(), variants.get(3));
        assertVariant(baseVariant().position(6).ref("A").alt("AA").build(), variants.get(4));
        assertVariant(baseVariant().position(6).ref("A").alt("AT").build(), variants.get(5));
        assertVariant(baseVariant().position(6).ref("A").alt("AC").build(), variants.get(6));

        assertVariant(baseVariant().position(5).ref("GA").alt("G").build(), variants.get(7));

        assertVariant(baseVariant().position(3).ref("TCG").alt("T").build(), variants.get(8));
        assertVariant(baseVariant().position(4).ref("CGA").alt("C").build(), variants.get(9));
    }

    private static void assertVariant(@NotNull Variant expectedVariant, @NotNull Variant actualVariant) {
        assertEquals(expectedVariant.chromosome(), actualVariant.chromosome());
        assertEquals(expectedVariant.position(), actualVariant.position());
        assertEquals(expectedVariant.ref(), actualVariant.ref());
        assertEquals(expectedVariant.alt(), actualVariant.alt());
    }

    @NotNull
    private static ImmutableTransvarRecord.Builder baseRecord() {
        return ImmutableTransvarRecord.builder().transcript("irrelevant").chromosome("1").variantSpanMultipleExons(false);
    }

    @NotNull
    private static ImmutableVariant.Builder baseVariant() {
        return ImmutableVariant.builder().chromosome("1");
    }
}
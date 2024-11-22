package com.hartwig.serve.extraction.util;

import static org.junit.Assert.assertEquals;

import com.hartwig.serve.DriverGenesTestFactory;
import com.hartwig.serve.datamodel.molecular.MutationType;

import org.junit.Test;

public class MutationTypeFilterAlgoTest {

    @Test
    public void canDetermineMutationFilter() {
        String tsg = "tsg";
        String onco = "onco";

        MutationTypeFilterAlgo algo = new MutationTypeFilterAlgo(DriverGenesTestFactory.createDriverGenes(tsg, onco));

        assertEquals(MutationType.NONSENSE_OR_FRAMESHIFT, algo.determine(onco, "EXON 9 FRAMESHIFT"));
        assertEquals(MutationType.SPLICE, algo.determine(onco, "Exon 12 splice site insertion"));
        assertEquals(MutationType.SPLICE, algo.determine(onco, "EXON 14 SKIPPING MUTATION"));
        assertEquals(MutationType.INFRAME_DELETION, algo.determine(onco, "EGFR exon 19 deletions"));
        assertEquals(MutationType.INFRAME_DELETION, algo.determine(onco, "EGFR exon 19 del"));
        assertEquals(MutationType.INFRAME_DELETION, algo.determine(onco, "Null (Partial deletion of Exons 2 & 3)"));
        assertEquals(MutationType.INFRAME_INSERTION, algo.determine(onco, "Exon 20 insertions"));
        assertEquals(MutationType.INFRAME_INSERTION, algo.determine(onco, "Exon 20 ins"));
        assertEquals(MutationType.INFRAME, algo.determine(onco, "Exon 20 insertions/deletions"));
        assertEquals(MutationType.INFRAME, algo.determine(onco, "Exon 19 deletion/insertion"));
        assertEquals(MutationType.MISSENSE, algo.determine(onco, "mut"));
        assertEquals(MutationType.INFRAME_INSERTION, algo.determine(onco, "insertion"));
        assertEquals(MutationType.ANY, algo.determine(tsg, "mut"));
        assertEquals(MutationType.NONSENSE_OR_FRAMESHIFT, algo.determine(tsg, "frameshift"));

        assertEquals(MutationType.ANY, algo.determine("NOT-A-GENE", "abcd"));
    }
}
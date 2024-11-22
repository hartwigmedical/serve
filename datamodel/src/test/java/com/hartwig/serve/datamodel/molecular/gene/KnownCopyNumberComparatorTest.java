package com.hartwig.serve.datamodel.molecular.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;

import org.junit.Test;

public class KnownCopyNumberComparatorTest {

    @Test
    public void canSortKnownCopyNumbers() {
        KnownCopyNumber copyNumber1 = GeneTestFactory.knownCopyNumberBuilder().gene("A").geneRole(GeneRole.ONCO).build();
        KnownCopyNumber copyNumber2 = GeneTestFactory.knownCopyNumberBuilder().gene("A").geneRole(GeneRole.TSG).build();
        KnownCopyNumber copyNumber3 = GeneTestFactory.knownCopyNumberBuilder().gene("B").geneRole(GeneRole.ONCO).build();

        List<KnownCopyNumber> copyNumbers = Lists.newArrayList(copyNumber3, copyNumber2, copyNumber1);
        copyNumbers.sort(new KnownCopyNumberComparator());

        assertEquals(copyNumber1, copyNumbers.get(0));
        assertEquals(copyNumber2, copyNumbers.get(1));
        assertEquals(copyNumber3, copyNumbers.get(2));
    }
}
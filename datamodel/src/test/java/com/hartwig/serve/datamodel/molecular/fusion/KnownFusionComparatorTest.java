package com.hartwig.serve.datamodel.molecular.fusion;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;

import org.junit.Test;

public class KnownFusionComparatorTest {

    @Test
    public void canSortKnownFusions() {
        KnownFusion fusion1 = FusionTestFactory.knownFusionBuilder().geneUp("X").proteinEffect(ProteinEffect.GAIN_OF_FUNCTION).build();
        KnownFusion fusion2 = FusionTestFactory.knownFusionBuilder().geneUp("X").proteinEffect(ProteinEffect.LOSS_OF_FUNCTION).build();
        KnownFusion fusion3 = FusionTestFactory.knownFusionBuilder().geneUp("Y").proteinEffect(ProteinEffect.GAIN_OF_FUNCTION).build();

        List<KnownFusion> fusions = Lists.newArrayList(fusion2, fusion1, fusion3);
        fusions.sort(new KnownFusionComparator());

        assertEquals(fusion1, fusions.get(0));
        assertEquals(fusion2, fusions.get(1));
        assertEquals(fusion3, fusions.get(2));
    }
}
package com.hartwig.serve.ckb.classification;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.CkbTestFactory;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;

import org.junit.Test;

public class CkbEventTypeExtractorTest {

    @Test
    public void entriesWithMultipleVariantsAreCombined() {
        Variant variant1 = CkbTestFactory.createVariant();
        Variant variant2 = CkbTestFactory.createVariant();
        CkbEntry entry = CkbTestFactory.createEntry(Lists.newArrayList(variant1, variant2));

        assertEquals(EventType.COMBINED, CkbEventTypeExtractor.classify(entry));
    }

    @Test
    public void canClassifyCharacteristics() {
        Variant characteristic =
                CkbTestFactory.createVariant(CkbConstants.NO_GENE, CkbConstants.MSI_NEGATIVE, CkbConstants.MSI_NEGATIVE, null);
        assertEquals(EventType.CHARACTERISTIC, CkbEventTypeExtractor.classify(CkbTestFactory.createEntry(characteristic)));
    }
}
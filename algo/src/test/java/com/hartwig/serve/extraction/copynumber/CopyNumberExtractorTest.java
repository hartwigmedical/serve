package com.hartwig.serve.extraction.copynumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.common.collect.Sets;
import com.hartwig.serve.DriverGenesTestFactory;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CopyNumberExtractorTest {

    @Test
    public void canCheckFilterInCatalog() {
        CopyNumberExtractor copyNumberExtractorIgnore = createTestExtractor(DriverInconsistencyMode.IGNORE);
        assertEquals(GeneEvent.AMPLIFICATION, copyNumberExtractorIgnore.extract("AKT1", EventType.AMPLIFICATION).event());

        CopyNumberExtractor copyNumberExtractorWarn = createTestExtractor(DriverInconsistencyMode.WARN_ONLY);
        assertEquals(GeneEvent.AMPLIFICATION, copyNumberExtractorWarn.extract("AKT1", EventType.AMPLIFICATION).event());

        CopyNumberExtractor copyNumberExtractorFilter = createTestExtractor(DriverInconsistencyMode.FILTER);
        assertEquals(GeneEvent.OVEREXPRESSION, copyNumberExtractorFilter.extract("AKT1", EventType.OVEREXPRESSION).event());

        CopyNumberExtractor copyNumberExtractorFilterDel = createTestExtractor(DriverInconsistencyMode.FILTER);
        assertNull(copyNumberExtractorFilterDel.extract("AKT1", EventType.DELETION));
    }

    @Test
    public void canCheckFilterNotInCatalog() {
        CopyNumberExtractor copyNumberExtractorIgnore = createTestExtractor(DriverInconsistencyMode.IGNORE);
        assertEquals(GeneEvent.AMPLIFICATION, copyNumberExtractorIgnore.extract("KRAS", EventType.AMPLIFICATION).event());

        CopyNumberExtractor copyNumberExtractorWarn = createTestExtractor(DriverInconsistencyMode.WARN_ONLY);
        assertEquals(GeneEvent.AMPLIFICATION, copyNumberExtractorWarn.extract("PTEN", EventType.AMPLIFICATION).event());

        CopyNumberExtractor copyNumberExtractorFilter = createTestExtractor(DriverInconsistencyMode.FILTER);
        assertNull(copyNumberExtractorFilter.extract("PTEN", EventType.AMPLIFICATION));
    }

    @Test
    public void canExtractCopyNumbersAmp() {
        CopyNumberExtractor copyNumberExtractor = createTestExtractor(DriverInconsistencyMode.IGNORE);
        GeneAnnotation amp = copyNumberExtractor.extract("AKT1", EventType.AMPLIFICATION);

        assertEquals("AKT1", amp.gene());
        assertEquals(GeneEvent.AMPLIFICATION, amp.event());
    }

    @Test
    public void canFilterAmpOnUnknownGene() {
        CopyNumberExtractor copyNumberExtractor = createTestExtractor(DriverInconsistencyMode.IGNORE);
        assertNull(copyNumberExtractor.extract("NOT-A-GENE", EventType.AMPLIFICATION));
    }

    @Test
    public void canExtractCopyNumbersDel() {
        CopyNumberExtractor copyNumberExtractor = createTestExtractor(DriverInconsistencyMode.IGNORE);
        GeneAnnotation del = copyNumberExtractor.extract("PTEN", EventType.UNDEREXPRESSION);

        assertEquals("PTEN", del.gene());
        assertEquals(GeneEvent.UNDEREXPRESSION, del.event());
    }

    @Test
    public void canFilterDelOnUnknownGene() {
        CopyNumberExtractor copyNumberExtractor = createTestExtractor(DriverInconsistencyMode.IGNORE);
        assertNull(copyNumberExtractor.extract("NOT-A-GENE", EventType.DELETION));
    }

    @NotNull
    private static CopyNumberExtractor createTestExtractor(@NotNull DriverInconsistencyMode mode) {
        return new CopyNumberExtractor(new GeneChecker(Sets.newHashSet("PTEN", "AKT1", "KRAS")),
                DriverGenesTestFactory.createDriverGenes("KRAS", "AKT1"),
                mode);
    }
}
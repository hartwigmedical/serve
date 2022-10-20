package com.hartwig.serve.sources.vicc.curation;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.junit.Test;

public class EvidenceLevelCuratorTest {

    @Test
    public void canCurateEvidenceLevels() {
        EvidenceLevelCurator curator = new EvidenceLevelCurator();

        EvidenceLevelCurationKey firstKey = EvidenceLevelCurationFactory.EVIDENCE_LEVEL_MAPPINGS.keySet().iterator().next();

        EvidenceLevel curatedLevel =
                curator.curate(firstKey.source(), firstKey.genes(), firstKey.treatment(), firstKey.level(), firstKey.direction());
        assertEquals(EvidenceLevelCurationFactory.EVIDENCE_LEVEL_MAPPINGS.get(firstKey), curatedLevel);

        EvidenceLevel nonCuratedLevel =
                curator.curate(ViccSource.CIVIC, Lists.newArrayList(), "treatment", EvidenceLevel.A, EvidenceDirection.RESPONSIVE);
        assertEquals(EvidenceLevel.A, nonCuratedLevel);

        curator.reportUnusedCurationKeys();
    }
}
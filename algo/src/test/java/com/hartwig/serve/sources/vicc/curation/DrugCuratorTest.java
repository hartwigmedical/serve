package com.hartwig.serve.sources.vicc.curation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevel;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.junit.Test;

public class DrugCuratorTest {

    @Test
    public void canCurateDrugs() {
        DrugCurator curator = new DrugCurator();

        DrugCurationKey firstKey = DrugCurationFactory.DRUG_MAPPINGS.keySet().iterator().next();

        List<List<String>> curatedDrugs = curator.curate(firstKey.source(), firstKey.level(), firstKey.treatment());
        assertEquals(DrugCurationFactory.DRUG_MAPPINGS.get(firstKey).drugs(), curatedDrugs);

        List<List<String>> nonCuratedDrugs = curator.curate(ViccSource.CIVIC, EvidenceLevel.A, "treatment");
        List<String> treatments = Lists.newArrayList("treatment");
        List<List<String>> listedTreatment = Lists.newArrayList();
        listedTreatment.add(treatments);
        assertEquals(listedTreatment, nonCuratedDrugs);

        curator.reportUnusedCurationKeys();
    }

    @Test
    public void canFilterDrugs() {
        DrugCurator curator = new DrugCurator();

        DrugCurationKey firstKey = DrugCurationFactory.DRUG_FILTERS.iterator().next();

        assertTrue(curator.curate(firstKey.source(), firstKey.level(), firstKey.treatment()).isEmpty());

        curator.reportUnusedCurationKeys();
    }
}
package com.hartwig.serve.sources.ckb.treatmentapproach;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import com.google.common.io.Resources;

import org.junit.Test;

public class TreatmentApproachCurationFileTest {

    private static final String TEST_CKB_TREATMENT_APPROACH_CURATION_TSV =
            Resources.getResource("ckb_curation/ckb_treatment_approach_curation.tsv").getPath();

    @Test
    public void canReadCkbDrugClassCurationTsv() throws IOException {
        Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> treatmentApproachMap =
                TreatmentApproachCurationFile.read(TEST_CKB_TREATMENT_APPROACH_CURATION_TSV);
        assertEquals(4, treatmentApproachMap.size());
    }
}
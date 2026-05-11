package com.hartwig.serve.sources.curatedtrials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class CuratedTrialReaderTest {

    private static final String EXAMPLE_JSON = Resources.getResource("curatedtrials/example.json").getPath();

    @Test
    public void canReadCuratedTrialsJson() throws IOException {
        List<CuratedTrialEntry> entries = CuratedTrialReader.read(EXAMPLE_JSON);

        // Only TRIAL-001 should be read — TRIAL-002 has an nctId and must be skipped
        assertEquals(1, entries.size());

        CuratedTrialEntry entry = entries.get(0);
        assertEquals("TRIAL-001", entry.trialId());
        assertEquals("Test Trial", entry.title());
        assertEquals("TT", entry.acronym());

        // Therapy
        assertEquals(1, entry.therapyNames().size());
        assertEquals("Everolimus", entry.therapyNames().iterator().next());

        // Country
        assertEquals(1, entry.countries().size());
        assertEquals("Netherlands", entry.countries().iterator().next().name());

        // Indication
        assertEquals(1, entry.indications().size());
        assertEquals("lung non-small cell carcinoma", entry.indications().iterator().next().applicableType().name());
        assertEquals("3908", entry.indications().iterator().next().applicableType().doid());

        // Molecular criteria
        assertEquals(1, entry.anyMolecularCriteria().size());
        assertEquals(1, entry.anyMolecularCriteria().get(0).genes().size());

        var gene = entry.anyMolecularCriteria().get(0).genes().iterator().next();
        assertEquals("EGFR", gene.gene());
        assertEquals(com.hartwig.serve.datamodel.molecular.gene.GeneEvent.ACTIVATION, gene.event());
        assertEquals("EGFR act mut", gene.sourceEvent());
    }
}
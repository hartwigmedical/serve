package com.hartwig.serve.sources.hartwig.trial;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Ignore;
import org.junit.Test;

public class HartwigTrialReaderTest {

    private static final String EXAMPLE_TSV = Resources.getResource("hartwig/hartwig_curated_trials.tsv").getPath();

    @Test
    @Ignore
    public void canReadHartwigTrialTsv() throws IOException {
        List<HartwigTrialEntry> entries = HartwigTrialReader.read(EXAMPLE_TSV);
        
        assertEquals(1, entries.size());

        HartwigTrialEntry entry = entries.get(0);
        assertEquals("NCT00001", entry.nctId());
        assertEquals("This is test trial 1", entry.title());
        assertEquals("TRIAL-1", entry.acronym());
        assertEquals("Netherlands", entry.country());
        assertEquals("Colorectal Cancer", entry.cancerType());
        assertEquals("123", entry.cancerTypeDoid());
        assertEquals("FUSION", entry.molecularCriteriumType());
        assertEquals("EML4::ALK", entry.molecularCriterium());
    }
}
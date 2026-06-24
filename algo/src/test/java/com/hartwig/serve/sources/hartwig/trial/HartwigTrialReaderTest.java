package com.hartwig.serve.sources.hartwig.trial;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class HartwigTrialReaderTest {

    private static final String EXAMPLE_TSV = Resources.getResource("hartwig/hartwig_curated_trials.tsv").getPath();

    @Test
    public void canReadHartwigTrialTsv() throws IOException {
        List<HartwigTrialEntry> entries = HartwigTrialReader.read(EXAMPLE_TSV);
        
        assertEquals(1, entries.size());

        HartwigTrialEntry entry = entries.get(0);
        assertEquals(LocalDate.of(2026, 1, 20), entry.date());
        assertEquals("NCT00001", entry.nctId());
        assertEquals("This is test trial 1", entry.title());
        assertEquals("TRIAL-1", entry.acronym());
        assertEquals("Netherlands", entry.country());
        assertEquals("Colorectal Cancer", entry.cancerType());
        assertEquals("123", entry.cancerTypeDoid());
        assertEquals("-", entry.actionableGene());
        assertEquals("EML4 - ALK Fusion", entry.actionableEvent());
        assertEquals("https://url.com", entry.url());
    }
}
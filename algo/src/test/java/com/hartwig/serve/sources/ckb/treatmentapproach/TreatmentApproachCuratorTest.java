package com.hartwig.serve.sources.ckb.treatmentapproach;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.EvidenceDirection;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class TreatmentApproachCuratorTest {

    @Test
    public void canTestMatchEntries() {
        Map<TreatmentApproachCurationEntryKey, TreatmentApproachCurationEntry> curationEntries = Maps.newHashMap();
        curationEntries.put(TreatmentApproachTestFactory.createCurationKey("Olutasidenib",
                        Strings.EMPTY,
                        "IDH1 GENE_LEVEL",
                        EvidenceDirection.PREDICTED_RESPONSIVE),
                TreatmentApproachTestFactory.createCurationEntry(TreatmentApproachCurationType.TREATMENT_APPROACH_CURATION,
                        "Olutasidenib",
                        Strings.EMPTY,
                        "IDH1 GENE_LEVEL",
                        EvidenceDirection.PREDICTED_RESPONSIVE,
                        "AA"));

        curationEntries.put(TreatmentApproachTestFactory.createCurationKey("B", "B", "BRAF amplification", EvidenceDirection.RESPONSIVE),
                TreatmentApproachTestFactory.createCurationEntry(TreatmentApproachCurationType.TREATMENT_APPROACH_CURATION,
                        "B",
                        "B",
                        "BRAF amplification",
                        EvidenceDirection.RESPONSIVE,
                        "BB"));

        curationEntries.put(TreatmentApproachTestFactory.createCurationKey("C", "C", "BRAF amplification", EvidenceDirection.RESPONSIVE),
                TreatmentApproachTestFactory.createCurationEntry(TreatmentApproachCurationType.EVENT_TREATMENT_APPROACH_CURATION_IGNORE,
                        "C",
                        "C",
                        "BRAF amplification",
                        EvidenceDirection.RESPONSIVE,
                        Strings.EMPTY));

        TreatmentApproachCurator curator = new TreatmentApproachCurator(curationEntries);

        TreatmentApproachCurationEntryKey keyMatch1 = ImmutableTreatmentApproachCurationEntryKey.builder()
                .treatment("Olutasidenib")
                .treatmentApproach(Strings.EMPTY)
                .event("IDH1 GENE_LEVEL")
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .build();

        TreatmentApproachCurationEntryKey keyIgnore = ImmutableTreatmentApproachCurationEntryKey.builder()
                .treatment("C")
                .treatmentApproach("C")
                .event("BRAF amplification")
                .direction(EvidenceDirection.RESPONSIVE)
                .build();

        TreatmentApproachCurationEntryKey keyUnmatch = ImmutableTreatmentApproachCurationEntryKey.builder()
                .treatment("D")
                .treatmentApproach("D")
                .event("BRAF amplification")
                .direction(EvidenceDirection.RESPONSIVE)
                .build();

        assertEquals("AA", curator.isMatch(keyMatch1));
        //assertEquals(Strings.EMPTY, curator.isMatch(keyIgnore));
        //assertEquals(Strings.EMPTY, curator.isMatch(keyUnmatch));
    }
}
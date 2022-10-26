package com.hartwig.serve.sources.ckb.treatmentapproach;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.EvidenceDirection;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class RelevantTreatmentApproachCuratorTest {

    @Test
    public void canTestMatchEntries() {
        Map<RelevantTreatmentApproachCurationEntryKey, RelevantTreatmentApproachCurationEntry> curationEntries = Maps.newHashMap();
        curationEntries.put(TreatmentApproachTestFactory.createCurationKey("Olutasidenib",
                        Strings.EMPTY,
                        "IDH1 GENE_LEVEL",
                        EvidenceDirection.PREDICTED_RESPONSIVE),
                TreatmentApproachTestFactory.createCurationEntry(RelevantTreatmentApproachCurationType.TREATMENT_APPROACH_CURATION,
                        "Olutasidenib",
                        Strings.EMPTY,
                        "IDH1 GENE_LEVEL",
                        EvidenceDirection.PREDICTED_RESPONSIVE,
                        "AA"));

        curationEntries.put(TreatmentApproachTestFactory.createCurationKey("B", "B", "BRAF amplification", EvidenceDirection.RESPONSIVE),
                TreatmentApproachTestFactory.createCurationEntry(RelevantTreatmentApproachCurationType.TREATMENT_APPROACH_CURATION,
                        "B",
                        "B",
                        "BRAF amplification",
                        EvidenceDirection.RESPONSIVE,
                        "BB"));

        curationEntries.put(TreatmentApproachTestFactory.createCurationKey("C", "C", "BRAF amplification", EvidenceDirection.RESPONSIVE),
                TreatmentApproachTestFactory.createCurationEntry(RelevantTreatmentApproachCurationType.EVENT_TREATMENT_APPROACH_CURATION_IGNORE,
                        "C",
                        "C",
                        "BRAF amplification",
                        EvidenceDirection.RESPONSIVE,
                        Strings.EMPTY));

        RelevantTreatmentApproachCurator curator = new RelevantTreatmentApproachCurator(curationEntries);

        RelevantTreatmentApproachCurationEntryKey keyMatch1 = ImmutableRelevantTreatmentApproachCurationEntryKey.builder()
                .treatment("Olutasidenib")
                .treatmentApproach(Strings.EMPTY)
                .event("IDH1 GENE_LEVEL")
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .build();

        RelevantTreatmentApproachCurationEntryKey keyIgnore = ImmutableRelevantTreatmentApproachCurationEntryKey.builder()
                .treatment("C")
                .treatmentApproach("C")
                .event("BRAF amplification")
                .direction(EvidenceDirection.RESPONSIVE)
                .build();

        RelevantTreatmentApproachCurationEntryKey keyUnmatch = ImmutableRelevantTreatmentApproachCurationEntryKey.builder()
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
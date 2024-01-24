package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.CkbTestFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CkbBlacklistEvidenceTest {

    @Test
    public void canBlacklistWholeTherapy() {
        CkbBlacklistEvidence ckbBlacklistEvidence = new CkbBlacklistEvidence(createBlacklistEvidenceEntryListTherapy(CkbBlacklistEvidenceReason.EVIDENCE_THERAPY, "AB"));
        CkbEntry entry =
                CkbTestFactory.createEntry("KRAS", "deletion", "KRAS deletion", "sensitive", "Emerging", "AB", "AB", "A", "DOID:162");
        assertTrue(ckbBlacklistEvidence.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistEvidence.reportUnusedBlacklistEntries();
    }

    @Test
    public void canBlacklistTherapyCancerType() {
        CkbBlacklistEvidence ckbBlacklistEvidence = new CkbBlacklistEvidence(createBlacklistEvidenceEntryListTherapy(CkbBlacklistEvidenceReason.EVIDENCE_CANCER_TYPE, "AB", "Solid tumor"));
        CkbEntry entry =
                CkbTestFactory.createEntry("KRAS", "deletion", "KRAS deletion", "sensitive", "Emerging", "AB", "Solid tumor", "A", "DOID:162");
        assertTrue(ckbBlacklistEvidence.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistEvidence.reportUnusedBlacklistEntries();
    }

    @Test
    public void canBlacklistTherapyCancerTypeMolecularProfile() {
        CkbBlacklistEvidence ckbBlacklistEvidence = new CkbBlacklistEvidence(createBlacklistEvidenceEntryListMolecularProfile(CkbBlacklistEvidenceReason.EVIDENCE_MOLECULAR_PROFILE, "AB", "Solid tumor", "KRAS deletion"));
        CkbEntry entry =
                CkbTestFactory.createEntryWithProfileName("KRAS", "deletion", "KRAS deletion",
                        "sensitive", "Emerging", "AB", "Solid tumor", "A", "DOID:162", "KRAS deletion");
        assertTrue(ckbBlacklistEvidence.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistEvidence.reportUnusedBlacklistEntries();
    }

    @Test
    public void canBlacklistAllMolecularProfile() {
        CkbBlacklistEvidence ckbBlacklistEvidence = new CkbBlacklistEvidence(createBlacklistEvidenceEntryListMolecularProfile(CkbBlacklistEvidenceReason.ALL_MOLECULAR_PROFILE, "AB", "Solid tumor", "KRAS deletion"));
        CkbEntry entry =
                CkbTestFactory.createEntryWithProfileName("KRAS", "deletion", "KRAS deletion",
                        "sensitive", "Emerging", "AB", "Solid tumor", "A", "DOID:162", "KRAS deletion");
        assertTrue(ckbBlacklistEvidence.run(Lists.newArrayList(entry)).isEmpty());

        ckbBlacklistEvidence.reportUnusedBlacklistEntries();
    }

    @NotNull
    private static List<CkbBlacklistEvidenceEntry> createBlacklistEvidenceEntryListTherapy(@NotNull CkbBlacklistEvidenceReason reason, @NotNull String therapy) {
        return Lists.newArrayList(ImmutableCkbBlacklistEvidenceEntry.builder().ckbBlacklistEvidenceReason(reason).therapy(therapy).build());
    }

    @NotNull
    private static List<CkbBlacklistEvidenceEntry> createBlacklistEvidenceEntryListTherapy(@NotNull CkbBlacklistEvidenceReason reason, @NotNull String therapy, @NotNull String cancerType) {
        return Lists.newArrayList(ImmutableCkbBlacklistEvidenceEntry.builder().ckbBlacklistEvidenceReason(reason).therapy(therapy).cancerType(cancerType).build());
    }

    @NotNull
    private static List<CkbBlacklistEvidenceEntry> createBlacklistEvidenceEntryListMolecularProfile(@NotNull CkbBlacklistEvidenceReason reason, @NotNull String therapy, @NotNull String cancerType, @NotNull String molecularProfile) {
        return Lists.newArrayList(ImmutableCkbBlacklistEvidenceEntry.builder().ckbBlacklistEvidenceReason(reason).therapy(therapy).cancerType(cancerType).molecularProfile(molecularProfile).build());
    }
}
package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class CkbBlacklistEvidenceFile {

    private static final String FIELD_DELIMITER = "\t";

    private CkbBlacklistEvidenceFile() {
    }

    @NotNull
    public static List<CkbBlacklistEvidenceEntry> read(@NotNull String ckbBlacklistEvidenceTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbBlacklistEvidenceTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        // Skip header
        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbBlacklistEvidenceEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbBlacklistEvidenceEntry> blacklistedEntries = Lists.newArrayList();
        for (String line : lines) {
            blacklistedEntries.add(fromLine(line, fields));
        }
        return blacklistedEntries;
    }

    @NotNull
    private static CkbBlacklistEvidenceEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        CkbBlacklistEvidenceReason reason = CkbBlacklistEvidenceReason.valueOf(values[fields.get("blacklistType")]);

        String therapy = null;
        String cancerType = null;
        String molecularProfile = null;

        if (Sets.newHashSet(CkbBlacklistEvidenceReason.EVIDENCE_THERAPY).contains(reason)) {
            therapy = values[fields.get("therapyName")];
        }
        if (Sets.newHashSet(CkbBlacklistEvidenceReason.EVIDENCE_THERAPY, CkbBlacklistEvidenceReason.EVIDENCE_CANCER_TYPE).contains(reason)) {
            therapy = values[fields.get("cancerType")];
        }
        if (Sets.newHashSet(CkbBlacklistEvidenceReason.EVIDENCE_THERAPY,CkbBlacklistEvidenceReason.EVIDENCE_CANCER_TYPE, CkbBlacklistEvidenceReason.EVIDENCE_MOLECULAR_PROFILE).contains(reason)) {
            cancerType = values[fields.get("molecularProfile")];
        }
        if (Sets.newHashSet(CkbBlacklistEvidenceReason.ALL_MOLECULAR_PROFILE).contains(reason)) {
            molecularProfile = values[fields.get("molecularProfile")];
        }

        return ImmutableCkbBlacklistEvidenceEntry.builder()
                .ckbBlacklistEvidenceReason(reason)
                .therapy(therapy)
                .cancerType(cancerType)
                .molecularProfile(molecularProfile)
                .build();
    }
}
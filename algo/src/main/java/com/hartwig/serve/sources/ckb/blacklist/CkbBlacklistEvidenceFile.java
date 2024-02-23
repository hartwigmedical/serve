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
        String gene = null;
        String event = null;
        String level = null;

        if (Sets.newHashSet(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY,
                CkbBlacklistEvidenceReason.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
                CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT).contains(reason)) {
            therapy = values[fields.get("therapyName")];
        }
        if (Sets.newHashSet(CkbBlacklistEvidenceReason.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
                CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT).contains(reason)) {
            cancerType = values[fields.get("cancerType")];
        }
        if (Sets.newHashSet(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                CkbBlacklistEvidenceReason.ALL_EVIDENCE_BASED_ON_GENE, CkbBlacklistEvidenceReason.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT).contains(reason)) {
            gene = values[fields.get("gene")];
        }
        if (Sets.newHashSet(CkbBlacklistEvidenceReason.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
                CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT).contains(reason)) {
            event = values[fields.get("event")];
        }

        return ImmutableCkbBlacklistEvidenceEntry.builder()
                .ckbBlacklistEvidenceReason(reason)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();
    }
}
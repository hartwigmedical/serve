package com.hartwig.serve.sources.ckb.blacklist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

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
        List<CkbBlacklistEvidenceEntry> blacklistEntries = Lists.newArrayList();
        for (String line : lines) {
            blacklistEntries.add(fromLine(line, fields));
        }
        return blacklistEntries;
    }

    @NotNull
    private static CkbBlacklistEvidenceEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        CkbBlacklistEvidenceType type = CkbBlacklistEvidenceType.valueOf(values[fields.get("blacklistType")]);

        String therapy = null;
        String cancerType = null;
        String gene = null;
        String event = null;
        EvidenceLevel level = null;

        if (BlacklistConstants.EVIDENCE_BLACKLIST_TYPES_CONTAINING_THERAPY.contains(type)) {
            therapy = values[fields.get("therapyName")];
        }
        if (BlacklistConstants.EVIDENCE_BLACKLIST_TYPES_CONTAINING_CANCER_TYPE.contains(type)) {
            cancerType = values[fields.get("cancerType")];
        }
        if (BlacklistConstants.EVIDENCE_BLACKLIST_TYPES_CONTAINING_GENE.contains(type)) {
            gene = values[fields.get("gene")];
        }
        if (BlacklistConstants.EVIDENCE_BLACKLIST_TYPES_CONTAINING_EVENT.contains(type)) {
            event = values[fields.get("event")];
        }

        if (!values[fields.get("level")].isEmpty()) {
            level = EvidenceLevel.valueOf(values[fields.get("level")]);
        }

        return ImmutableCkbBlacklistEvidenceEntry.builder()
                .type(type)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();
    }
}
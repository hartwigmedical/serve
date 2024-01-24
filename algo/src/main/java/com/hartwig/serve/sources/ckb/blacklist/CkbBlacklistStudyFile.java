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

public class CkbBlacklistStudyFile {


    private static final String FIELD_DELIMITER = "\t";

    private CkbBlacklistStudyFile() {
    }

    @NotNull
    public static List<CkbBlacklistStudyEntry> read(@NotNull String ckbBlacklistStudiesTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbBlacklistStudiesTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        // Skip header
        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbBlacklistStudyEntry> blacklistedEntries = Lists.newArrayList();
        for (String line : lines) {
            blacklistedEntries.add(fromLine(line, fields));
        }
        return blacklistedEntries;
    }

    @NotNull
    private static CkbBlacklistStudyEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        CkbBlacklistReason reason = CkbBlacklistReason.valueOf(values[fields.get("blacklistType")]);

        String therapy = null;
        String cancerType = null;
        String molecularProfile = null;

        if (Sets.newHashSet(CkbBlacklistReason.STUDY_THERAPY, CkbBlacklistReason.STUDY_CANCER_TYPE, CkbBlacklistReason.STUDY_MOLECULAR_PROFILE).contains(reason)) {
            therapy = values[fields.get("therapyName")];
        }
        if (Sets.newHashSet(CkbBlacklistReason.STUDY_CANCER_TYPE, CkbBlacklistReason.STUDY_MOLECULAR_PROFILE).contains(reason)) {
            cancerType = values[fields.get("cancerType")];
        }
        if (Sets.newHashSet(CkbBlacklistReason.STUDY_MOLECULAR_PROFILE).contains(reason)) {
            molecularProfile = values[fields.get("molecularProfile")];
        }
        return ImmutableCkbBlacklistStudyEntry.builder()
                .ckbBlacklistReason(reason)
                .nctId(values[fields.get("nctId")])
                .therapy(therapy)
                .cancerType(cancerType)
                .molecularProfile(molecularProfile)
                .build();
    }
}
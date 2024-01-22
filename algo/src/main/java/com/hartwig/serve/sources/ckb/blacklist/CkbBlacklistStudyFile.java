package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Sets;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class CkbBlacklistStudyFile {


    private static final String FIELD_DELIMITER = "\t";

    private CkbBlacklistStudyFile() {
    }

    @NotNull
    public static List<CkbBlacklistStudyEntry> read(@NotNull String ckbBlacklistStudiesTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbBlacklistStudiesTsv).toPath());
        // Skip header
        return fromLines(lines.subList(1, lines.size()));
    }

    @NotNull
    private static List<CkbBlacklistStudyEntry> fromLines(@NotNull List<String> lines) {
        List<CkbBlacklistStudyEntry> blacklistedEntries = Lists.newArrayList();
        for (String line : lines) {
            blacklistedEntries.add(fromLine(line));
        }
        return blacklistedEntries;
    }

    @NotNull
    private static CkbBlacklistStudyEntry fromLine(@NotNull String line) {
        String[] values = line.split(FIELD_DELIMITER);

        CkbBlacklistReason reason = CkbBlacklistReason.valueOf(values[0]);

        String therapy = null;
        String cancerType = null;
        String molecularProfile = null;

        if (Sets.newHashSet(CkbBlacklistReason.STUDY_THERAPY, CkbBlacklistReason.STUDY_CANCER_TYPE, CkbBlacklistReason.STUDY_MOLECULAR_PROFILE).contains(reason)) {
            therapy = values[2];
        }
        if (Sets.newHashSet(CkbBlacklistReason.STUDY_CANCER_TYPE, CkbBlacklistReason.STUDY_MOLECULAR_PROFILE).contains(reason)) {
            cancerType = values[3];
        }
        if (Sets.newHashSet(CkbBlacklistReason.STUDY_MOLECULAR_PROFILE).contains(reason)) {
            molecularProfile = values[4];
        }
        return ImmutableCkbBlacklistStudyEntry.builder()
                .ckbBlacklistReason(reason)
                .nctId(values[1])
                .therapy(therapy)
                .cancerType(cancerType)
                .molecularProfile(molecularProfile)
                .build();
    }
}
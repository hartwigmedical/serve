package com.hartwig.serve.sources.ckb.blacklist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;

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
        CkbBlacklistStudyType type = CkbBlacklistStudyType.valueOf(values[fields.get("blacklistType")]);

        String therapy = null;
        String cancerType = null;
        String gene = null;
        String event = null;

        if (Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY,
                CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
                CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT).contains(type)) {
            therapy = values[fields.get("therapyName")];
        }
        if (Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
                CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT).contains(type)) {
            cancerType = values[fields.get("cancerType")];
        }
        if (Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE).contains(type)) {
            gene = values[fields.get("gene")];
        }

        if (Sets.newHashSet(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE_AND_EVENT).contains(type)) {
            event = values[fields.get("event")];
        }
        return ImmutableCkbBlacklistStudyEntry.builder()
                .type(type)
                .nctId(values[fields.get("nctId")])
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();
    }
}
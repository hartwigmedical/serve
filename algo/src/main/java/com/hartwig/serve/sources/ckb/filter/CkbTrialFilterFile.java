package com.hartwig.serve.sources.ckb.filter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.serialization.SerializationUtil;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public final class CkbTrialFilterFile {

    private static final String FIELD_DELIMITER = "\t";

    private final static Set<CkbTrialFilterType> TRIAL_FILTER_TYPES_CONTAINING_THERAPY =
            Sets.newHashSet(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY,
                    CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE,
                    CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);

    private final static Set<CkbTrialFilterType> TRIAL_FILTER_TYPES_CONTAINING_CANCER_TYPE =
            Sets.newHashSet(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE,
                    CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);

    private final static Set<CkbTrialFilterType> TRIAL_FILTER_TYPES_CONTAINING_GENE =
            Sets.newHashSet(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbTrialFilterType.ALL_TRIALS_BASED_ON_GENE);

    private final static Set<CkbTrialFilterType> TRIAL_FILTER_TYPES_CONTAINING_EVENT =
            Sets.newHashSet(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                    CkbTrialFilterType.ALL_TRIALS_BASED_ON_GENE_AND_EVENT);

    private CkbTrialFilterFile() {
    }

    @NotNull
    public static List<CkbTrialFilterEntry> read(@NotNull String ckbTrialFilterTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbTrialFilterTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        // Skip header
        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbTrialFilterEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbTrialFilterEntry> entries = Lists.newArrayList();
        for (String line : lines) {
            entries.add(fromLine(line, fields));
        }
        return entries;
    }

    @NotNull
    private static CkbTrialFilterEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        CkbTrialFilterType type = CkbTrialFilterType.valueOf(values[fields.get("filterType")]);

        String therapy = null;
        String cancerType = null;
        String gene = null;
        String event = null;

        if (TRIAL_FILTER_TYPES_CONTAINING_THERAPY.contains(type)) {
            therapy = values[fields.get("therapyName")];
        }
        if (TRIAL_FILTER_TYPES_CONTAINING_CANCER_TYPE.contains(type)) {
            cancerType = values[fields.get("cancerType")];
        }
        if (TRIAL_FILTER_TYPES_CONTAINING_GENE.contains(type)) {
            gene = values[fields.get("gene")];
        }

        if (TRIAL_FILTER_TYPES_CONTAINING_EVENT.contains(type)) {
            event = values[fields.get("event")];
        }
        return ImmutableCkbTrialFilterEntry.builder()
                .type(type)
                .nctId(values[fields.get("nctId")])
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();
    }
}
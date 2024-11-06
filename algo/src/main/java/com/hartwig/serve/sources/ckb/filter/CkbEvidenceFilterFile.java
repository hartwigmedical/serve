package com.hartwig.serve.sources.ckb.filter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.serialization.SerializationUtil;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public final class CkbEvidenceFilterFile {

    private static final String FIELD_DELIMITER = "\t";

    private final static Set<CkbEvidenceFilterType> EVIDENCE_FILTER_TYPES_CONTAINING_THERAPY =
            Sets.newHashSet(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY,
                    CkbEvidenceFilterType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
                    CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);

    private final static Set<CkbEvidenceFilterType> EVIDENCE_FILTER_TYPES_CONTAINING_CANCER_TYPE = Sets.newHashSet(
            CkbEvidenceFilterType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
            CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
            CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);

    private final static Set<CkbEvidenceFilterType> EVIDENCE_FILTER_TYPES_CONTAINING_GENE =
            Sets.newHashSet(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                    CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                    CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE,
                    CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT);

    private final static Set<CkbEvidenceFilterType> EVIDENCE_FILTER_TYPES_CONTAINING_EVENT =
            Sets.newHashSet(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
                    CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT);

    private CkbEvidenceFilterFile() {
    }

    @NotNull
    public static List<CkbEvidenceFilterEntry> read(@NotNull String ckbEvidenceFilterTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(ckbEvidenceFilterTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        // Skip header
        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    private static List<CkbEvidenceFilterEntry> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<CkbEvidenceFilterEntry> entries = Lists.newArrayList();
        for (String line : lines) {
            entries.add(fromLine(line, fields));
        }
        return entries;
    }

    @NotNull
    private static CkbEvidenceFilterEntry fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);
        CkbEvidenceFilterType type = CkbEvidenceFilterType.valueOf(values[fields.get("filterType")]);

        String therapy = null;
        String cancerType = null;
        String gene = null;
        String event = null;
        EvidenceLevel level = null;

        if (EVIDENCE_FILTER_TYPES_CONTAINING_THERAPY.contains(type)) {
            therapy = values[fields.get("therapyName")];
        }
        if (EVIDENCE_FILTER_TYPES_CONTAINING_CANCER_TYPE.contains(type)) {
            cancerType = values[fields.get("cancerType")];
        }
        if (EVIDENCE_FILTER_TYPES_CONTAINING_GENE.contains(type)) {
            gene = values[fields.get("gene")];
        }
        if (EVIDENCE_FILTER_TYPES_CONTAINING_EVENT.contains(type)) {
            event = values[fields.get("event")];
        }

        if (!values[fields.get("level")].isEmpty()) {
            level = EvidenceLevel.valueOf(values[fields.get("level")]);
        }

        return ImmutableCkbEvidenceFilterEntry.builder()
                .type(type)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();
    }
}
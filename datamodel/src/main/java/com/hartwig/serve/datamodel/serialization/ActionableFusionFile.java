package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ActionableFusionComparator;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class ActionableFusionFile {

    private static final String ACTIONABLE_FUSION_TSV = "ActionableFusions.tsv";

    private ActionableFusionFile() {
    }

    @NotNull
    public static String actionableFusionTsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_FUSION_TSV);
    }

    public static void write(@NotNull String actionableFusionTsv, @NotNull Iterable<ActionableFusion> actionableFusions)
            throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableFusions));

        Files.write(new File(actionableFusionTsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableFusion> read(@NotNull String actionableFusionTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableFusionTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), ActionableFileUtil.FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("geneUp")
                .add("minExonUp")
                .add("maxExonUp")
                .add("geneDown")
                .add("minExonDown")
                .add("maxExonDown")
                .add(ActionableFileUtil.header())
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableFusion> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableFusion> actionableFusions = Lists.newArrayList();
        for (String line : lines) {
            actionableFusions.add(fromLine(line, fields));
        }
        return actionableFusions;
    }

    @NotNull
    private static ActionableFusion fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER, -1);

        return ImmutableActionableFusion.builder()
                .from(ActionableFileUtil.fromLine(values, fields))
                .geneUp(values[fields.get("geneUp")])
                .minExonUp(SerializationUtil.optionalInteger(values[fields.get("minExonUp")]))
                .maxExonUp(SerializationUtil.optionalInteger(values[fields.get("maxExonUp")]))
                .geneDown(values[fields.get("geneDown")])
                .minExonDown(SerializationUtil.optionalInteger(values[fields.get("minExonDown")]))
                .maxExonDown(SerializationUtil.optionalInteger(values[fields.get("maxExonDown")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<ActionableFusion> actionableFusions) {
        List<String> lines = Lists.newArrayList();
        for (ActionableFusion actionableFusion : sort(actionableFusions)) {
            lines.add(toLine(actionableFusion));
        }
        return lines;
    }

    @NotNull
    private static List<ActionableFusion> sort(@NotNull Iterable<ActionableFusion> actionableFusions) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableFusion> sorted = Lists.newArrayList(actionableFusions);
        sorted.sort(new ActionableFusionComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableFusion fusion) {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(fusion.geneUp())
                .add(SerializationUtil.nullableInteger(fusion.minExonUp()))
                .add(SerializationUtil.nullableInteger(fusion.maxExonUp()))
                .add(fusion.geneDown())
                .add(SerializationUtil.nullableInteger(fusion.minExonDown()))
                .add(SerializationUtil.nullableInteger(fusion.maxExonDown()))
                .add(ActionableFileUtil.toLine(fusion))
                .toString();
    }
}

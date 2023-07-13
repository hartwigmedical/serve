package com.hartwig.serve.datamodel.serialization;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.range.*;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ActionableExonFile {


    private static final String ACTIONABLE_EXON_TSV = "ActionableExons.tsv";

    private ActionableExonFile() {
    }

    @NotNull
    public static String actionableExonTsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_EXON_TSV);
    }

    public static void write(@NotNull String actionableExonTsv, @NotNull Iterable<ActionableExon> actionableExons) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableExons));

        Files.write(new File(actionableExonTsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableExon> read(@NotNull String actionableExonTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableExonTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), ActionableFileUtil.FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("gene")
                .add("chromosome")
                .add("start")
                .add("end")
                .add("applicableMutationType")
                .add(ActionableFileUtil.header())
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableExon> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableExon> actionableExons = Lists.newArrayList();
        for (String line : lines) {
            actionableExons.add(fromLine(line, fields));
        }
        return actionableExons;
    }

    @NotNull
    private static ActionableExon fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        return ImmutableActionableExon.builder()
                .from(ActionableFileUtil.fromLine(values, fields))
                .gene(values[fields.get("gene")])
                .chromosome(values[fields.get("chromosome")])
                .start(Integer.parseInt(values[fields.get("start")]))
                .end(Integer.parseInt(values[fields.get("end")]))
                .applicableMutationType(MutationType.valueOf(values[fields.get("applicableMutationType")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<ActionableExon> actionableExons) {
        List<String> lines = Lists.newArrayList();
        for (ActionableExon actionableExon : sort(actionableExons)) {
            lines.add(toLine(actionableExon));
        }
        return lines;
    }

    @NotNull
    public static List<ActionableExon> sort(@NotNull Iterable<ActionableExon> actionableExons) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableExon> sorted = Lists.newArrayList(actionableExons);
        sorted.sort(new ActionableExonComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableExon exon) {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(exon.gene())
                .add(exon.chromosome())
                .add(Long.toString(exon.start()))
                .add(Long.toString(exon.end()))
                .add(exon.applicableMutationType().toString())
                .add(ActionableFileUtil.toLine(exon))
                .toString();
    }
}
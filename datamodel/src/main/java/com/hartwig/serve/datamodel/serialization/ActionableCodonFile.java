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

public class ActionableCodonFile {


    private static final String ACTIONABLE_CODON_TSV = "ActionableCodons.tsv";

    private ActionableCodonFile() {
    }

    @NotNull
    public static String actionableCodonTsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_CODON_TSV);
    }

    public static void write(@NotNull String actionableCodonTsv, @NotNull Iterable<ActionableCodon> actionableCodons) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableCodons));

        Files.write(new File(actionableCodonTsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableCodon> read(@NotNull String actionableCodonTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableCodonTsv).toPath());
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
    static List<ActionableCodon> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableCodon> actionableCodons = Lists.newArrayList();
        for (String line : lines) {
            actionableCodons.add(fromLine(line, fields));
        }
        return actionableCodons;
    }

    @NotNull
    private static ActionableCodon fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        return ImmutableActionableCodon.builder()
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
    static List<String> toLines(@NotNull Iterable<ActionableCodon> actionableCodons) {
        List<String> lines = Lists.newArrayList();
        for (ActionableCodon actionableCodon : sort(actionableCodons)) {
            lines.add(toLine(actionableCodon));
        }
        return lines;
    }

    @NotNull
    public static List<ActionableCodon> sort(@NotNull Iterable<ActionableCodon> actionableCodons) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableCodon> sorted = Lists.newArrayList(actionableCodons);
        sorted.sort(new ActionableCodonComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableCodon codon) {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(codon.gene())
                .add(codon.chromosome())
                .add(Long.toString(codon.start()))
                .add(Long.toString(codon.end()))
                .add(codon.applicableMutationType().toString())
                .add(ActionableFileUtil.toLine(codon))
                .toString();
    }
}

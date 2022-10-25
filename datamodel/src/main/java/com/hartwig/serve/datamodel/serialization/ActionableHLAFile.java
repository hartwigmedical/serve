package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ActionableHLAComparator;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.jetbrains.annotations.NotNull;

public final class ActionableHLAFile {

    private static final String ACTIONABLE_HLA_TSV = "ActionableHLA.tsv";

    private ActionableHLAFile() {
    }

    @NotNull
    public static String actionableHLATsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_HLA_TSV);
    }

    public static void write(@NotNull String actionableHLATsv, @NotNull Iterable<ActionableHLA> actionableHLA) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableHLA));
        Files.write(new File(actionableHLATsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableHLA> read(@NotNull String actionableHLATsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableHLATsv).toPath());
        // Skip header
        return fromLines(lines.subList(1, lines.size()));
    }

    @NotNull
    private static String header() {
        return new StringJoiner(ActionableFileFunctions.FIELD_DELIMITER).add("HLAType").add(ActionableFileFunctions.header()).toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableHLA> fromLines(@NotNull List<String> lines) {
        List<ActionableHLA> actionableHLA = Lists.newArrayList();
        for (String line : lines) {
            actionableHLA.add(fromLine(line));
        }
        return actionableHLA;
    }

    @NotNull
    private static ActionableHLA fromLine(@NotNull String line) {
        String[] values = line.split(ActionableFileFunctions.FIELD_DELIMITER);

        return ImmutableActionableHLA.builder().from(ActionableFileFunctions.fromLine(values, 1)).hlaType(values[0]).build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<ActionableHLA> actionableHLAs) {
        List<String> lines = Lists.newArrayList();
        for (ActionableHLA actionableHLA : sort(actionableHLAs)) {
            lines.add(toLine(actionableHLA));
        }
        return lines;
    }

    @NotNull
    private static List<ActionableHLA> sort(@NotNull Iterable<ActionableHLA> actionableHLAs) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableHLA> sorted = Lists.newArrayList(actionableHLAs);
        sorted.sort(new ActionableHLAComparator());
        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableHLA hla) {
        return new StringJoiner(ActionableFileFunctions.FIELD_DELIMITER).add(hla.hlaType())
                .add(ActionableFileFunctions.toLine(hla))
                .toString();
    }
}
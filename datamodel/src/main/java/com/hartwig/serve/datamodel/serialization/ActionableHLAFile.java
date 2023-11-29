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
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ActionableHLAComparator;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.BackwardsCompatibilityUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class ActionableHLAFile {

    private static final String ACTIONABLE_HLA_TSV = "ActionableHLA.tsv";

    private ActionableHLAFile() {
    }

    @NotNull
    public static String actionableHLATsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_HLA_TSV);
    }

    public static void write(@NotNull String actionableHLATsv, @NotNull Iterable<ActionableHLA> actionableHLA) throws IOException {
        BackwardsCompatibilityUtil.verifyActionableEventsBeforeWrite(actionableHLA);

        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableHLA));

        Files.write(new File(actionableHLATsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableHLA> read(@NotNull String actionableHLATsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableHLATsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), ActionableFileUtil.FIELD_DELIMITER);

        return BackwardsCompatibilityUtil.expandActionableHLA(fromLines(lines.subList(1, lines.size()), fields));
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("hlaAllele").add(ActionableFileUtil.header()).toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableHLA> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableHLA> actionableHLA = Lists.newArrayList();
        for (String line : lines) {
            actionableHLA.add(fromLine(line, fields));
        }
        return actionableHLA;
    }

    @NotNull
    private static ActionableHLA fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        return ImmutableActionableHLA.builder()
                .from(ActionableFileUtil.fromLine(values, fields))
                .hlaAllele(values[fields.get("hlaAllele")])
                .build();
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
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(hla.hlaAllele()).add(ActionableFileUtil.toLine(hla)).toString();
    }
}
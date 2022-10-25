package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristicComparator;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ActionableCharacteristicFile {

    private static final String ACTIONABLE_CHARACTERISTIC_TSV = "ActionableCharacteristics.tsv";

    private ActionableCharacteristicFile() {
    }

    @NotNull
    public static String actionableCharacteristicTsvPath(@NotNull String serveActionabilityDir,
            @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_CHARACTERISTIC_TSV);
    }

    public static void write(@NotNull String actionableCharacteristicTsv,
            @NotNull Iterable<ActionableCharacteristic> actionableCharacteristics) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableCharacteristics));
        Files.write(new File(actionableCharacteristicTsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableCharacteristic> read(@NotNull String actionableCharacteristicTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableCharacteristicTsv).toPath());
        // Skip header
        return fromLines(lines.subList(1, lines.size()));
    }

    @NotNull
    private static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("type")
                .add("cutoffType")
                .add("cutoff")
                .add(ActionableFileUtil.header())
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableCharacteristic> fromLines(@NotNull List<String> lines) {
        List<ActionableCharacteristic> actionableCharacteristics = Lists.newArrayList();
        for (String line : lines) {
            actionableCharacteristics.add(fromLine(line));
        }
        return actionableCharacteristics;
    }

    @NotNull
    private static ActionableCharacteristic fromLine(@NotNull String line) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        return ImmutableActionableCharacteristic.builder()
                .from(ActionableFileUtil.fromLine(values, 3))
                .type(TumorCharacteristicType.valueOf(values[0]))
                .cutoffType(!values[1].equals(Strings.EMPTY) ? TumorCharacteristicCutoffType.valueOf(values[1]) : null)
                .cutoff(!values[2].equals(Strings.EMPTY) ? Double.valueOf(values[2]) : null)
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<ActionableCharacteristic> actionableCharacteristics) {
        List<String> lines = Lists.newArrayList();
        for (ActionableCharacteristic actionableCharacteristic : sort(actionableCharacteristics)) {
            lines.add(toLine(actionableCharacteristic));
        }
        return lines;
    }

    @NotNull
    private static List<ActionableCharacteristic> sort(@NotNull Iterable<ActionableCharacteristic> actionableCharacteristics) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableCharacteristic> sorted = Lists.newArrayList(actionableCharacteristics);
        sorted.sort(new ActionableCharacteristicComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableCharacteristic characteristic) {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(characteristic.type().toString())
                .add(characteristic.cutoffType() != null ? characteristic.cutoffType().toString() : Strings.EMPTY)
                .add(characteristic.cutoff() != null ? Double.toString(characteristic.cutoff()) : Strings.EMPTY)
                .add(ActionableFileUtil.toLine(characteristic))
                .toString();
    }
}

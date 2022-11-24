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
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristicComparator;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ActionableCharacteristicFile {

    private static final String ACTIONABLE_CHARACTERISTIC_TSV = "ActionableCharacteristics.tsv";

    private ActionableCharacteristicFile() {
    }

    @NotNull
    public static String actionableCharacteristicTsvPath(@NotNull String serveActionabilityDir,
            @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_CHARACTERISTIC_TSV);
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
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), ActionableFileUtil.FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("type")
                .add("cutoffType")
                .add("cutoff")
                .add(ActionableFileUtil.header())
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableCharacteristic> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableCharacteristic> actionableCharacteristics = Lists.newArrayList();
        for (String line : lines) {
            actionableCharacteristics.add(fromLine(line, fields));
        }
        return actionableCharacteristics;
    }

    @NotNull
    private static ActionableCharacteristic fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        String cutoffType = SerializationUtil.optionalString(values[fields.get("cutoffType")]);

        return ImmutableActionableCharacteristic.builder()
                .from(ActionableFileUtil.fromLine(values, fields))
                .type(TumorCharacteristicType.valueOf(values[fields.get("type")]))
                .cutoffType(cutoffType != null ? TumorCharacteristicCutoffType.valueOf(cutoffType) : null)
                .cutoff(SerializationUtil.optionalNumber(values[fields.get("cutoff")]))
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
                .add(nullableCutoffType(characteristic.cutoffType()))
                .add(SerializationUtil.nullableNumber(characteristic.cutoff()))
                .add(ActionableFileUtil.toLine(characteristic))
                .toString();
    }

    @NotNull
    public static String nullableCutoffType(@Nullable TumorCharacteristicCutoffType cutoffType) {
        return cutoffType != null ? cutoffType.toString() : Strings.EMPTY;
    }
}

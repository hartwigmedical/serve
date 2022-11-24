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
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspotComparator;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class ActionableHotspotFile {

    private static final String ACTIONABLE_HOTSPOT_TSV = "ActionableHotspots.tsv";

    private ActionableHotspotFile() {
    }

    @NotNull
    public static String actionableHotspotTsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_HOTSPOT_TSV);
    }

    public static void write(@NotNull String actionableHotspotTsv, @NotNull Iterable<ActionableHotspot> actionableHotspots)
            throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableHotspots));

        Files.write(new File(actionableHotspotTsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableHotspot> read(@NotNull String actionableHotspotTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableHotspotTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), ActionableFileUtil.FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("gene")
                .add("chromosome")
                .add("position")
                .add("ref")
                .add("alt")
                .add(ActionableFileUtil.header())
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableHotspot> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableHotspot> actionableHotspots = Lists.newArrayList();
        for (String line : lines) {
            actionableHotspots.add(fromLine(line, fields));
        }
        return actionableHotspots;
    }

    @NotNull
    private static ActionableHotspot fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        return ImmutableActionableHotspot.builder()
                .from(ActionableFileUtil.fromLine(values, fields))
                .gene(values[fields.get("gene")])
                .chromosome(values[fields.get("chromosome")])
                .position(Integer.parseInt(values[fields.get("position")]))
                .ref(values[fields.get("ref")])
                .alt(values[fields.get("alt")])
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<ActionableHotspot> actionableHotspots) {
        List<String> lines = Lists.newArrayList();
        for (ActionableHotspot actionableHotspot : sort(actionableHotspots)) {
            lines.add(toLine(actionableHotspot));
        }
        return lines;
    }

    @NotNull
    private static List<ActionableHotspot> sort(@NotNull Iterable<ActionableHotspot> actionableHotspots) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableHotspot> sorted = Lists.newArrayList(actionableHotspots);
        sorted.sort(new ActionableHotspotComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableHotspot variant) {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(variant.gene())
                .add(variant.chromosome())
                .add(Integer.toString(variant.position()))
                .add(variant.ref())
                .add(variant.alt())
                .add(ActionableFileUtil.toLine(variant))
                .toString();
    }
}

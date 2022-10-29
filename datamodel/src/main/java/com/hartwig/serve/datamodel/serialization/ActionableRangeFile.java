package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ActionableRangeComparator;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.RangeType;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class ActionableRangeFile {

    private static final String ACTIONABLE_RANGE_TSV = "ActionableRanges.tsv";

    private ActionableRangeFile() {
    }

    @NotNull
    public static String actionableRangeTsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_RANGE_TSV);
    }

    public static void write(@NotNull String actionableRangeTsv, @NotNull Iterable<ActionableRange> actionableRanges) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableRanges));

        Files.write(new File(actionableRangeTsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableRange> read(@NotNull String actionableRangeTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableRangeTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), ActionableFileUtil.FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("gene")
                .add("geneRole")
                .add("proteinEffect")
                .add("associatedWithDrugResistance")
                .add("transcript")
                .add("chromosome")
                .add("start")
                .add("end")
                .add("applicableMutationType")
                .add("rangeType")
                .add("rank")
                .add(ActionableFileUtil.header())
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableRange> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableRange> actionableRanges = Lists.newArrayList();
        for (String line : lines) {
            actionableRanges.add(fromLine(line, fields));
        }
        return actionableRanges;
    }

    @NotNull
    private static ActionableRange fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        return ImmutableActionableRange.builder()
                .from(ActionableFileUtil.fromLine(values, fields))
                .gene(values[fields.get("gene")])
                .geneRole(GeneRole.valueOf(values[fields.get("geneRole")]))
                .proteinEffect(ProteinEffect.valueOf(values[fields.get("proteinEffect")]))
                .associatedWithDrugResistance(SerializationUtil.optionalBoolean(values[fields.get("associatedWithDrugResistance")]))
                .transcript(values[fields.get("transcript")])
                .chromosome(values[fields.get("chromosome")])
                .start(Integer.parseInt(values[fields.get("start")]))
                .end(Integer.parseInt(values[fields.get("end")]))
                .applicableMutationType(MutationType.valueOf(values[fields.get("applicableMutationType")]))
                .rangeType(RangeType.valueOf(values[fields.get("rangeType")]))
                .rank(Integer.parseInt(values[fields.get("rank")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<ActionableRange> actionableRanges) {
        List<String> lines = Lists.newArrayList();
        for (ActionableRange actionableRange : sort(actionableRanges)) {
            lines.add(toLine(actionableRange));
        }
        return lines;
    }

    @NotNull
    public static List<ActionableRange> sort(@NotNull Iterable<ActionableRange> actionableRanges) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableRange> sorted = Lists.newArrayList(actionableRanges);
        sorted.sort(new ActionableRangeComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableRange range) {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(range.gene())
                .add(range.geneRole().toString())
                .add(range.proteinEffect().toString())
                .add(SerializationUtil.nullableBoolean(range.associatedWithDrugResistance()))
                .add(range.transcript())
                .add(range.chromosome())
                .add(Long.toString(range.start()))
                .add(Long.toString(range.end()))
                .add(range.applicableMutationType().toString())
                .add(range.rangeType().toString())
                .add(String.valueOf(range.rank()))
                .add(ActionableFileUtil.toLine(range))
                .toString();
    }
}

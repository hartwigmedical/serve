package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.fusion.FusionPairComparator;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusionPair;
import com.hartwig.serve.datamodel.fusion.KnownFusionPair;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class KnownFusionPairFile {

    static final String FIELD_DELIMITER = "\t";
    private static final String KNOWN_FUSION_PAIR_TSV = "KnownFusionPairs.SERVE.tsv";

    private KnownFusionPairFile() {
    }

    @NotNull
    public static String knownFusionPairTsvPath(@NotNull String outputDir, @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(outputDir + File.separator + KNOWN_FUSION_PAIR_TSV);
    }

    public static void write(@NotNull String fusionPairTsv, @NotNull Iterable<KnownFusionPair> fusionPairs) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(fusionPairs));

        Files.write(new File(fusionPairTsv).toPath(), lines);
    }

    @NotNull
    public static List<KnownFusionPair> read(@NotNull String file) throws IOException {
        List<String> lines = Files.readAllLines(new File(file).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("geneUp")
                .add("minExonUp")
                .add("maxExonUp")
                .add("geneDown")
                .add("minExonDown")
                .add("maxExonDown")
                .add("sources")
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<KnownFusionPair> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<KnownFusionPair> fusionPairs = Lists.newArrayList();
        for (String line : lines) {
            fusionPairs.add(fromLine(line, fields));
        }
        return fusionPairs;
    }

    @NotNull
    private static KnownFusionPair fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableKnownFusionPair.builder()
                .geneUp(values[fields.get("geneUp")])
                .minExonUp(SerializationUtil.optionalInteger(values[fields.get("minExonUp")]))
                .maxExonUp(SerializationUtil.optionalInteger(values[fields.get("maxExonUp")]))
                .geneDown(values[fields.get("geneDown")])
                .minExonDown(SerializationUtil.optionalInteger(values[fields.get("minExonDown")]))
                .maxExonDown(SerializationUtil.optionalInteger(values[fields.get("maxExonDown")]))
                .sources(Knowledgebase.fromCommaSeparatedSourceString(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<KnownFusionPair> fusionPairs) {
        List<String> lines = Lists.newArrayList();
        for (KnownFusionPair fusionPair : sort(fusionPairs)) {
            lines.add(toLine(fusionPair));
        }
        return lines;
    }

    @NotNull
    private static List<KnownFusionPair> sort(@NotNull Iterable<KnownFusionPair> fusionPairs) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<KnownFusionPair> sorted = Lists.newArrayList(fusionPairs);
        sorted.sort(new FusionPairComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull KnownFusionPair fusionPair) {
        return new StringJoiner(FIELD_DELIMITER).add(fusionPair.geneUp())
                .add(SerializationUtil.nullableInteger(fusionPair.minExonUp()))
                .add(SerializationUtil.nullableInteger(fusionPair.maxExonUp()))
                .add(fusionPair.geneDown())
                .add(SerializationUtil.nullableInteger(fusionPair.minExonDown()))
                .add(SerializationUtil.nullableInteger(fusionPair.maxExonDown()))
                .add(Knowledgebase.toCommaSeparatedSourceString(fusionPair.sources()))
                .toString();
    }
}

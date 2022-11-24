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
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusionComparator;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class KnownFusionFile {

    static final String FIELD_DELIMITER = "\t";
    private static final String KNOWN_FUSION_TSV = "KnownFusions.SERVE.tsv";

    private KnownFusionFile() {
    }

    @NotNull
    public static String knownFusionTsvPath(@NotNull String outputDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(outputDir + File.separator + KNOWN_FUSION_TSV);
    }

    public static void write(@NotNull String fusionTsv, @NotNull Iterable<KnownFusion> fusions) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(fusions));

        Files.write(new File(fusionTsv).toPath(), lines);
    }

    @NotNull
    public static List<KnownFusion> read(@NotNull String file) throws IOException {
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
                .add("proteinEffect")
                .add("associatedWithDrugResistance")
                .add("sources")
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<KnownFusion> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<KnownFusion> fusions = Lists.newArrayList();
        for (String line : lines) {
            fusions.add(fromLine(line, fields));
        }
        return fusions;
    }

    @NotNull
    private static KnownFusion fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableKnownFusion.builder()
                .geneUp(values[fields.get("geneUp")])
                .minExonUp(SerializationUtil.optionalInteger(values[fields.get("minExonUp")]))
                .maxExonUp(SerializationUtil.optionalInteger(values[fields.get("maxExonUp")]))
                .geneDown(values[fields.get("geneDown")])
                .minExonDown(SerializationUtil.optionalInteger(values[fields.get("minExonDown")]))
                .maxExonDown(SerializationUtil.optionalInteger(values[fields.get("maxExonDown")]))
                .proteinEffect(ProteinEffect.valueOf(values[fields.get("proteinEffect")]))
                .associatedWithDrugResistance(SerializationUtil.optionalBoolean(values[fields.get("associatedWithDrugResistance")]))
                .sources(Knowledgebase.fromCommaSeparatedSourceString(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<KnownFusion> fusions) {
        List<String> lines = Lists.newArrayList();
        for (KnownFusion fusion : sort(fusions)) {
            lines.add(toLine(fusion));
        }
        return lines;
    }

    @NotNull
    private static List<KnownFusion> sort(@NotNull Iterable<KnownFusion> fusions) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<KnownFusion> sorted = Lists.newArrayList(fusions);
        sorted.sort(new KnownFusionComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull KnownFusion fusion) {
        return new StringJoiner(FIELD_DELIMITER).add(fusion.geneUp())
                .add(SerializationUtil.nullableInteger(fusion.minExonUp()))
                .add(SerializationUtil.nullableInteger(fusion.maxExonUp()))
                .add(fusion.geneDown())
                .add(SerializationUtil.nullableInteger(fusion.minExonDown()))
                .add(SerializationUtil.nullableInteger(fusion.maxExonDown()))
                .add(fusion.proteinEffect().toString())
                .add(SerializationUtil.nullableBoolean(fusion.associatedWithDrugResistance()))
                .add(Knowledgebase.toCommaSeparatedSourceString(fusion.sources()))
                .toString();
    }
}

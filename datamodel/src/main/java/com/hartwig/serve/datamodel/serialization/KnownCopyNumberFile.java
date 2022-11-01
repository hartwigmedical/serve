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
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumberComparator;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class KnownCopyNumberFile {

    static final String FIELD_DELIMITER = "\t";
    private static final String KNOWN_COPY_NUMBER_TSV = "KnownCopyNumbers.SERVE.tsv";

    private KnownCopyNumberFile() {
    }

    @NotNull
    public static String knownCopyNumberTsvPath(@NotNull String outputDir, @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(outputDir + File.separator + KNOWN_COPY_NUMBER_TSV);
    }

    public static void write(@NotNull String copyNumberTsv, @NotNull Iterable<KnownCopyNumber> copyNumbers) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(copyNumbers));

        Files.write(new File(copyNumberTsv).toPath(), lines);
    }

    @NotNull
    public static List<KnownCopyNumber> read(@NotNull String file) throws IOException {
        List<String> lines = Files.readAllLines(new File(file).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("gene")
                .add("geneRole")
                .add("proteinEffect")
                .add("associatedWithDrugResistance")
                .add("event")
                .add("sources")
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<KnownCopyNumber> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<KnownCopyNumber> copyNumber = Lists.newArrayList();
        for (String line : lines) {
            copyNumber.add(fromLine(line, fields));
        }
        return copyNumber;
    }

    @NotNull
    private static KnownCopyNumber fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableKnownCopyNumber.builder()
                .gene(values[fields.get("gene")])
                .geneRole(GeneRole.valueOf(values[fields.get("geneRole")]))
                .proteinEffect(ProteinEffect.valueOf(values[fields.get("proteinEffect")]))
                .associatedWithDrugResistance(SerializationUtil.optionalBoolean(values[fields.get("associatedWithDrugResistance")]))
                .event(GeneEvent.valueOf(values[fields.get("event")]))
                .sources(Knowledgebase.fromCommaSeparatedSourceString(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<KnownCopyNumber> copyNumbers) {
        List<String> lines = Lists.newArrayList();
        for (KnownCopyNumber copyNumber : sort(copyNumbers)) {
            lines.add(toLine(copyNumber));
        }
        return lines;
    }

    @NotNull
    private static List<KnownCopyNumber> sort(@NotNull Iterable<KnownCopyNumber> copyNumbers) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<KnownCopyNumber> sorted = Lists.newArrayList(copyNumbers);
        sorted.sort(new KnownCopyNumberComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull KnownCopyNumber copyNumber) {
        return new StringJoiner(FIELD_DELIMITER).add(copyNumber.gene())
                .add(copyNumber.geneRole().toString())
                .add(copyNumber.proteinEffect().toString())
                .add(SerializationUtil.nullableBoolean(copyNumber.associatedWithDrugResistance()))
                .add(copyNumber.event().toString())
                .add(Knowledgebase.toCommaSeparatedSourceString(copyNumber.sources()))
                .toString();
    }
}

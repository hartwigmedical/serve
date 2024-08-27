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
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownCodonComparator;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class KnownCodonFile {

    static final String FIELD_DELIMITER = "\t";
    private static final String KNOWN_CODON_TSV = "KnownCodons.SERVE.tsv";

    private KnownCodonFile() {
    }

    @NotNull
    public static String knownCodonTsvPath(@NotNull String outputDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(outputDir + File.separator + KNOWN_CODON_TSV);
    }

    public static void write(@NotNull String codonTsv, @NotNull Iterable<KnownCodon> codons) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(codons));

        Files.write(new File(codonTsv).toPath(), lines);
    }

    @NotNull
    public static List<KnownCodon> read(@NotNull String file) throws IOException {
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
                .add("chromosome")
                .add("start")
                .add("end")
                .add("applicableMutationType")
                .add("inputTranscript")
                .add("inputCodonRank")
                .add("sources")
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<KnownCodon> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<KnownCodon> codons = Lists.newArrayList();
        for (String line : lines) {
            codons.add(fromLine(line, fields));
        }
        return codons;
    }

    @NotNull
    private static KnownCodon fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER, -1);

        return ImmutableKnownCodon.builder()
                .gene(values[fields.get("gene")])
                .geneRole(GeneRole.valueOf(values[fields.get("geneRole")]))
                .proteinEffect(ProteinEffect.valueOf(values[fields.get("proteinEffect")]))
                .associatedWithDrugResistance(SerializationUtil.optionalBoolean(values[fields.get("associatedWithDrugResistance")]))
                .chromosome(values[fields.get("chromosome")])
                .start(Integer.parseInt(values[fields.get("start")]))
                .end(Integer.parseInt(values[fields.get("end")]))
                .applicableMutationType(MutationType.valueOf(values[fields.get("applicableMutationType")]))
                .inputTranscript(values[fields.get("inputTranscript")])
                .inputCodonRank(Integer.parseInt(values[fields.get("inputCodonRank")]))
                .sources(Knowledgebase.fromCommaSeparatedSourceString(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<KnownCodon> codons) {
        List<String> lines = Lists.newArrayList();
        for (KnownCodon codon : sort(codons)) {
            lines.add(toLine(codon));
        }
        return lines;
    }

    @NotNull
    private static List<KnownCodon> sort(@NotNull Iterable<KnownCodon> codons) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<KnownCodon> sorted = Lists.newArrayList(codons);
        sorted.sort(new KnownCodonComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull KnownCodon codon) {
        return new StringJoiner(FIELD_DELIMITER).add(codon.gene())
                .add(codon.geneRole().toString())
                .add(codon.proteinEffect().toString())
                .add(SerializationUtil.nullableBoolean(codon.associatedWithDrugResistance()))
                .add(codon.chromosome())
                .add(String.valueOf(codon.start()))
                .add(String.valueOf(codon.end()))
                .add(codon.applicableMutationType().toString())
                .add(codon.inputTranscript())
                .add(String.valueOf(codon.inputCodonRank()))
                .add(Knowledgebase.toCommaSeparatedSourceString(codon.sources()))
                .toString();
    }
}

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
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.range.ImmutableExonAnnotation;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.KnownExonComparator;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class KnownExonFile {

    static final String FIELD_DELIMITER = "\t";
    private static final String KNOWN_EXON_TSV = "KnownExons.SERVE.tsv";

    private KnownExonFile() {
    }

    @NotNull
    public static String knownExonTsvPath(@NotNull String outputDir, @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(outputDir + File.separator + KNOWN_EXON_TSV);
    }

    public static void write(@NotNull String exonTsv, @NotNull Iterable<KnownExon> exons) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(exons));

        Files.write(new File(exonTsv).toPath(), lines);
    }

    @NotNull
    public static List<KnownExon> read(@NotNull String file) throws IOException {
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
                .add("transcript")
                .add("chromosome")
                .add("start")
                .add("end")
                .add("applicableMutationType")
                .add("exonRank")
                .add("sources")
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<KnownExon> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<KnownExon> exons = Lists.newArrayList();
        for (String line : lines) {
            exons.add(fromLine(line, fields));
        }
        return exons;
    }

    @NotNull
    private static KnownExon fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableKnownExon.builder()
                .annotation(ImmutableExonAnnotation.builder()
                        .gene(values[fields.get("gene")])
                        .geneRole(GeneRole.valueOf(values[fields.get("geneRole")]))
                        .proteinEffect(ProteinEffect.valueOf(values[fields.get("proteinEffect")]))
                        .transcript(values[fields.get("transcript")])
                        .chromosome(values[fields.get("chromosome")])
                        .start(Integer.parseInt(values[fields.get("start")]))
                        .end(Integer.parseInt(values[fields.get("end")]))
                        .applicableMutationType(MutationType.valueOf(values[fields.get("applicableMutationType")]))
                        .rank(Integer.parseInt(values[fields.get("exonRank")]))
                        .build())
                .sources(Knowledgebase.fromCommaSeparatedSourceString(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<KnownExon> exons) {
        List<String> lines = Lists.newArrayList();
        for (KnownExon exon : sort(exons)) {
            lines.add(toLine(exon));
        }
        return lines;
    }

    @NotNull
    private static List<KnownExon> sort(@NotNull Iterable<KnownExon> codons) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<KnownExon> sorted = Lists.newArrayList(codons);
        sorted.sort(new KnownExonComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull KnownExon exon) {
        return new StringJoiner(FIELD_DELIMITER).add(exon.annotation().gene())
                .add(exon.annotation().geneRole().toString())
                .add(exon.annotation().proteinEffect().toString())
                .add(exon.annotation().transcript())
                .add(exon.annotation().chromosome())
                .add(String.valueOf(exon.annotation().start()))
                .add(String.valueOf(exon.annotation().end()))
                .add(exon.annotation().applicableMutationType().toString())
                .add(String.valueOf(exon.annotation().rank()))
                .add(Knowledgebase.toCommaSeparatedSourceString(exon.sources()))
                .toString();
    }
}

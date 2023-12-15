package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.gene.KnownGeneComparator;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class KnownGeneFile {

    static final String FIELD_DELIMITER = "\t";
    private static final String KNOWN_GENE_TSV = "KnownGenes.SERVE.tsv";

    private KnownGeneFile() {
    }

    @NotNull
    public static String knownGeneTsvPath(@NotNull String outputDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(outputDir + File.separator + KNOWN_GENE_TSV);
    }

    public static void write(@NotNull String genesTsv, @NotNull Iterable<KnownGene> genes) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(genes));

        Files.write(new File(genesTsv).toPath(), lines);
    }

    @NotNull
    public static List<KnownGene> read(@NotNull String file) throws IOException {
        List<String> lines = Files.readAllLines(new File(file).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("gene").add("geneRole").add("sources").toString();
    }

    @NotNull
    @VisibleForTesting
    static List<KnownGene> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        return lines.stream().map(line -> fromLine(line, fields)).collect(Collectors.toList());
    }

    @NotNull
    private static KnownGene fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableKnownGene.builder()
                .gene(values[fields.get("gene")])
                .geneRole(GeneRole.valueOf(values[fields.get("geneRole")]))
                .sources(Knowledgebase.fromCommaSeparatedSourceString(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<KnownGene> genes) {
        return StreamSupport.stream(genes.spliterator(), false)
                .sorted(new KnownGeneComparator())
                .map(KnownGeneFile::toLine)
                .collect(Collectors.toList());
    }

    @NotNull
    private static String toLine(@NotNull KnownGene gene) {
        return new StringJoiner(FIELD_DELIMITER).add(gene.gene())
                .add(gene.geneRole().toString())
                .add(Knowledgebase.toCommaSeparatedSourceString(gene.sources()))
                .toString();
    }
}

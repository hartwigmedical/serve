package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.ActionableGeneComparator;
import com.hartwig.serve.datamodel.gene.GeneLevelEvent;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class ActionableGeneFile {

    private static final String ACTIONABLE_GENE_TSV = "ActionableGenes.tsv";

    private ActionableGeneFile() {
    }

    @NotNull
    public static String actionableGeneTsvPath(@NotNull String serveActionabilityDir, @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(serveActionabilityDir + File.separator + ACTIONABLE_GENE_TSV);
    }

    public static void write(@NotNull String actionableGeneTsv, @NotNull Iterable<ActionableGene> actionableGenes) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(actionableGenes));

        Files.write(new File(actionableGeneTsv).toPath(), lines);
    }

    @NotNull
    public static List<ActionableGene> read(@NotNull String actionableGeneTsv) throws IOException {
        List<String> lines = Files.readAllLines(new File(actionableGeneTsv).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), ActionableFileUtil.FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add("gene")
                .add("geneRole")
                .add("proteinEffect")
                .add("event")
                .add(ActionableFileUtil.header())
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<ActionableGene> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<ActionableGene> actionableGenes = Lists.newArrayList();
        for (String line : lines) {
            actionableGenes.add(fromLine(line, fields));
        }
        return actionableGenes;
    }

    @NotNull
    private static ActionableGene fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(ActionableFileUtil.FIELD_DELIMITER);

        return ImmutableActionableGene.builder()
                .from(ActionableFileUtil.fromLine(values, fields))
                .gene(values[fields.get("gene")])
                .geneRole(GeneRole.valueOf(values[fields.get("geneRole")]))
                .proteinEffect(ProteinEffect.valueOf(values[fields.get("proteinEffect")]))
                .event(GeneLevelEvent.valueOf(values[fields.get("event")]))
                .build();
    }

    @NotNull
    @VisibleForTesting
    static List<String> toLines(@NotNull Iterable<ActionableGene> actionableGenes) {
        List<String> lines = Lists.newArrayList();
        for (ActionableGene actionableGene : sort(actionableGenes)) {
            lines.add(toLine(actionableGene));
        }
        return lines;
    }

    @NotNull
    private static List<ActionableGene> sort(@NotNull Iterable<ActionableGene> actionableGenes) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<ActionableGene> sorted = Lists.newArrayList(actionableGenes);
        sorted.sort(new ActionableGeneComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull ActionableGene gene) {
        return new StringJoiner(ActionableFileUtil.FIELD_DELIMITER).add(gene.gene())
                .add(gene.geneRole().toString())
                .add(gene.proteinEffect().toString())
                .add(gene.event().toString())
                .add(ActionableFileUtil.toLine(gene))
                .toString();
    }
}
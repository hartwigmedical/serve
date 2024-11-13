package com.hartwig.serve.sources.hartwig.gene;

import static java.util.stream.Collectors.toList;

import static com.hartwig.serve.datamodel.serialization.util.SerializationUtil.createFields;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.hartwig.serve.datamodel.common.GeneRole;

import org.jetbrains.annotations.NotNull;

public final class HartwigGeneFileReader {

    private static final String DELIMITER = "\t";

    private static final String DRIVER_GENES_GENE_ROLE_FIELD = "likelihoodType";
    private static final String CURATED_GENES_GENE_ROLE_FIELD = "geneRole";

    private HartwigGeneFileReader() {
    }

    @NotNull
    public static List<HartwigGeneEntry> readDriverGenes(@NotNull String fileName) throws IOException {
        return HartwigGeneFileReader.read(fileName, DRIVER_GENES_GENE_ROLE_FIELD);
    }

    @NotNull
    public static List<HartwigGeneEntry> readCuratedGenes(@NotNull String fileName) throws IOException {
        return HartwigGeneFileReader.read(fileName, CURATED_GENES_GENE_ROLE_FIELD);
    }

    @NotNull
    private static List<HartwigGeneEntry> read(@NotNull String fileName, @NotNull String geneRoleFieldName) throws IOException {
        return fromLines(Files.readAllLines(new File(fileName).toPath()), geneRoleFieldName);
    }

    @NotNull
    private static List<HartwigGeneEntry> fromLines(@NotNull List<String> lines, @NotNull String geneRoleFieldName) {
        Map<String, Integer> fields = createFields(lines.get(0), DELIMITER);
        int geneFieldIndex = fields.get("gene");
        int geneRoleFieldIndex = fields.get(geneRoleFieldName);
        return lines.stream().skip(1).map(line -> fromLine(line, geneFieldIndex, geneRoleFieldIndex)).collect(toList());
    }

    @NotNull
    private static HartwigGeneEntry fromLine(@NotNull String line, int geneFieldIndex, int geneRoleFieldIndex) {
        String[] values = line.split(DELIMITER);

        return ImmutableHartwigGeneEntry.builder()
                .gene(values[geneFieldIndex])
                .geneRole(GeneRole.valueOf(values[geneRoleFieldIndex].toUpperCase()))
                .build();
    }
}

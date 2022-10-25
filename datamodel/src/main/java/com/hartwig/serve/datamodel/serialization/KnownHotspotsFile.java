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
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspotComparator;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;

public final class KnownHotspotsFile {

    static final String FIELD_DELIMITER = "\t";
    private static final String KNOWN_HOTSPOT_TSV = "KnownHotspots.SERVE.tsv";

    private KnownHotspotsFile() {
    }

    @NotNull
    public static String knownHotspotTsvPath(@NotNull String outputDir, @NotNull RefGenomeVersion refGenomeVersion) {
        return refGenomeVersion.addVersionToFilePath(outputDir + File.separator + KNOWN_HOTSPOT_TSV);
    }

    public static void write(@NotNull String hotspotTsv, @NotNull Iterable<KnownHotspot> hotspots) throws IOException {
        List<String> lines = Lists.newArrayList();
        lines.add(header());
        lines.addAll(toLines(hotspots));

        Files.write(new File(hotspotTsv).toPath(), lines);
    }

    @NotNull
    public static List<KnownHotspot> read(@NotNull String file) throws IOException {
        List<String> lines = Files.readAllLines(new File(file).toPath());
        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        return fromLines(lines.subList(1, lines.size()), fields);
    }

    @NotNull
    @VisibleForTesting
    static String header() {
        return new StringJoiner(FIELD_DELIMITER).add("chromosome")
                .add("position")
                .add("ref")
                .add("alt")
                .add("gene")
                .add("geneRole")
                .add("proteinEffect")
                .add("transcript")
                .add("proteinAnnotation")
                .add("sources")
                .toString();
    }

    @NotNull
    @VisibleForTesting
    static List<KnownHotspot> fromLines(@NotNull List<String> lines, @NotNull Map<String, Integer> fields) {
        List<KnownHotspot> fusionPairs = Lists.newArrayList();
        for (String line : lines) {
            fusionPairs.add(fromLine(line, fields));
        }
        return fusionPairs;
    }

    @NotNull
    private static KnownHotspot fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        return ImmutableKnownHotspot.builder()
                .chromosome(values[fields.get("chromosome")])
                .position(Integer.parseInt(values[fields.get("position")]))
                .ref(values[fields.get("ref")])
                .alt(values[fields.get("alt")])
                .gene(values[fields.get("gene")])
                .geneRole(GeneRole.valueOf(values[fields.get("geneRole")]))
                .proteinEffect(ProteinEffect.valueOf(values[fields.get("proteinEffect")]))
                .transcript(SerializationUtil.optionalString(values[fields.get("transcript")]))
                .proteinAnnotation(values[fields.get("proteinAnnotation")])
                .sources(Knowledgebase.fromCommaSeparatedSourceString(values[fields.get("sources")]))
                .build();
    }

    @NotNull
    private static List<String> toLines(@NotNull Iterable<KnownHotspot> hotspots) {
        List<String> lines = Lists.newArrayList();
        for (KnownHotspot hotspot : sort(hotspots)) {
            lines.add(toLine(hotspot));
        }
        return lines;
    }

    @NotNull
    private static List<KnownHotspot> sort(@NotNull Iterable<KnownHotspot> hotspots) {
        // Need to make a copy since the input may be immutable and cannot be sorted!
        List<KnownHotspot> sorted = Lists.newArrayList(hotspots);
        sorted.sort(new VariantHotspotComparator());

        return sorted;
    }

    @NotNull
    private static String toLine(@NotNull KnownHotspot hotspot) {
        return new StringJoiner(FIELD_DELIMITER).add(hotspot.chromosome())
                .add(String.valueOf(hotspot.position()))
                .add(hotspot.ref())
                .add(hotspot.alt())
                .add(hotspot.gene())
                .add(hotspot.geneRole().toString())
                .add(hotspot.proteinEffect().toString())
                .add(SerializationUtil.nullableString(hotspot.transcript()))
                .add(hotspot.proteinAnnotation())
                .add(Knowledgebase.toCommaSeparatedSourceString(hotspot.sources()))
                .toString();
    }
}

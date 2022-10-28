package com.hartwig.serve.common.drivergene;

import static com.hartwig.serve.datamodel.serialization.util.SerializationUtil.createFields;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class DriverGeneFile {

    private static final String DELIMITER = "\t";
    private static final String OTHER_TRANS_DELIM = ";";

    private DriverGeneFile() {
    }

    @NotNull
    public static List<DriverGene> read(@NotNull String filename) throws IOException {
        return fromLines(Files.readAllLines(new File(filename).toPath()));
    }

    @NotNull
    public static List<DriverGene> fromLines(@NotNull List<String> lines) {
        List<DriverGene> driverGenes = Lists.newArrayList();

        String header = lines.get(0);
        Map<String, Integer> fields = createFields(header, DELIMITER);
        lines.remove(0);

        int geneIndex = fields.get("gene");
        int missenseIndex = fields.get("reportMissense");
        int nonsenseIndex = fields.get("reportNonsense");
        int spliceIndex = fields.get("reportSplice");
        int deletionIndex = fields.get("reportDeletion");
        int disruptionIndex = fields.get("reportDisruption");
        int amplificationIndex = fields.get("reportAmplification");

        int somaticHotspotIndex = fields.containsKey("reportSomaticHotspot")
                ? fields.get("reportSomaticHotspot")
                : fields.get("reportHotspot"); // for older files

        int likelihoodTypeIndex = fields.get("likelihoodType");
        int germlineVariantIndex = fields.get("reportGermlineVariant");
        int germlineHotspotIndex = fields.get("reportGermlineHotspot");
        int germlineDisruptionIndex = fields.get("reportGermlineDisruption");
        int altTransIndex = fields.get("additionalReportedTranscripts");
        Integer reportPGXIndex = fields.get("reportPGX");

        ImmutableDriverGene.Builder builder = ImmutableDriverGene.builder();

        for (String line : lines) {
            String[] values = line.split(DELIMITER, -1);

            List<String> otherReportableTrans =
                    Arrays.stream(values[altTransIndex].split(OTHER_TRANS_DELIM)).filter(x -> !x.isEmpty()).collect(Collectors.toList());

            builder.gene(values[geneIndex])
                    .reportMissenseAndInframe(Boolean.parseBoolean(values[missenseIndex]))
                    .reportNonsenseAndFrameshift(Boolean.parseBoolean(values[nonsenseIndex]))
                    .reportSplice(Boolean.parseBoolean(values[spliceIndex]))
                    .reportDeletion(Boolean.parseBoolean(values[deletionIndex]))
                    .reportDisruption(Boolean.parseBoolean(values[disruptionIndex]))
                    .reportAmplification(Boolean.parseBoolean(values[amplificationIndex]))
                    .reportSomaticHotspot(Boolean.parseBoolean(values[somaticHotspotIndex]))
                    .likelihoodType(DriverCategory.valueOf(values[likelihoodTypeIndex]))
                    .reportGermlineVariant(GermlineReportingMode.valueOf(values[germlineVariantIndex].toUpperCase()))
                    .reportGermlineHotspot(GermlineReportingMode.valueOf(values[germlineHotspotIndex].toUpperCase()))
                    .reportGermlineDisruption(Boolean.parseBoolean(values[germlineDisruptionIndex]))
                    .additionalReportedTranscripts(otherReportableTrans)
                    .reportPGX(reportPGXIndex != null && Boolean.parseBoolean(values[reportPGXIndex]));

            driverGenes.add(builder.build());
        }

        return driverGenes;
    }
}

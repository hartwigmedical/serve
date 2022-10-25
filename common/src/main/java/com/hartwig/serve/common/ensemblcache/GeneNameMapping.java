package com.hartwig.serve.common.ensemblcache;

import static com.hartwig.serve.datamodel.serialization.FileReaderUtils.createFields;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// a resource to map gene names between GRCh37 and HGNC + GRCh38
public class GeneNameMapping {

    private final Map<String, GeneMappingData> geneNameNewToOldMap;
    private final Set<String> unchangedGenes;

    private static final String DELIMITER = ",";

    public GeneNameMapping() {
        geneNameNewToOldMap = Maps.newHashMap();
        unchangedGenes = Sets.newHashSet();

        InputStream inputStream = GeneNameMapping.class.getResourceAsStream("/ensembl/gene_name_mapping.csv");
        List<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());

        Map<String, Integer> fields = createFields(lines.get(0), DELIMITER);
        lines.remove(0);

        int geneIdIndex = fields.get("GeneId");
        int geneOldIndex = fields.get("GeneNameOld");
        int geneNewIndex = fields.get("GeneNameNew");

        for (String line : lines) {
            String[] values = line.split(DELIMITER);
            String geneId = values[geneIdIndex];
            String geneNameOld = values[geneOldIndex];
            String geneNameNew = values[geneNewIndex];

            if (geneNameOld.equals(geneNameNew)) {
                unchangedGenes.add(geneNameOld);
            } else {
                GeneMappingData data = new GeneMappingData(geneId, geneNameNew, geneNameOld);
                geneNameNewToOldMap.put(geneNameNew, data);
            }
        }
    }

    public boolean hasNewGene(@NotNull String geneNameNew) {
        return unchangedGenes.contains(geneNameNew) || geneNameNewToOldMap.containsKey(geneNameNew);
    }

    @Nullable
    public String getOldName(@NotNull String geneNameNew) {
        if (unchangedGenes.contains(geneNameNew)) {
            return geneNameNew;
        }

        GeneMappingData data = geneNameNewToOldMap.get(geneNameNew);
        return data != null ? data.GeneNameOld : null;
    }
}

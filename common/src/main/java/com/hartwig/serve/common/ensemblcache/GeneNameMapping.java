package com.hartwig.serve.common.ensemblcache;

import static com.hartwig.serve.datamodel.serialization.util.SerializationUtil.createFields;

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

public class GeneNameMapping {

    private static final String DELIMITER = ",";

    @NotNull
    private final Map<String, GeneMappingData> geneNameNewToOldMap;
    @NotNull
    private final Set<String> unchangedGenes;

    @NotNull
    public static GeneNameMapping loadFromResource() {
        Map<String, GeneMappingData> geneNameNewToOldMap = Maps.newHashMap();
        Set<String> unchangedGenes = Sets.newHashSet();

        InputStream inputStream = GeneNameMapping.class.getResourceAsStream("/ensembl/gene_name_mapping.csv");
        List<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());

        Map<String, Integer> fields = createFields(lines.get(0), DELIMITER);
        lines.remove(0);

        int geneIdIndex = fields.get("geneId");
        int geneOldIndex = fields.get("geneNameOld");
        int geneNewIndex = fields.get("geneNameNew");

        for (String line : lines) {
            String[] values = line.split(DELIMITER);
            String geneId = values[geneIdIndex];
            String geneNameOld = values[geneOldIndex];
            String geneNameNew = values[geneNewIndex];

            if (geneNameOld.equals(geneNameNew)) {
                unchangedGenes.add(geneNameOld);
            } else {
                GeneMappingData data =
                        ImmutableGeneMappingData.builder().geneId(geneId).geneNameNew(geneNameNew).geneNameOld(geneNameOld).build();
                geneNameNewToOldMap.put(geneNameNew, data);
            }
        }

        return new GeneNameMapping(geneNameNewToOldMap, unchangedGenes);
    }

    private GeneNameMapping(@NotNull final Map<String, GeneMappingData> geneNameNewToOldMap, @NotNull final Set<String> unchangedGenes) {
        this.geneNameNewToOldMap = geneNameNewToOldMap;
        this.unchangedGenes = unchangedGenes;
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
        return data != null ? data.geneNameOld() : null;
    }
}

package com.hartwig.serve.common.ensemblcache;

import static com.hartwig.serve.common.FileReaderUtils.createFields;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

// a resource to map gene names between GRCh37 and HGNC + GRCh38
public class GeneNameMapping {

    private final Map<String, GeneMappingData> mGeneNameOldToNewMap;
    private final Map<String, GeneMappingData> mGeneNameNewToOldMap;
    private final Set<String> mUnchangedGenes;
    private final Set<String> mUnmappedGenes;

    private static final String DELIM = ",";
    private static final String UNMAPPED = "NA";

    public GeneNameMapping() {
        mGeneNameOldToNewMap = Maps.newHashMap();
        mGeneNameNewToOldMap = Maps.newHashMap();
        mUnchangedGenes = Sets.newHashSet();
        mUnmappedGenes = Sets.newHashSet();

        final InputStream inputStream = GeneNameMapping.class.getResourceAsStream("/ensembl/gene_name_mapping.csv");
        List<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());

        Map<String, Integer> fieldsNameIndex = createFields(lines.get(0), DELIM);
        lines.remove(0);

        int geneIdIndex = fieldsNameIndex.get("GeneId");
        int geneOldIndex = fieldsNameIndex.get("GeneNameOld");
        int geneNewIndex = fieldsNameIndex.get("GeneNameNew");

        for (String line : lines) {
            String[] values = line.split(DELIM);
            String geneId = values[geneIdIndex];
            String geneNameOld = values[geneOldIndex];
            String geneNameNew = values[geneNewIndex];

            if (geneNameNew.isEmpty() || geneNameNew.equals(UNMAPPED)) {
                mUnmappedGenes.add(geneNameOld);
            } else if (geneNameOld.equals(geneNameNew)) {
                mUnchangedGenes.add(geneNameOld);
            } else {
                GeneMappingData data = new GeneMappingData(geneId, geneNameNew, geneNameOld);
                mGeneNameOldToNewMap.put(geneNameOld, data);
                mGeneNameNewToOldMap.put(geneNameNew, data);
            }
        }
    }

    public boolean hasNewGene(final String geneNameNew) {
        return mUnchangedGenes.contains(geneNameNew) || mGeneNameNewToOldMap.containsKey(geneNameNew);
    }

    public String getOldName(final String geneNameNew) {
        if (mUnchangedGenes.contains(geneNameNew)) {
            return geneNameNew;
        }

        GeneMappingData data = mGeneNameNewToOldMap.get(geneNameNew);
        return data != null ? data.GeneNameOld : null;
    }
}

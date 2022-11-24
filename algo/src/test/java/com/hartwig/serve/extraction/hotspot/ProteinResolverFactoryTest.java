package com.hartwig.serve.extraction.hotspot;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import com.google.common.io.Resources;
import com.hartwig.serve.EnsemblDataCacheTestFactory;
import com.hartwig.serve.datamodel.RefGenome;

import org.junit.Test;

public class ProteinResolverFactoryTest {

    private static final String REF_GENOME_FASTA_FILE = Resources.getResource("refgenome/v37/ref.fasta").getPath();

    @Test
    public void canCreateTransvarResolver() throws FileNotFoundException {
        assertNotNull(ProteinResolverFactory.transvarWithRefGenome(RefGenome.V37,
                REF_GENOME_FASTA_FILE,
                EnsemblDataCacheTestFactory.create37()));
    }
}
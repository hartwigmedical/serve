package com.hartwig.serve.refgenome;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.hartwig.serve.DriverGenesTestFactory;
import com.hartwig.serve.EnsemblDataCacheTestFactory;
import com.hartwig.serve.KnownFusionCacheTestFactory;
import com.hartwig.serve.extraction.variant.ImmutableVariant;
import com.hartwig.serve.extraction.variant.ProteinResolver;
import com.hartwig.serve.extraction.variant.Variant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public final class RefGenomeResourceTestFactory {

    private static final String REF_GENOME_37_FASTA_FILE = Resources.getResource("refgenome/v37/ref.fasta").getPath();
    private static final String REF_GENOME_38_FASTA_FILE = Resources.getResource("refgenome/v38/ref.fasta").getPath();

    private RefGenomeResourceTestFactory() {
    }

    @NotNull
    public static RefGenomeResource buildTestResource37() {
        return ImmutableRefGenomeResource.builder()
                .refSequence(loadTestRefSequence37())
                .driverGenes(DriverGenesTestFactory.createDriverGenes("BRAF", "KIT"))
                .knownFusionCache(KnownFusionCacheTestFactory.create37())
                .ensemblDataCache(EnsemblDataCacheTestFactory.create37())
                .proteinResolver(new TestProteinResolver())
                .build();
    }

    @NotNull
    public static IndexedFastaSequenceFile loadTestRefSequence37() {
        return loadRefFromFastaFile(REF_GENOME_37_FASTA_FILE);
    }

    @NotNull
    public static IndexedFastaSequenceFile loadTestRefSequence38() {
        return loadRefFromFastaFile(REF_GENOME_38_FASTA_FILE);
    }

    @NotNull
    private static IndexedFastaSequenceFile loadRefFromFastaFile(@NotNull String fastaFile) {
        IndexedFastaSequenceFile refSequence;
        try {
            refSequence = new IndexedFastaSequenceFile(new File(fastaFile));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Could not create ref sequence from " + fastaFile);
        }
        return refSequence;
    }

    private static class TestProteinResolver implements ProteinResolver {

        @NotNull
        @Override
        public List<Variant> resolve(@NotNull final String gene, @Nullable final String specificTranscript,
                @NotNull final String proteinAnnotation) {
            return Lists.newArrayList(ImmutableVariant.builder().chromosome("1").position(10).ref("A").alt("T").build());
        }

        @NotNull
        @Override
        public Set<String> unresolvedProteinAnnotations() {
            return Sets.newHashSet();
        }
    }
}

package com.hartwig.serve.common.ensemblcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.RefGenome;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EnsemblDataLoaderTest {

    private static final String ENSEMBL_DATA_DIR = Resources.getResource("ensembl_data_cache").getPath();

    @Test
    public void canLoadEnsemblDataCache() throws IOException {
        EnsemblDataCache cache = EnsemblDataLoader.load(ENSEMBL_DATA_DIR, RefGenome.V37);

        assertEnsemblGenes(cache.genesPerChromosome());
        assertEnsemblTranscripts(cache.transcriptsPerGeneId());
    }

    private static void assertEnsemblGenes(@NotNull Map<String, List<GeneData>> genesPerChromosome) {
        assertEquals(3, genesPerChromosome.size());
        for (List<GeneData> genes : genesPerChromosome.values()) {
            assertEquals(1, genes.size());
        }

        GeneData kit = genesPerChromosome.get("4").get(0);
        assertEquals("ENSG00000157404", kit.geneId());
        assertEquals("KIT", kit.geneName());
        assertEquals(1, kit.strand());
        assertEquals(55524085, kit.geneStart());
        assertEquals(55606881, kit.geneEnd());
        assertEquals("q12", kit.karyotypeBand());

        GeneData braf = genesPerChromosome.get("7").get(0);
        assertEquals("ENSG00000157764", braf.geneId());
        assertEquals("BRAF", braf.geneName());
        assertEquals(-1, braf.strand());
        assertEquals(140419127, braf.geneStart());
        assertEquals(140624564, braf.geneEnd());
        assertEquals("q34", braf.karyotypeBand());

        GeneData ntrk3 = genesPerChromosome.get("15").get(0);
        assertEquals("ENSG00000140538", ntrk3.geneId());
        assertEquals("NTRK3", ntrk3.geneName());
        assertEquals(-1, ntrk3.strand());
        assertEquals(88418230, ntrk3.geneStart());
        assertEquals(88799999, ntrk3.geneEnd());
        assertEquals("q25.3", ntrk3.karyotypeBand());
    }

    private static void assertEnsemblTranscripts(@NotNull Map<String, List<TranscriptData>> transcriptsPerGeneId) {
        assertEquals(3, transcriptsPerGeneId.size());

        List<TranscriptData> transcriptsBRAF = transcriptsPerGeneId.get("ENSG00000157764");
        assertEquals(5, transcriptsBRAF.size());

        TranscriptData transcriptBRAF = findByTranscriptId(transcriptsBRAF, 2318863);
        assertEquals("ENST00000496384", transcriptBRAF.transcriptName());
        assertEquals("ENSG00000157764", transcriptBRAF.geneId());
        assertFalse(transcriptBRAF.isCanonical());
        assertEquals(-1, transcriptBRAF.strand());
        assertEquals(140419127, transcriptBRAF.transcriptStart());
        assertEquals(140482957, transcriptBRAF.transcriptEnd());
        assertEquals(140426294, (int) transcriptBRAF.codingStart());
        assertEquals(140482957, (int) transcriptBRAF.codingEnd());
        assertEquals("protein_coding", transcriptBRAF.bioType());
        assertEquals(10, transcriptBRAF.exons().size());

        ExonData exonBRAF = findByExonRank(transcriptBRAF.exons(), 10);
        assertEquals(140419127, exonBRAF.start());
        assertEquals(140426316, exonBRAF.end());
        assertEquals(1, exonBRAF.phaseStart());
        assertEquals(-1, exonBRAF.phaseEnd());

        List<TranscriptData> transcriptsKIT = transcriptsPerGeneId.get("ENSG00000157404");
        assertEquals(4, transcriptsKIT.size());

        TranscriptData transcriptKIT = findByTranscriptId(transcriptsKIT, 2432287);
        assertEquals("ENST00000288135", transcriptKIT.transcriptName());
        assertEquals("ENSG00000157404", transcriptKIT.geneId());
        assertTrue(transcriptKIT.isCanonical());
        assertEquals(1, transcriptKIT.strand());
        assertEquals(55524085, transcriptKIT.transcriptStart());
        assertEquals(55606881, transcriptKIT.transcriptEnd());
        assertNull(transcriptKIT.codingStart());
        assertNull(transcriptKIT.codingEnd());
        assertEquals("protein_coding", transcriptKIT.bioType());
        assertEquals(21, transcriptKIT.exons().size());

        ExonData exonKIT = findByExonRank(transcriptKIT.exons(), 1);
        assertEquals(55524085, exonKIT.start());
        assertEquals(55524248, exonKIT.end());
        assertEquals(-1, exonKIT.phaseStart());
        assertEquals(1, exonKIT.phaseEnd());

        List<TranscriptData> transcriptsNTRK3 = transcriptsPerGeneId.get("ENSG00000140538");
        assertEquals(20, transcriptsNTRK3.size());

        TranscriptData transcriptNTRK3 = findByTranscriptId(transcriptsNTRK3, 2454058);
        assertEquals("ENST00000317501", transcriptNTRK3.transcriptName());
        assertEquals("ENSG00000140538", transcriptNTRK3.geneId());
        assertFalse(transcriptNTRK3.isCanonical());
        assertEquals(-1, transcriptNTRK3.strand());
        assertEquals(88520598, transcriptNTRK3.transcriptStart());
        assertEquals(88799661, transcriptNTRK3.transcriptEnd());
        assertEquals(88522576, (int) transcriptNTRK3.codingStart());
        assertEquals(88799384, (int) transcriptNTRK3.codingEnd());
        assertEquals("protein_coding", transcriptNTRK3.bioType());
        assertEquals(15, transcriptNTRK3.exons().size());

        ExonData exonNTRK3 = findByExonRank(transcriptNTRK3.exons(), 1);
        assertEquals(88799515, exonNTRK3.start());
        assertEquals(88799661, exonNTRK3.end());
        assertEquals(-1, exonNTRK3.phaseStart());
        assertEquals(-1, exonNTRK3.phaseEnd());
    }

    @NotNull
    private static TranscriptData findByTranscriptId(@NotNull List<TranscriptData> transcripts, int transcriptIdToFind) {
        for (TranscriptData transcript : transcripts) {
            if (transcript.transcriptId() == transcriptIdToFind){
                return transcript;
            }
        }

        throw new IllegalStateException("Could not find transcript with ID: " + transcriptIdToFind);
    }

    @NotNull
    private static ExonData findByExonRank(@NotNull List<ExonData> exons, int exonRankToFind) {
        for (ExonData exon : exons) {
            if (exon.rank() == exonRankToFind) {
                return exon;
            }
        }

        throw new IllegalStateException("Could not find exon with rank " + exonRankToFind);
    }
}
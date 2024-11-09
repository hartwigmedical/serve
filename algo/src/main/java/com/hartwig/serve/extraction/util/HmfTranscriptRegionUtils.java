package com.hartwig.serve.extraction.util;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.ensemblcache.ExonData;
import com.hartwig.serve.common.ensemblcache.GeneData;
import com.hartwig.serve.common.ensemblcache.Strand;
import com.hartwig.serve.common.ensemblcache.TranscriptData;
import com.hartwig.serve.datamodel.common.GenomeRegion;
import com.hartwig.serve.datamodel.common.GenomeRegionComparator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HmfTranscriptRegionUtils {

    private HmfTranscriptRegionUtils() {
    }

    @NotNull
    public static HmfTranscriptRegion fromEnsemblData(@NotNull GeneData gene, @NotNull TranscriptData transcript) {
        List<HmfExonRegion> exons = Lists.newArrayList();

        for (ExonData exon : transcript.exons()) {
            exons.add(ImmutableHmfExonRegion.builder()
                    .chromosome(gene.chromosome())
                    .exonRank(exon.rank())
                    .start(exon.start())
                    .end(exon.end())
                    .build());
        }

        return ImmutableHmfTranscriptRegion.builder()
                .geneId(gene.geneId())
                .geneName(gene.geneName())
                .chromosome(gene.chromosome())
                .strand(Strand.resolve(gene.strand()))
                .geneStart(gene.geneStart())
                .geneEnd(gene.geneEnd())
                .entrezId(Lists.newArrayList())
                .chromosomeBand(gene.karyotypeBand())
                .transcriptId(transcript.transcriptName())
                .isCanonical(transcript.isCanonical())
                .start(transcript.transcriptStart())
                .end(transcript.transcriptEnd())
                .codingStart(transcript.codingStart())
                .codingEnd(transcript.codingEnd())
                .exons(exons)
                .build();
    }

    @Nullable
    public static List<GenomeRegion> codonRangeByRank(final HmfTranscriptRegion transcript, int startCodon, int endCodon) {
        if (startCodon < 1 || endCodon < 1) {
            // Enforce 1-based codons.
            return null;
        }

        if (transcript.codingStart() == null || transcript.codingEnd() == null) {
            // Only coding transcripts have codons.
            return null;
        }

        List<GenomeRegion> codonRegions = Lists.newArrayList();
        int effectiveStartBase = 1 + (startCodon - 1) * 3;
        int effectiveEndBase = 3 + (endCodon - 1) * 3;

        int basesCovered = 0;
        Integer startPosition = null;
        Integer endPosition = null;

        for (HmfExonRegion exon : transcript.strandSortedExome()) {
            int exonCodingStart = Math.max(exon.start(), transcript.codingStart());
            int exonCodingEnd = Math.min(exon.end(), transcript.codingEnd());
            int exonBaseLength = exonCodingEnd - exonCodingStart + 1;

            if (exonBaseLength <= 0) {
                // Exon is entirely non-coding so can be skipped.
                continue;
            }

            if (basesCovered + exonBaseLength >= effectiveStartBase && startPosition == null) {
                startPosition = transcript.strand() == Strand.FORWARD
                        ? exonCodingStart + effectiveStartBase - basesCovered - 1
                        : exonCodingEnd - effectiveStartBase + basesCovered + 1;
            }

            if (basesCovered + exonBaseLength >= effectiveEndBase && endPosition == null) {
                endPosition = transcript.strand() == Strand.FORWARD
                        ? exonCodingStart + effectiveEndBase - basesCovered - 1
                        : exonCodingEnd - effectiveEndBase + basesCovered + 1;
            }

            basesCovered += exonBaseLength;

            GenomeRegion region = decideOnRangeToIncludeForExon(transcript,
                    startPosition,
                    endPosition,
                    exonCodingStart,
                    exonCodingEnd,
                    !codonRegions.isEmpty());

            if (region != null) {
                codonRegions.add(region);
            }

            if (startPosition != null && endPosition != null) {
                codonRegions.sort(new GenomeRegionComparator());
                return codonRegions;
            }
        }

        return null;
    }

    @Nullable
    private static GenomeRegion decideOnRangeToIncludeForExon(final HmfTranscriptRegion transcript, @Nullable Integer startPosition,
            @Nullable Integer endPosition, int exonCodingStart, int exonCodingEnd, boolean hasCodingRegionsDefinedAlready) {
        if (startPosition != null) {
            if (endPosition == null) {
                // Check to see if we need to include the entire exon we are considering.
                if (hasCodingRegionsDefinedAlready) {
                    return ImmutableGenomeRegionImpl.builder()
                            .chromosome(transcript.chromosome())
                            .start(exonCodingStart)
                            .end(exonCodingEnd)
                            .build();
                } else {
                    return ImmutableGenomeRegionImpl.builder()
                            .chromosome(transcript.chromosome())
                            .start(transcript.strand() == Strand.FORWARD ? startPosition : exonCodingStart)
                            .end(transcript.strand() == Strand.FORWARD ? exonCodingEnd : startPosition)
                            .build();
                }
            } else if (hasCodingRegionsDefinedAlready) {
                return ImmutableGenomeRegionImpl.builder()
                        .chromosome(transcript.chromosome())
                        .start(transcript.strand() == Strand.FORWARD ? exonCodingStart : endPosition)
                        .end(transcript.strand() == Strand.FORWARD ? endPosition : exonCodingEnd)
                        .build();
            } else {
                return ImmutableGenomeRegionImpl.builder()
                        .chromosome(transcript.chromosome())
                        .start(transcript.strand() == Strand.FORWARD ? startPosition : endPosition)
                        .end(transcript.strand() == Strand.FORWARD ? endPosition : startPosition)
                        .build();
            }
        }
        return null;
    }
}

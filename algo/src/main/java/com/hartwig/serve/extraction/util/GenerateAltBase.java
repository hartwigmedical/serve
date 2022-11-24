package com.hartwig.serve.extraction.util;

import com.hartwig.serve.common.RefGenomeFunctions;
import com.hartwig.serve.datamodel.RefGenome;

import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class GenerateAltBase {

    @NotNull
    private final RefGenome refGenome;
    @NotNull
    private final IndexedFastaSequenceFile refGenomeFasta;

    public GenerateAltBase(@NotNull final RefGenome refGenome, @NotNull final IndexedFastaSequenceFile refGenomeFasta) {
        this.refGenome = refGenome;
        this.refGenomeFasta = refGenomeFasta;
    }

    @NotNull
    public String createAltForRefBase(@NotNull String chromosome, long position) {
        String refBaseAtPosition = extractRefBaseAtGenomicPosition(chromosome, position);

        switch (refBaseAtPosition) {
            case "A":
                return "T";
            case "C":
                return "A";
            case "T":
                return "G";
            case "G":
                return "C";
            default:
                throw new IllegalArgumentException("Cannot generate alt for ref base '" + refBaseAtPosition + "'");
        }
    }

    @NotNull
    public String extractRefBaseAtGenomicPosition(@NotNull String chromosome, long position) {
        String versionedChromosome = RefGenomeFunctions.versionedChromosome(chromosome, refGenome);
        return refGenomeFasta.getSubsequenceAt(versionedChromosome, position, position).getBaseString();
    }
}

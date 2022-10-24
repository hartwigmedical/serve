package com.hartwig.serve.refgenome.liftover;

import java.io.File;

import com.hartwig.serve.datamodel.refgenome.RefGenomeFunctions;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;

public class UCSCLiftOver implements LiftOverAlgo {

    @NotNull
    private final LiftOver liftOver;
    @NotNull
    private final RefGenomeVersion targetVersion;

    @NotNull
    public static UCSCLiftOver fromChainFile(@NotNull String chainFile, @NotNull RefGenomeVersion targetVersion) {
        return new UCSCLiftOver(new LiftOver(new File(chainFile)), targetVersion);
    }

    private UCSCLiftOver(@NotNull final LiftOver liftOver, @NotNull final RefGenomeVersion targetVersion) {
        this.liftOver = liftOver;
        this.targetVersion = targetVersion;
    }

    @Nullable
    @Override
    public LiftOverResult liftOver(@NotNull final String chromosome, final int position) {
        // UCSC expects hg19 format in case v37 is used.
        String ucscChromosome = RefGenomeFunctions.enforceChrPrefix(chromosome);
        Interval interval = new Interval(ucscChromosome, position, position);
        Interval lifted = liftOver.liftOver(interval);
        if (lifted == null) {
            return null;
        }

        // We convert chromosome back from UCSC to target ref genome version
        String targetChromosome = RefGenomeFunctions.versionedChromosome(lifted.getContig(), targetVersion);
        return ImmutableLiftOverResult.builder().chromosome(targetChromosome).position(lifted.getStart()).build();
    }
}

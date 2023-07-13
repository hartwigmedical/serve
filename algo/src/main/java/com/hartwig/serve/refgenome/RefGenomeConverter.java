package com.hartwig.serve.refgenome;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.RefGenomeFunctions;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.common.GenomeRegion;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.range.*;
import com.hartwig.serve.datamodel.serialization.ActionableExonFile;
import com.hartwig.serve.extraction.util.ImmutableGenomeRegionImpl;
import com.hartwig.serve.refgenome.liftover.LiftOverAlgo;
import com.hartwig.serve.refgenome.liftover.LiftOverChecker;
import com.hartwig.serve.refgenome.liftover.LiftOverResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

class RefGenomeConverter {

    private static final Logger LOGGER = LogManager.getLogger(RefGenomeConverter.class);

    @NotNull
    private final RefGenome sourceVersion;
    @NotNull
    private final RefGenome targetVersion;
    @NotNull
    private final IndexedFastaSequenceFile targetSequence;
    @NotNull
    private final LiftOverAlgo liftOverAlgo;

    public RefGenomeConverter(@NotNull final RefGenome sourceVersion, @NotNull final RefGenome targetVersion,
            @NotNull final IndexedFastaSequenceFile targetSequence, @NotNull final LiftOverAlgo liftOverAlgo) {
        this.sourceVersion = sourceVersion;
        this.targetVersion = targetVersion;
        this.targetSequence = targetSequence;
        this.liftOverAlgo = liftOverAlgo;
    }

    @NotNull
    public Set<KnownHotspot> convertKnownHotspots(@NotNull Set<KnownHotspot> hotspots) {
        Set<KnownHotspot> convertedHotspots = Sets.newHashSet();
        for (KnownHotspot hotspot : hotspots) {
            KnownHotspot lifted = liftOverKnownHotspot(hotspot);

            if (lifted != null) {
                convertedHotspots.add(lifted);
            }
        }

        return convertedHotspots;
    }

    @NotNull
    public Set<KnownCodon> convertKnownCodons(@NotNull Set<KnownCodon> codons) {
        Set<KnownCodon> convertedCodons = Sets.newHashSet();
        for (KnownCodon codon : codons) {
            KnownCodon lifted = liftOverKnownCodon(codon);
            if (lifted != null) {
                if (codon.end() - codon.start() == 2 && lifted.end() - lifted.start() != 2) {
                    LOGGER.warn(" Skipping liftover from {} to {}: Lifted codon '{}' is no longer 3 bases long. Lifted codon: '{}'",
                            sourceVersion,
                            targetVersion,
                            codon,
                            lifted);
                } else {
                    convertedCodons.add(lifted);
                }
            }
        }

        return convertedCodons;
    }

    @NotNull
    public Set<KnownExon> convertKnownExons(@NotNull Set<KnownExon> exons) {
        Set<KnownExon> convertedExons = Sets.newHashSet();
        for (KnownExon exon : exons) {
            KnownExon lifted = liftOverKnownExon(exon);
            if (lifted != null) {
                convertedExons.add(lifted);
            }
        }

        return convertedExons;
    }

    @NotNull
    public Set<ActionableHotspot> convertActionableHotspots(@NotNull Set<ActionableHotspot> actionableHotspots) {
        Set<ActionableHotspot> convertedActionableHotspots = Sets.newHashSet();
        for (ActionableHotspot actionableHotspot : actionableHotspots) {
            ActionableHotspot lifted = liftOverActionableHotspot(actionableHotspot);
            if (lifted != null) {
                convertedActionableHotspots.add(lifted);
            }
        }
        return convertedActionableHotspots;
    }

    @NotNull
    public Set<ActionableCodon> convertActionableCodons(@NotNull Set<ActionableCodon> actionableCodons) {
        Set<ActionableCodon> convertedActionableCodons = Sets.newHashSet();
        for (ActionableCodon actionableCodon : actionableCodons) {
            ActionableCodon lifted = liftOverActionableCodon(actionableCodon);
            if (lifted != null) {
                convertedActionableCodons.add(lifted);
            }
        }
        return convertedActionableCodons;
    }

    @NotNull
    public Set<ActionableExon> convertActionableExons(@NotNull Set<ActionableExon> actionableExons) {
        Set<ActionableExon> convertedActionableExons = Sets.newHashSet();
        for (ActionableExon actionableExon : actionableExons) {
            ActionableExon lifted = liftOverActionableExons(actionableExon);
            if (lifted != null) {
                convertedActionableExons.add(lifted);
            }
        }
        return convertedActionableExons;
    }

    @Nullable
    private KnownHotspot liftOverKnownHotspot(@NotNull KnownHotspot knownHotspot) {
        LiftOverResult lifted = liftOverHotspot(knownHotspot);

        if (lifted == null) {
            return null;
        }

        return ImmutableKnownHotspot.builder().from(knownHotspot).chromosome(lifted.chromosome()).position(lifted.position()).build();
    }

    @Nullable
    private ActionableHotspot liftOverActionableHotspot(@NotNull ActionableHotspot actionableHotspot) {
        LiftOverResult lifted = liftOverHotspot(actionableHotspot);

        if (lifted == null) {
            return null;
        }

        return ImmutableActionableHotspot.builder()
                .from(actionableHotspot)
                .chromosome(lifted.chromosome())
                .position(lifted.position())
                .build();
    }

    @Nullable
    private LiftOverResult liftOverHotspot(@NotNull VariantHotspot hotspot) {
        LiftOverResult lifted = liftOverAlgo.liftOver(hotspot.chromosome(), hotspot.position());

        if (!LiftOverChecker.isValidLiftedPosition(lifted, hotspot)) {
            return null;
        }

        verifyNoChromosomeChange(hotspot.chromosome(), lifted, hotspot);

        String newRef = sequence(lifted.chromosome(), lifted.position(), hotspot.ref().length());
        if (!newRef.equals(hotspot.ref())) {
            LOGGER.warn(" Skipping liftover from {} to {}: Ref changed from '{}' to '{}' on position {} from {}",
                    sourceVersion,
                    targetVersion,
                    hotspot.ref(),
                    newRef,
                    lifted.position(),
                    hotspot);
            return null;
        }

        return lifted;
    }

    @Nullable
    private KnownCodon liftOverKnownCodon(@NotNull KnownCodon knownCodon) {
        GenomeRegion lifted = liftOverRange(knownCodon);

        if (lifted == null) {
            return null;
        }

        return ImmutableKnownCodon.builder()
                .from(knownCodon)
                .chromosome(lifted.chromosome())
                .start(lifted.start())
                .end(lifted.end())
                .build();
    }

    @Nullable
    private KnownExon liftOverKnownExon(@NotNull KnownExon knownExon) {
        GenomeRegion lifted = liftOverRange(knownExon);

        if (lifted == null) {
            return null;
        }

        return ImmutableKnownExon.builder().from(knownExon).chromosome(lifted.chromosome()).start(lifted.start()).end(lifted.end()).build();
    }

    @Nullable
    private ActionableCodon liftOverActionableCodon(@NotNull ActionableCodon actionableCodon) {
        GenomeRegion lifted = liftOverRange(actionableCodon);

        if (lifted == null) {
            return null;
        }

        return ImmutableActionableCodon.builder()
                .from(actionableCodon)
                .chromosome(lifted.chromosome())
                .start(lifted.start())
                .end(lifted.end())
                .build();
    }

    @Nullable
    private ActionableExon liftOverActionableExons(@NotNull ActionableExon actionableExon) {
        GenomeRegion lifted = liftOverRange(actionableExon);

        if (lifted == null) {
            return null;
        }

        return ImmutableActionableExon.builder()
                .from(actionableExon)
                .chromosome(lifted.chromosome())
                .start(lifted.start())
                .end(lifted.end())
                .build();
    }

    @Nullable
    private GenomeRegion liftOverRange(@NotNull RangeAnnotation range) {
        LiftOverResult liftedStart = liftOverAlgo.liftOver(range.chromosome(), range.start());
        LiftOverResult liftedEnd = liftOverAlgo.liftOver(range.chromosome(), range.end());

        if (!LiftOverChecker.isValidLiftedRegion(liftedStart, liftedEnd, range)) {
            return null;
        }

        verifyNoChromosomeChange(range.chromosome(), liftedStart, range);
        verifyNoChromosomeChange(range.chromosome(), liftedEnd, range);

        return ImmutableGenomeRegionImpl.builder()
                .chromosome(liftedStart.chromosome())
                .start(liftedStart.position())
                .end(liftedEnd.position())
                .build();
    }


    private void verifyNoChromosomeChange(@NotNull String prevChromosome, @NotNull LiftOverResult lifted, @NotNull Object object) {
        String versionedChromosome = RefGenomeFunctions.versionedChromosome(prevChromosome, targetVersion);
        if (!lifted.chromosome().equals(versionedChromosome)) {
            LOGGER.warn(" Liftover from {} to {} moved chromosome from '{}' to '{}' on {}",
                    sourceVersion,
                    targetVersion,
                    versionedChromosome,
                    lifted.chromosome(),
                    object);
        }
    }

    @NotNull
    private String sequence(@NotNull String chromosome, long start, long length) {
        String targetChromosome = RefGenomeFunctions.versionedChromosome(chromosome, targetVersion);
        return targetSequence.getSubsequenceAt(targetChromosome, start, start + length - 1).getBaseString();
    }
}
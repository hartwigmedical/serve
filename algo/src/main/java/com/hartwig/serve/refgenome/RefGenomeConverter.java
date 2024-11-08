package com.hartwig.serve.refgenome;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.RefGenomeFunctions;
import com.hartwig.serve.datamodel.ActionableTrial;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.ImmutableEfficacyEvidence;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.MolecularCriterium;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.common.GenomeRegion;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.RangeAnnotation;
import com.hartwig.serve.extraction.util.ImmutableGenomeRegionImpl;
import com.hartwig.serve.refgenome.liftover.LiftOverAlgo;
import com.hartwig.serve.refgenome.liftover.LiftOverChecker;
import com.hartwig.serve.refgenome.liftover.LiftOverResult;

import org.apache.commons.compress.utils.Lists;
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

    @Nullable
    public KnownEvents convertKnownEvents(@Nullable KnownEvents knownEvents) {
        if (knownEvents == null) {
            return null;
        }

        return ImmutableKnownEvents.builder()
                .from(knownEvents)
                .hotspots(convertKnownHotspots(knownEvents.hotspots()))
                .codons(convertKnownCodons(knownEvents.codons()))
                .exons(convertKnownExons(knownEvents.exons()))
                .build();
    }

    @Nullable
    public List<EfficacyEvidence> convertEfficacyEvidences(@Nullable List<EfficacyEvidence> evidences) {
        if (evidences == null) {
            return null;
        }

        List<EfficacyEvidence> converted = Lists.newArrayList();
        for (EfficacyEvidence evidence : evidences) {
            converted.add(convertEfficacyEvidence(evidence));
        }
        return converted;
    }

    @Nullable
    public List<ActionableTrial> convertClinicalTrials(@Nullable List<ActionableTrial> clinicalTrials) {
        if (clinicalTrials == null) {
            return null;
        }

        List<ActionableTrial> converted = Lists.newArrayList();
        for (ActionableTrial clinicalTrial : clinicalTrials) {
            converted.add(convertClinicalTrial(clinicalTrial));
        }
        return converted;
    }

    @NotNull
    private Set<KnownHotspot> convertKnownHotspots(@NotNull Set<KnownHotspot> hotspots) {
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
    private Set<KnownCodon> convertKnownCodons(@NotNull Set<KnownCodon> codons) {
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
    private Set<KnownExon> convertKnownExons(@NotNull Set<KnownExon> exons) {
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
    private EfficacyEvidence convertEfficacyEvidence(@NotNull EfficacyEvidence evidence) {
        return ImmutableEfficacyEvidence.builder()
                .from(evidence)
                .molecularCriterium(convertMolecularCriterium(evidence.molecularCriterium()))
                .build();
    }

    @NotNull
    private ActionableTrial convertClinicalTrial(@NotNull ActionableTrial clinicalTrial) {
        Set<MolecularCriterium> convertedCriteria = Sets.newHashSet();
        for (MolecularCriterium criterium : clinicalTrial.molecularCriteria()) {
            convertedCriteria.add(convertMolecularCriterium(criterium));
        }
        return ImmutableActionableTrial.builder().from(clinicalTrial).molecularCriteria(convertedCriteria).build();
    }

    @NotNull
    private MolecularCriterium convertMolecularCriterium(@NotNull MolecularCriterium molecularCriterium) {
        return ImmutableMolecularCriterium.builder()
                .from(molecularCriterium)
                .hotspots(convertActionableHotspots(molecularCriterium.hotspots()))
                .codons(convertActionableRanges(molecularCriterium.codons()))
                .exons(convertActionableRanges(molecularCriterium.exons()))
                .build();
    }

    @NotNull
    private Set<ActionableHotspot> convertActionableHotspots(@NotNull Set<ActionableHotspot> actionableHotspots) {
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
    private Set<ActionableRange> convertActionableRanges(@NotNull Set<ActionableRange> actionableRanges) {
        Set<ActionableRange> convertedActionableRanges = Sets.newHashSet();
        for (ActionableRange actionableRange : actionableRanges) {
            ActionableRange lifted = liftOverActionableRange(actionableRange);
            if (lifted != null) {
                convertedActionableRanges.add(lifted);
            }
        }
        return convertedActionableRanges;
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
    private ActionableRange liftOverActionableRange(@NotNull ActionableRange actionableRange) {
        GenomeRegion lifted = liftOverRange(actionableRange);

        if (lifted == null) {
            return null;
        }

        return ImmutableActionableRange.builder()
                .from(actionableRange)
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
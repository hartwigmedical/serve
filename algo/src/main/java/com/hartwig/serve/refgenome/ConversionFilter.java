package com.hartwig.serve.refgenome;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableTrial;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.ImmutableEfficacyEvidence;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.MolecularCriterium;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ConversionFilter {

    private static final Logger LOGGER = LogManager.getLogger(ConversionFilter.class);

    @NotNull
    private final Set<String> filteredGenes = Sets.newHashSet();

    @NotNull
    public ExtractionResult filter(@NotNull ExtractionResult extractionResult) {
        return ImmutableExtractionResult.builder()
                .from(extractionResult)
                .knownEvents(filterKnownEvents(extractionResult.knownEvents()))
                .efficacyEvidences(filterEfficacyEvidences(extractionResult.efficacyEvidences()))
                .clinicalTrials(filterClinicalTrials(extractionResult.clinicalTrials()))
                .build();
    }

    public void reportUnusedFilterEntries() {
        int unusedGeneCount = 0;
        for (String gene : ConversionFilterFactory.GENES_TO_EXCLUDE_FOR_CONVERSION) {
            if (!filteredGenes.contains(gene)) {
                unusedGeneCount++;
                LOGGER.warn("Gene '{}' hasn't been used during ref genome conversion filtering", gene);
            }
        }

        LOGGER.debug("Found {} unused genes during ref genome conversion", unusedGeneCount);
    }

    @Nullable
    private KnownEvents filterKnownEvents(@Nullable KnownEvents knownEvents) {
        if (knownEvents == null) {
            return null;
        }

        return ImmutableKnownEvents.builder()
                .hotspots(filterHotspots(knownEvents.hotspots()))
                .codons(filterCodons(knownEvents.codons()))
                .exons(filterExons(knownEvents.exons()))
                .genes(filterGenes(knownEvents.genes()))
                .copyNumbers(filterCopyNumbers(knownEvents.copyNumbers()))
                .fusions(filterFusions(knownEvents.fusions()))
                .build();
    }

    @NotNull
    private Set<KnownHotspot> filterHotspots(@NotNull Set<KnownHotspot> hotspots) {
        Set<KnownHotspot> filteredHotspots = Sets.newHashSet();
        for (KnownHotspot hotspot : hotspots) {
            if (!isGeneToExclude(hotspot.gene())) {
                filteredHotspots.add(hotspot);
            } else {
                LOGGER.debug("Filtered known hotspot for ref genome conversion: {}", hotspot);
            }
        }
        return filteredHotspots;
    }

    @NotNull
    private Set<KnownCodon> filterCodons(@NotNull Set<KnownCodon> codons) {
        Set<KnownCodon> filteredCodons = Sets.newHashSet();
        for (KnownCodon codon : codons) {
            if (!isGeneToExclude(codon.gene())) {
                filteredCodons.add(codon);
            } else {
                LOGGER.debug("Filtered known codon for ref genome conversion: {}", codon);
            }
        }
        return filteredCodons;
    }

    @NotNull
    private Set<KnownExon> filterExons(@NotNull Set<KnownExon> exons) {
        Set<KnownExon> filteredExons = Sets.newHashSet();
        for (KnownExon exon : exons) {
            if (!isGeneToExclude(exon.gene())) {
                filteredExons.add(exon);
            } else {
                LOGGER.debug("Filtered known exon for ref genome conversion: {}", exon);
            }
        }
        return filteredExons;
    }

    @NotNull
    private Set<KnownGene> filterGenes(@NotNull Set<KnownGene> genes) {
        Set<KnownGene> filteredGenes = Sets.newHashSet();
        for (KnownGene gene : genes) {
            if (!isGeneToExclude(gene.gene())) {
                filteredGenes.add(gene);
            } else {
                LOGGER.debug("Filtered known gene for ref genome conversion: {}", gene);
            }
        }
        return filteredGenes;
    }

    @NotNull
    private Set<KnownCopyNumber> filterCopyNumbers(@NotNull Set<KnownCopyNumber> copyNumbers) {
        Set<KnownCopyNumber> filteredCopyNumbers = Sets.newHashSet();
        for (KnownCopyNumber copyNumber : copyNumbers) {
            if (!isGeneToExclude(copyNumber.gene())) {
                filteredCopyNumbers.add(copyNumber);
            } else {
                LOGGER.debug("Filtered known copy number for ref genome conversion: {}", copyNumber);
            }
        }
        return filteredCopyNumbers;
    }

    @NotNull
    private Set<KnownFusion> filterFusions(@NotNull Set<KnownFusion> fusions) {
        Set<KnownFusion> filteredFusionPairs = Sets.newHashSet();
        for (KnownFusion fusion : fusions) {
            if (!isGeneToExclude(fusion.geneUp()) && !isGeneToExclude(fusion.geneDown())) {
                filteredFusionPairs.add(fusion);
            } else {
                LOGGER.debug("Filtered known fusion for ref genome conversion: {}", fusion);
            }
        }
        return filteredFusionPairs;
    }

    @Nullable
    private List<EfficacyEvidence> filterEfficacyEvidences(@Nullable List<EfficacyEvidence> evidences) {
        if (evidences == null) {
            return null;
        }

        List<EfficacyEvidence> filtered = Lists.newArrayList();
        for (EfficacyEvidence evidence : evidences) {
            MolecularCriterium criterium = cleanMolecularCriterium(evidence.molecularCriterium());
            if (hasAtLeastOneCriterium(criterium)) {
                filtered.add(ImmutableEfficacyEvidence.builder().from(evidence).molecularCriterium(criterium).build());
            }
        }
        return filtered;
    }

    @Nullable
    private List<ActionableTrial> filterClinicalTrials(@Nullable List<ActionableTrial> clinicalTrials) {
        if (clinicalTrials == null) {
            return null;
        }

        List<ActionableTrial> filtered = Lists.newArrayList();
        for (ActionableTrial clinicalTrial : clinicalTrials) {
            List<MolecularCriterium> cleanedCriteria = Lists.newArrayList();
            for (MolecularCriterium criterium : clinicalTrial.anyMolecularCriteria()) {
                MolecularCriterium cleaned = cleanMolecularCriterium(criterium);
                if (hasAtLeastOneCriterium(cleaned)) {
                    cleanedCriteria.add(cleaned);
                }
            }

            if (!cleanedCriteria.isEmpty()) {
                filtered.add(ImmutableActionableTrial.builder().from(clinicalTrial).anyMolecularCriteria(cleanedCriteria).build());
            }
        }
        return filtered;
    }

    @NotNull
    private MolecularCriterium cleanMolecularCriterium(@NotNull MolecularCriterium criterium) {
        return ImmutableMolecularCriterium.builder()
                .from(criterium)
                .hotspots(filterActionableHotspots(criterium.hotspots()))
                .codons(filterActionableRange(criterium.codons()))
                .exons(filterActionableRange(criterium.exons()))
                .genes(filterActionableGenes(criterium.genes()))
                .fusions(filterActionableFusions(criterium.fusions()))
                .build();
    }

    private static boolean hasAtLeastOneCriterium(@NotNull MolecularCriterium criterium) {
        return !criterium.hotspots().isEmpty() || !criterium.codons().isEmpty() || !criterium.exons().isEmpty() || !criterium.genes()
                .isEmpty() || !criterium.fusions().isEmpty() || !criterium.characteristics().isEmpty() || !criterium.hla().isEmpty();
    }

    @NotNull
    private Set<ActionableHotspot> filterActionableHotspots(@NotNull Set<ActionableHotspot> actionableHotspots) {
        return actionableHotspots;
    }

    @NotNull
    private Set<ActionableRange> filterActionableRange(@NotNull Set<ActionableRange> actionableRange) {
        return actionableRange;
    }

    @NotNull
    private Set<ActionableGene> filterActionableGenes(@NotNull Set<ActionableGene> actionableGenes) {
        Set<ActionableGene> filteredActionableGenes = Sets.newHashSet();
        for (ActionableGene actionableGene : actionableGenes) {
            if (!isGeneToExclude(actionableGene.gene())) {
                filteredActionableGenes.add(actionableGene);
            } else {
                LOGGER.debug("Filtered actionable gene for ref genome conversion: {}", actionableGene);
            }
        }
        return filteredActionableGenes;
    }

    @NotNull
    private Set<ActionableFusion> filterActionableFusions(@NotNull Set<ActionableFusion> actionableFusions) {
        Set<ActionableFusion> filteredActionableFusions = Sets.newHashSet();
        for (ActionableFusion actionableFusion : actionableFusions) {
            if (!isGeneToExclude(actionableFusion.geneUp()) && !isGeneToExclude(actionableFusion.geneDown())) {
                filteredActionableFusions.add(actionableFusion);
            } else {
                LOGGER.debug("Filtered actionable fusion for ref genome conversion: {}", actionableFusion);
            }
        }
        return filteredActionableFusions;
    }

    private boolean isGeneToExclude(@NotNull String gene) {
        if (ConversionFilterFactory.GENES_TO_EXCLUDE_FOR_CONVERSION.contains(gene)) {
            filteredGenes.add(gene);
            return true;
        } else {
            return false;
        }
    }
}

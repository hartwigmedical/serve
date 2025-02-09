package com.hartwig.serve.sources.ckb;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.datamodel.ImmutableActionableEventImpl;
import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbMolecularCriteriaExtractor {
    private static final Logger LOGGER = LogManager.getLogger(CkbMolecularCriteriaExtractor.class);

    // TODO inject this in constructor
    @NotNull
    CkbVariantCriteriaExtractor variantCriteriaExtractor;

    public CkbMolecularCriteriaExtractor(@NotNull EventExtractor eventExtractor) {
        this.variantCriteriaExtractor = new CkbVariantCriteriaExtractor();
    }

    @NotNull
    public MolecularCriterium criterium(CkbEntry entry, Set<EventExtractorOutput> eventExtractorOutputs) {
        String sourceEvent = combinedSourceEvent(entry);
        ActionableEvent actionableEvent = toActionableEvent(sourceEvent, entry);

        Set<MolecularCriterium> molecularCriteria = eventExtractorOutputs.stream()
                .map(eventExtractorOutput -> variantCriteriaExtractor.createMolecularCriterium(eventExtractorOutput, actionableEvent))
                .collect(Collectors.toSet());

        // Join the molecular criteria into a single set, which represents the conjunction of the individual
        // variant criteria. TODO We eventually ended up with a MolecularCriteriaCombiner so check into updating & using that instead

        return ImmutableMolecularCriterium.builder()
                .addAllOneOfEachHotspots(molecularCriteria.stream()
                        .flatMap(c -> c.oneOfEachHotspots().stream())
                        .collect(Collectors.toSet()))
                .addAllCodons(molecularCriteria.stream().flatMap(c -> c.codons().stream()).collect(Collectors.toSet()))
                .addAllExons(molecularCriteria.stream().flatMap(c -> c.exons().stream()).collect(Collectors.toSet()))
                .addAllGenes(molecularCriteria.stream().flatMap(c -> c.genes().stream()).collect(Collectors.toSet()))
                .addAllFusions(molecularCriteria.stream().flatMap(c -> c.fusions().stream()).collect(Collectors.toSet()))
                .addAllCharacteristics(molecularCriteria.stream().flatMap(c -> c.characteristics().stream()).collect(Collectors.toSet()))
                .addAllHla(molecularCriteria.stream().flatMap(c -> c.hla().stream()).collect(Collectors.toSet()))
                .build();

    }

    @NotNull
    private String combinedSourceEvent(@NotNull CkbEntry entry) {
        return entry.variants().stream()
                .map(variant -> {
                    String event = CkbEventAndGeneExtractor.extractEvent(variant);
                    String gene = CkbEventAndGeneExtractor.extractGene(variant);
                    return gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;
                })
                .collect(Collectors.joining(" & "));
    }

    @NotNull
    private static ActionableEvent toActionableEvent(@NotNull String sourceEvent, @NotNull CkbEntry entry) {
        String sourceUrl = "https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=" + entry.profileId();
        LocalDate sourceDate = entry.createDate();
        return ImmutableActionableEventImpl.builder().sourceDate(sourceDate).sourceEvent(sourceEvent).sourceUrls(Set.of(sourceUrl)).build();
    }
}

package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbTestFactory.createCombinedEntry;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.ImmutableEventInterpretation;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;
import com.hartwig.serve.sources.ckb.filter.CkbFilteringTestFactory;
import com.hartwig.serve.sources.ckb.region.ImmutableCkbRegion;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachTestFactory;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbExtractorTest {

    @Test
    public void canExtractEvidenceAndTrialsFromCkbEntries() {
        ExtractionResult result = ckbExtractor().extract(createCkbEntryTestDatabase());

        assertEquals(7, result.evidences().size());
        assertEquals(7, result.trials().size());
        assertEquals(1, result.knownEvents().hotspots().size());
        assertEquals(1, result.knownEvents().exons().size());
        assertEquals(1, result.knownEvents().codons().size());
        assertEquals(1, result.knownEvents().fusions().size());
        assertEquals(1, result.knownEvents().copyNumbers().size());
        assertEquals(3, result.knownEvents().genes().size());
    }

    @Test
    public void canExtractEvidenceWithCombinedCriteria() {

        List<CkbEntry> ckbEntries = Lists.newArrayList();
        List<Variant> variants =
                List.of(CkbTestFactory.createVariant("BRAF", "loss", "BRAF loss"), CkbTestFactory.createVariant("KIT", "loss", "KIT loss"));

        ckbEntries.add(createCombinedEntry(variants,
                "sensitive",
                "Actionable",
                "any treatment",
                "any indication",
                "A",
                "Guideline",
                "DOID:162"));

        ExtractionResult extractionResult = ckbExtractor().extract(ckbEntries);

        assertEquals(1, extractionResult.evidences().size());
        MolecularCriterium criterium = extractionResult.evidences().get(0).molecularCriterium();
        assertEquals(2, criterium.genes().size());

        assertEquals(0, extractionResult.knownEvents().hotspots().size());
        assertEquals(0, extractionResult.knownEvents().codons().size());
        assertEquals(0, extractionResult.knownEvents().exons().size());
        assertEquals(2, extractionResult.knownEvents().genes().size());
        assertEquals(2, extractionResult.knownEvents().copyNumbers().size());
        assertEquals(0, extractionResult.knownEvents().fusions().size());

        assertEquals(1, extractionResult.eventInterpretations().size());
        EventInterpretation interpretation = extractionResult.eventInterpretations().iterator().next();

        assertEquals(ImmutableEventInterpretation.builder()
                .source(Knowledgebase.CKB)
                .sourceEvent("Multiple loss,loss")
                .interpretedGene("Multiple")
                .interpretedEvent("loss,loss")
                .interpretedEventType(EventType.COMBINED)
                .build(), interpretation);

        assertEquals(Knowledgebase.CKB, interpretation.source());

        // trials not extracted yet
        assertEquals(0, extractionResult.trials().size());
    }

    @Test
    public void shouldReturnInterpretationOnlyForUnknownEvents() {
        Variant recognizedVariant = CkbTestFactory.createVariant("BRAF", "V600E", "BRAF V600E");
        Variant unrecognizedVariant = CkbTestFactory.createVariant("BRAF", "unknown_type", "BRAF unknown_type");

        CkbEntry entryWithInvalidVariant = createCombinedEntry(List.of(recognizedVariant, unrecognizedVariant));

        assertEmptyExtractionWithInterpretation(
                interpretationBuilder()
                        .sourceEvent("Multiple V600E,unknown_type")
                        .interpretedGene("Multiple")
                        .interpretedEvent("V600E,unknown_type")
                        .interpretedEventType(EventType.COMBINED)
                        .build(),
                ckbExtractor().extract(List.of(entryWithInvalidVariant)));
    }

    @Test
    public void shouldReturnInterpretationOnlyForComplexEvent() {
        Variant complexVariant = CkbTestFactory.createVariant("BRAF", "V600*fs", "BRAF V600*fs");

        CkbEntry entryWithComplexVariant = createCombinedEntry(List.of(complexVariant));

        assertEmptyExtractionWithInterpretation(
                interpretationBuilder()
                        .sourceEvent("BRAF V600*fs")
                        .interpretedGene("BRAF")
                        .interpretedEvent("V600*fs")
                        .interpretedEventType(EventType.COMPLEX)
                        .build(),
                ckbExtractor().extract(List.of(entryWithComplexVariant)));
    }

    @Test
    public void shouldReturnInterpretationOnlyForUnresolvableEvent() {
        Variant complexVariant = CkbTestFactory.createVariant("UNKNOWN_GENE", "act mut", "UNKNOWN_GENE act mut");

        CkbEntry entryWithComplexVariant = createCombinedEntry(List.of(complexVariant));

        assertEmptyExtractionWithInterpretation(
                interpretationBuilder()
                        .sourceEvent("UNKNOWN_GENE act mut")
                        .interpretedGene("UNKNOWN_GENE")
                        .interpretedEvent("act mut")
                        .interpretedEventType(EventType.GENE_LEVEL)
                        .build(),
                ckbExtractor().extract(List.of(entryWithComplexVariant)));
    }

    private void assertEmptyExtractionWithInterpretation(@NotNull EventInterpretation eventInterpretation,
            @NotNull ExtractionResult extractionResult) {
        assertEquals(ImmutableExtractionResult.builder()
                        .refGenomeVersion(RefGenome.V38)
                        .eventInterpretations(Set.of(eventInterpretation))
                        .build(),
                extractionResult);
    }

    @NotNull
    private ImmutableEventInterpretation.Builder interpretationBuilder() {
        return ImmutableEventInterpretation.builder().source(Knowledgebase.CKB);
    }

    @NotNull
    private static CkbExtractor ckbExtractor() {
        return CkbExtractorFactory.createExtractor(CkbClassificationConfig.build(),
                RefGenomeResourceTestFactory.buildTestResource37(),
                TreatmentApproachTestFactory.createEmptyCurator(),
                CkbFilteringTestFactory.createEmptyEvidenceFilterModel(),
                CkbFilteringTestFactory.createEmptyTrialFilterModel(),
                Set.of(ImmutableCkbRegion.builder().country("netherlands").states(Collections.emptySet()).build()));
    }

    @NotNull
    private static List<CkbEntry> createCkbEntryTestDatabase() {
        List<CkbEntry> ckbEntries = Lists.newArrayList();
        ckbEntries.add(createWithOpenMolecularTrial("nct1", "KIT", "amp", "KIT amp", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("nct2", "BRAF", "V600E", "BRAF V600E", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("nct3",
                "NTRK3",
                "fusion promiscuous",
                "NTRK3 fusion promiscuous",
                "sensitive",
                "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("nct4", "BRAF", "V600", "BRAF V600", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("nct5", "BRAF", "exon 1 deletion", "BRAF exon 1 deletion", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("nct6", "-", "MSI high", "MSI high", "sensitive", "Actionable"));
        ckbEntries.add(createWithOpenMolecularTrial("nct7", "ALK", "EML4-ALK", "EML4-ALK Fusion", "sensitive", "Actionable"));

        return ckbEntries;
    }

    @NotNull
    private static CkbEntry createWithOpenMolecularTrial(@NotNull String nctId, @NotNull String gene, @NotNull String variant,
            @NotNull String fullName, @NotNull String responseType, @NotNull String evidenceType) {
        CkbEntry baseEntry = CkbTestFactory.createEntry(gene,
                variant,
                fullName,
                responseType,
                evidenceType,
                "any treatment",
                "any indication",
                "A",
                "Guideline",
                "DOID:162");

        return CkbTestFactory.builder()
                .from(baseEntry)
                .clinicalTrials(List.of(CkbTestFactory.createTrialWithTherapy(nctId,
                        "title",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")),
                        List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                        "Recruiting",
                        List.of("senior", "child", "adult"),
                        List.of(ImmutableVariantRequirementDetail.builder()
                                .profileId(baseEntry.profileId())
                                .requirementType("required")
                                .build()),
                        List.of(CkbTestFactory.createLocation("Netherlands", null, "Rotterdam", "EMC")))))
                .build();
    }
}
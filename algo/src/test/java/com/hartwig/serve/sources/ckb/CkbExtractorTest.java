package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbTestFactory.createCombinedEntry;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.hartwig.serve.ckb.classification.CkbClassificationConfig;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.ImmutableCkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.refgenome.RefGenomeResourceTestFactory;
import com.hartwig.serve.sources.ckb.filter.CkbFilteringTestFactory;
import com.hartwig.serve.sources.ckb.region.ImmutableCkbRegion;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachTestFactory;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbExtractorTest {

    private static final ExtractionResult EMPTY_EXTRACTION_RESULT =
            ImmutableExtractionResult.builder().refGenomeVersion(RefGenome.V38).build();

    @Test
    public void canExtractEvidenceAndTrialsFromCkbEntries() {
        CkbExtractor trialExtractor = CkbExtractorFactory.createExtractor(CkbClassificationConfig.build(),
                RefGenomeResourceTestFactory.buildTestResource37(),
                TreatmentApproachTestFactory.createEmptyCurator(),
                CkbFilteringTestFactory.createEmptyEvidenceFilterModel(),
                CkbFilteringTestFactory.createEmptyTrialFilterModel(),
                Set.of(ImmutableCkbRegion.builder().country("netherlands").states(Collections.emptySet()).build()));

        ExtractionResult trialResult = trialExtractor.extract(createCkbEntryTestDatabase());
        assertEquals(7, trialResult.trials().size());
        assertEquals(7, trialResult.evidences().size());
        assertEquals(1, trialResult.knownEvents().hotspots().size());
        assertEquals(1, trialResult.knownEvents().exons().size());
        assertEquals(1, trialResult.knownEvents().codons().size());
        assertEquals(1, trialResult.knownEvents().fusions().size());
        assertEquals(1, trialResult.knownEvents().copyNumbers().size());
        assertEquals(3, trialResult.knownEvents().genes().size());
    }

    @Test
    public void canExtractEvidenceWithCombinedCriteria() {
        CkbExtractor ckbExtractor = CkbExtractorFactory.createExtractor(CkbClassificationConfig.build(),
                RefGenomeResourceTestFactory.buildTestResource37(),
                TreatmentApproachTestFactory.createEmptyCurator(),
                CkbFilteringTestFactory.createEmptyEvidenceFilterModel(),
                CkbFilteringTestFactory.createEmptyTrialFilterModel(),
                Set.of(ImmutableCkbRegion.builder().country("netherlands").states(Collections.emptySet()).build()));

        List<CkbEntry> ckbEntries = Lists.newArrayList();
        List<Variant> variants = List.of(
                CkbTestFactory.createVariant("BRAF", "loss", "BRAF loss"),
                CkbTestFactory.createVariant("KIT", "loss", "KIT loss")
        );

        ckbEntries.add(createCombinedEntry(variants,
                "sensitive",
                "Actionable",
                "any treatment",
                "any indication",
                "A",
                "Guideline",
                "DOID:162"));

        ExtractionResult extractionResult = ckbExtractor.extract(ckbEntries);

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
        assertEquals(EventType.COMBINED, interpretation.interpretedEventType());
        assertEquals("Multiple", interpretation.interpretedGene());
        assertEquals("loss,loss", interpretation.interpretedEvent());
        assertEquals("Multiple loss,loss", interpretation.sourceEvent());
        assertEquals(Knowledgebase.CKB, interpretation.source());

        // trials not extracted yet
        assertEquals(0, extractionResult.trials().size());
    }

    @Test
    public void shouldReturnNullForUnresolvableEvents() {
        CkbExtractor ckbExtractor = CkbExtractorFactory.createExtractor(CkbClassificationConfig.build(),
                RefGenomeResourceTestFactory.buildTestResource37(),
                TreatmentApproachTestFactory.createEmptyCurator(),
                CkbFilteringTestFactory.createEmptyEvidenceFilterModel(),
                CkbFilteringTestFactory.createEmptyTrialFilterModel(),
                Set.of(ImmutableCkbRegion.builder().country("netherlands").states(Collections.emptySet()).build()));

        Variant recognizedVariant = CkbTestFactory.createVariant("BRAF", "V600E", "BRAF V600E");
        Variant unrecognizedVariant1 = CkbTestFactory.createVariant("BRAF", "unknown_type", "BRAF unknown_type");
        Variant unrecognizedVariant2 = CkbTestFactory.createVariant("KIT", "unknown_type", "KIT unknown_type");

        CkbEntry entryWithAllInvalid = createCombinedEntry(List.of(unrecognizedVariant1, unrecognizedVariant2),
                "sensitive",
                "Actionable",
                "any treatment",
                "any indication",
                "A",
                "Guideline",
                "DOID:162");

        CkbEntry entryWithSomeInvalid = ImmutableCkbEntry.builder()
                .from(entryWithAllInvalid)
                .addAllVariants(List.of(recognizedVariant, unrecognizedVariant1))
                .build();

        assertEquals(EMPTY_EXTRACTION_RESULT, ckbExtractor.extract(List.of(entryWithAllInvalid)));
        assertEquals(EMPTY_EXTRACTION_RESULT, ckbExtractor.extract(List.of(entryWithSomeInvalid)));
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
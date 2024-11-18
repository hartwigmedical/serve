package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.ACTIONABLECHARACTERISTIC;
import static com.hartwig.serve.database.Tables.ACTIONABLECODON;
import static com.hartwig.serve.database.Tables.ACTIONABLEEXON;
import static com.hartwig.serve.database.Tables.ACTIONABLEFUSION;
import static com.hartwig.serve.database.Tables.ACTIONABLEGENE;
import static com.hartwig.serve.database.Tables.ACTIONABLEHLA;
import static com.hartwig.serve.database.Tables.ACTIONABLEHOTSPOT;
import static com.hartwig.serve.database.Tables.EVENTINTERPRETATION;
import static com.hartwig.serve.database.Tables.KNOWNCODON;
import static com.hartwig.serve.database.Tables.KNOWNCOPYNUMBER;
import static com.hartwig.serve.database.Tables.KNOWNEXON;
import static com.hartwig.serve.database.Tables.KNOWNFUSION;
import static com.hartwig.serve.database.Tables.KNOWNGENE;
import static com.hartwig.serve.database.Tables.KNOWNHOTSPOT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.common.CancerType;
import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.EvidenceDirection;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevel;
import com.hartwig.serve.datamodel.efficacy.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.KnownGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.molecular.range.ActionableRange;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.Country;
import com.hartwig.serve.datamodel.trial.Hospital;
import com.hartwig.serve.datamodel.trial.ImmutableActionableTrial;
import com.hartwig.serve.extraction.events.EventInterpretation;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep10;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep7;
import org.jooq.InsertValuesStepN;

@SuppressWarnings({ "unchecked", "ResultOfMethodCallIgnored" })
public class ServeDAO {

    private static final Logger LOGGER = LogManager.getLogger(ServeDAO.class);

    private static final String MAIN_JOINER = ",";
    private static final String SUB_JOINER = ";";

    @NotNull
    private final DSLContext context;

    ServeDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        LOGGER.info("Deleting all data from SERVE database");
        context.deleteFrom(ACTIONABLEHOTSPOT).execute();
        context.deleteFrom(ACTIONABLECODON).execute();
        context.deleteFrom(ACTIONABLEEXON).execute();
        context.deleteFrom(ACTIONABLEGENE).execute();
        context.deleteFrom(ACTIONABLEFUSION).execute();
        context.deleteFrom(ACTIONABLECHARACTERISTIC).execute();
        context.deleteFrom(ACTIONABLEHLA).execute();
        context.deleteFrom(KNOWNHOTSPOT).execute();
        context.deleteFrom(KNOWNCODON).execute();
        context.deleteFrom(KNOWNEXON).execute();
        context.deleteFrom(KNOWNGENE).execute();
        context.deleteFrom(KNOWNCOPYNUMBER).execute();
        context.deleteFrom(KNOWNFUSION).execute();
        context.deleteFrom(EVENTINTERPRETATION).execute();
    }

    void write(@NotNull ServeRecord serveRecord, @NotNull List<EventInterpretation> eventInterpretations) {
        deleteAll();

        Timestamp timestamp = new Timestamp(new Date().getTime());

        writeEventInterpretations(timestamp, eventInterpretations);
        writeKnownEvents(timestamp, serveRecord.knownEvents());
        writeEfficacyEvidences(timestamp, serveRecord.evidences());
        writeActionableTrials(timestamp, serveRecord.trials());
    }

    private void writeEventInterpretations(@NotNull Timestamp timestamp, @NotNull List<EventInterpretation> eventInterpretations) {
        for (List<EventInterpretation> batch : Iterables.partition(eventInterpretations, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep6 inserter = context.insertInto(EVENTINTERPRETATION,
                    EVENTINTERPRETATION.MODIFIED,
                    EVENTINTERPRETATION.SOURCE,
                    EVENTINTERPRETATION.SOURCEEVENT,
                    EVENTINTERPRETATION.INTERPRETEDGENE,
                    EVENTINTERPRETATION.INTERPRETEDEVENT,
                    EVENTINTERPRETATION.INTERPRETEDEVENTTYPE);
            batch.forEach(entry -> writeEventInterpretationBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeEventInterpretationBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep6 inserter,
            @NotNull EventInterpretation eventInterpretation) {
        inserter.values(timestamp,
                eventInterpretation.source(),
                eventInterpretation.sourceEvent(),
                eventInterpretation.interpretedGene(),
                eventInterpretation.interpretedEvent(),
                eventInterpretation.interpretedEventType());
    }

    private void writeKnownEvents(@NotNull Timestamp timestamp, @NotNull KnownEvents knownEvents) {
        writeKnownHotspots(timestamp, knownEvents.hotspots());
        writeKnownCodons(timestamp, knownEvents.codons());
        writeKnownExons(timestamp, knownEvents.exons());
        writeKnownGenes(timestamp, knownEvents.genes());
        writeKnownCopyNumbers(timestamp, knownEvents.copyNumbers());
        writeKnownFusions(timestamp, knownEvents.fusions());
    }

    private void writeKnownHotspots(@NotNull Timestamp timestamp, @NotNull Set<KnownHotspot> hotspots) {
        for (List<KnownHotspot> batch : Iterables.partition(hotspots, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep12 inserter = context.insertInto(KNOWNHOTSPOT,
                    KNOWNHOTSPOT.MODIFIED,
                    KNOWNHOTSPOT.GENE,
                    KNOWNHOTSPOT.GENEROLE,
                    KNOWNHOTSPOT.PROTEINEFFECT,
                    KNOWNHOTSPOT.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNHOTSPOT.CHROMOSOME,
                    KNOWNHOTSPOT.POSITION,
                    KNOWNHOTSPOT.REF,
                    KNOWNHOTSPOT.ALT,
                    KNOWNHOTSPOT.INPUTTRANSCRIPT,
                    KNOWNHOTSPOT.INPUTPROTEINANNOTATION,
                    KNOWNHOTSPOT.SOURCES);
            batch.forEach(entry -> writeKnownHotspotBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownHotspotBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep12 inserter,
            @NotNull KnownHotspot knownHotspot) {
        inserter.values(timestamp,
                knownHotspot.gene(),
                knownHotspot.geneRole().toString(),
                knownHotspot.proteinEffect().toString(),
                DatabaseUtil.toByte(knownHotspot.associatedWithDrugResistance()),
                knownHotspot.chromosome(),
                knownHotspot.position(),
                knownHotspot.ref(),
                knownHotspot.alt(),
                knownHotspot.inputTranscript(),
                knownHotspot.inputProteinAnnotation(),
                Knowledgebase.toCommaSeparatedSourceString(knownHotspot.sources()));
    }

    private void writeKnownCodons(@NotNull Timestamp timestamp, @NotNull Set<KnownCodon> codons) {
        for (List<KnownCodon> batch : Iterables.partition(codons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep12 inserter = context.insertInto(KNOWNCODON,
                    KNOWNCODON.MODIFIED,
                    KNOWNCODON.GENE,
                    KNOWNCODON.GENEROLE,
                    KNOWNCODON.PROTEINEFFECT,
                    KNOWNCODON.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNCODON.CHROMOSOME,
                    KNOWNCODON.START,
                    KNOWNCODON.END,
                    KNOWNCODON.APPLICABLEMUTATIONTYPE,
                    KNOWNCODON.INPUTTRANSCRIPT,
                    KNOWNCODON.INPUTCODONRANK,
                    KNOWNCODON.SOURCES);
            batch.forEach(entry -> writeKnownCodonBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownCodonBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep12 inserter,
            @NotNull KnownCodon knownCodon) {
        inserter.values(timestamp,
                knownCodon.gene(),
                knownCodon.geneRole().toString(),
                knownCodon.proteinEffect().toString(),
                DatabaseUtil.toByte(knownCodon.associatedWithDrugResistance()),
                knownCodon.chromosome(),
                knownCodon.start(),
                knownCodon.end(),
                knownCodon.applicableMutationType(),
                knownCodon.inputTranscript(),
                knownCodon.inputCodonRank(),
                Knowledgebase.toCommaSeparatedSourceString(knownCodon.sources()));
    }

    private void writeKnownExons(@NotNull Timestamp timestamp, @NotNull Set<KnownExon> exons) {
        for (List<KnownExon> batch : Iterables.partition(exons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep12 inserter = context.insertInto(KNOWNEXON,
                    KNOWNEXON.MODIFIED,
                    KNOWNEXON.GENE,
                    KNOWNEXON.GENEROLE,
                    KNOWNEXON.PROTEINEFFECT,
                    KNOWNEXON.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNEXON.CHROMOSOME,
                    KNOWNEXON.START,
                    KNOWNEXON.END,
                    KNOWNEXON.APPLICABLEMUTATIONTYPE,
                    KNOWNEXON.INPUTTRANSCRIPT,
                    KNOWNEXON.INPUTEXONRANK,
                    KNOWNEXON.SOURCES);
            batch.forEach(entry -> writeKnownExonBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownExonBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep12 inserter,
            @NotNull KnownExon knownExon) {
        inserter.values(timestamp,
                knownExon.gene(),
                knownExon.geneRole().toString(),
                knownExon.proteinEffect().toString(),
                DatabaseUtil.toByte(knownExon.associatedWithDrugResistance()),
                knownExon.chromosome(),
                knownExon.start(),
                knownExon.end(),
                knownExon.applicableMutationType(),
                knownExon.inputTranscript(),
                knownExon.inputExonRank(),
                Knowledgebase.toCommaSeparatedSourceString(knownExon.sources()));
    }

    private void writeKnownGenes(@NotNull Timestamp timestamp, @NotNull Set<KnownGene> genes) {
        for (List<KnownGene> batch : Iterables.partition(genes, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep4 inserter =
                    context.insertInto(KNOWNGENE, KNOWNGENE.MODIFIED, KNOWNGENE.GENE, KNOWNGENE.GENEROLE, KNOWNGENE.SOURCES);
            batch.forEach(entry -> writeKnownGeneBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownGeneBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep4 inserter,
            @NotNull KnownGene knownGene) {
        inserter.values(timestamp,
                knownGene.gene(),
                knownGene.geneRole().toString(),
                Knowledgebase.toCommaSeparatedSourceString(knownGene.sources()));
    }

    private void writeKnownCopyNumbers(@NotNull Timestamp timestamp, @NotNull Set<KnownCopyNumber> copyNumbers) {
        for (List<KnownCopyNumber> batch : Iterables.partition(copyNumbers, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep7 inserter = context.insertInto(KNOWNCOPYNUMBER,
                    KNOWNCOPYNUMBER.MODIFIED,
                    KNOWNCOPYNUMBER.GENE,
                    KNOWNCOPYNUMBER.GENEROLE,
                    KNOWNCOPYNUMBER.PROTEINEFFECT,
                    KNOWNCOPYNUMBER.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNCOPYNUMBER.EVENT,
                    KNOWNCOPYNUMBER.SOURCES);
            batch.forEach(entry -> writeKnownCopyNumberBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownCopyNumberBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep7 inserter,
            @NotNull KnownCopyNumber knownCopyNumber) {
        inserter.values(timestamp,
                knownCopyNumber.gene(),
                knownCopyNumber.geneRole().toString(),
                knownCopyNumber.proteinEffect().toString(),
                DatabaseUtil.toByte(knownCopyNumber.associatedWithDrugResistance()),
                knownCopyNumber.event().toString(),
                Knowledgebase.toCommaSeparatedSourceString(knownCopyNumber.sources()));
    }

    private void writeKnownFusions(@NotNull Timestamp timestamp, @NotNull Set<KnownFusion> fusions) {
        for (List<KnownFusion> batch : Iterables.partition(fusions, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep10 inserter = context.insertInto(KNOWNFUSION,
                    KNOWNFUSION.MODIFIED,
                    KNOWNFUSION.GENEUP,
                    KNOWNFUSION.MINEXONUP,
                    KNOWNFUSION.MAXEXONUP,
                    KNOWNFUSION.GENEDOWN,
                    KNOWNFUSION.MINEXONDOWN,
                    KNOWNFUSION.MAXEXONDOWN,
                    KNOWNFUSION.PROTEINEFFECT,
                    KNOWNFUSION.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNFUSION.SOURCES);
            batch.forEach(entry -> writeKnownFusionBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownFusionBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep10 inserter,
            @NotNull KnownFusion fusion) {
        inserter.values(timestamp,
                fusion.geneUp(),
                fusion.minExonUp(),
                fusion.maxExonUp(),
                fusion.geneDown(),
                fusion.minExonDown(),
                fusion.maxExonDown(),
                fusion.proteinEffect().toString(),
                DatabaseUtil.toByte(fusion.associatedWithDrugResistance()),
                Knowledgebase.toCommaSeparatedSourceString(fusion.sources()));
    }

    private void writeEfficacyEvidences(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> efficacyEvidences) {
        writeHotspotEfficacyEvidence(timestamp, filterEfficacyEvidence(efficacyEvidences, hotspotFilter()));
        writeCodonEfficacyEvidence(timestamp, filterEfficacyEvidence(efficacyEvidences, codonFilter()));
        writeExonEfficacyEvidence(timestamp, filterEfficacyEvidence(efficacyEvidences, exonFilter()));
        writeGeneEfficacyEvidence(timestamp, filterEfficacyEvidence(efficacyEvidences, geneFilter()));
        writeFusionEfficacyEvidence(timestamp, filterEfficacyEvidence(efficacyEvidences, fusionFilter()));
        writeCharacteristicEfficacyEvidence(timestamp, filterEfficacyEvidence(efficacyEvidences, characteristicsFilter()));
        writeHLAEfficacyEvidence(timestamp, filterEfficacyEvidence(efficacyEvidences, hlaFilter()));
    }

    private void writeHotspotEfficacyEvidence(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> evidenceForHotspots) {
        for (List<EfficacyEvidence> batch : Iterables.partition(evidenceForHotspots, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableHotspotInserter();
            batch.forEach(entry -> writeEvidenceForHotspotBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeCodonEfficacyEvidence(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> evidenceForCodons) {
        for (List<EfficacyEvidence> batch : Iterables.partition(evidenceForCodons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableCodonInserter();
            batch.forEach(entry -> writeEvidenceForRangeBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeExonEfficacyEvidence(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> evidenceForExons) {
        for (List<EfficacyEvidence> batch : Iterables.partition(evidenceForExons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableExonInserter();
            batch.forEach(entry -> writeEvidenceForRangeBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeGeneEfficacyEvidence(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> evidenceForGenes) {
        for (List<EfficacyEvidence> batch : Iterables.partition(evidenceForGenes, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableGeneInserter();
            batch.forEach(entry -> writeEvidenceForGeneBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeFusionEfficacyEvidence(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> fusions) {
        for (List<EfficacyEvidence> batch : Iterables.partition(fusions, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableFusionInserter();
            batch.forEach(entry -> writeEvidenceForFusionBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeCharacteristicEfficacyEvidence(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> characteristics) {
        for (List<EfficacyEvidence> batch : Iterables.partition(characteristics, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableCharacteristicInserter();
            batch.forEach(entry -> writeEvidenceForCharacteristicBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeHLAEfficacyEvidence(@NotNull Timestamp timestamp, @NotNull List<EfficacyEvidence> hla) {
        for (List<EfficacyEvidence> batch : Iterables.partition(hla, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableHLAInserter();
            batch.forEach(entry -> writeEvidenceForHLABatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeEvidenceForHotspotBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull EfficacyEvidence evidenceForHotspot) {
        ActionableHotspot hotspot = evidenceForHotspot.molecularCriterium().hotspots().iterator().next();
        inserter.values(timestamp,
                hotspot.gene(),
                hotspot.chromosome(),
                hotspot.position(),
                hotspot.ref(),
                hotspot.alt(),
                evidenceForHotspot.source(),
                hotspot.sourceDate(),
                hotspot.sourceEvent(),
                concat(hotspot.sourceUrls()),
                null,
                null,
                null,
                null,
                null,
                null,
                evidenceForHotspot.treatment().name(),
                !evidenceForHotspot.treatment().treatmentApproachesDrugClass().isEmpty() ? concat(evidenceForHotspot.treatment()
                        .treatmentApproachesDrugClass()) : null,
                !evidenceForHotspot.treatment().treatmentApproachesTherapy().isEmpty() ? concat(evidenceForHotspot.treatment()
                        .treatmentApproachesTherapy()) : null,
                evidenceForHotspot.indication().applicableType().name(),
                evidenceForHotspot.indication().applicableType().doid(),
                concat(toStrings(evidenceForHotspot.indication().excludedSubTypes())),
                evidenceForHotspot.efficacyDescription(),
                evidenceForHotspot.evidenceYear(),
                evidenceForHotspot.evidenceLevel(),
                evidenceForHotspot.evidenceLevelDetails(),
                evidenceForHotspot.evidenceDirection(),
                concat(evidenceForHotspot.urls()));
    }

    private static void writeEvidenceForRangeBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull EfficacyEvidence evidenceForRange) {
        MolecularCriterium molecularCriterium = evidenceForRange.molecularCriterium();
        ActionableRange range;
        if (!molecularCriterium.codons().isEmpty()) {
            range = molecularCriterium.codons().iterator().next();
        } else if (!molecularCriterium.exons().isEmpty()) {
            range = molecularCriterium.exons().iterator().next();
        } else {
            throw new IllegalStateException("Neither codon nor range present in evidence on actionable range: " + molecularCriterium);
        }
        inserter.values(timestamp,
                range.gene(),
                range.chromosome(),
                range.start(),
                range.end(),
                range.applicableMutationType(),
                evidenceForRange.source(),
                range.sourceDate(),
                range.sourceEvent(),
                concat(range.sourceUrls()),
                null,
                null,
                null,
                null,
                null,
                null,
                evidenceForRange.treatment().name(),
                !evidenceForRange.treatment().treatmentApproachesDrugClass().isEmpty() ? concat(evidenceForRange.treatment()
                        .treatmentApproachesDrugClass()) : null,
                !evidenceForRange.treatment().treatmentApproachesTherapy().isEmpty() ? concat(evidenceForRange.treatment()
                        .treatmentApproachesTherapy()) : null,
                evidenceForRange.indication().applicableType().name(),
                evidenceForRange.indication().applicableType().doid(),
                concat(toStrings(evidenceForRange.indication().excludedSubTypes())),
                evidenceForRange.efficacyDescription(),
                evidenceForRange.evidenceYear(),
                evidenceForRange.evidenceLevel(),
                evidenceForRange.evidenceLevelDetails(),
                evidenceForRange.evidenceDirection(),
                concat(evidenceForRange.urls()));
    }

    private static void writeEvidenceForGeneBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull EfficacyEvidence evidenceForGene) {
        ActionableGene gene = evidenceForGene.molecularCriterium().genes().iterator().next();

        inserter.values(timestamp,
                gene.gene(),
                gene.event(),
                evidenceForGene.source(),
                gene.sourceDate(),
                gene.sourceEvent(),
                concat(gene.sourceUrls()),
                null,
                null,
                null,
                null,
                null,
                null,
                evidenceForGene.treatment().name(),
                !evidenceForGene.treatment().treatmentApproachesDrugClass().isEmpty() ? concat(evidenceForGene.treatment()
                        .treatmentApproachesDrugClass()) : null,
                !evidenceForGene.treatment().treatmentApproachesTherapy().isEmpty() ? concat(evidenceForGene.treatment()
                        .treatmentApproachesTherapy()) : null,
                evidenceForGene.indication().applicableType().name(),
                evidenceForGene.indication().applicableType().doid(),
                concat(toStrings(evidenceForGene.indication().excludedSubTypes())),
                evidenceForGene.efficacyDescription(),
                evidenceForGene.evidenceYear(),
                evidenceForGene.evidenceLevel(),
                evidenceForGene.evidenceLevelDetails(),
                evidenceForGene.evidenceDirection(),
                concat(evidenceForGene.urls()));
    }

    private static void writeEvidenceForFusionBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull EfficacyEvidence evidenceForFusion) {
        ActionableFusion fusion = evidenceForFusion.molecularCriterium().fusions().iterator().next();

        inserter.values(timestamp,
                fusion.geneUp(),
                fusion.minExonUp(),
                fusion.maxExonUp(),
                fusion.geneDown(),
                fusion.minExonDown(),
                fusion.maxExonDown(),
                evidenceForFusion.source(),
                fusion.sourceDate(),
                fusion.sourceEvent(),
                concat(fusion.sourceUrls()),
                null,
                null,
                null,
                null,
                null,
                null,
                evidenceForFusion.treatment().name(),
                !evidenceForFusion.treatment().treatmentApproachesDrugClass().isEmpty() ? concat(evidenceForFusion.treatment()
                        .treatmentApproachesDrugClass()) : null,
                !evidenceForFusion.treatment().treatmentApproachesTherapy().isEmpty() ? concat(evidenceForFusion.treatment()
                        .treatmentApproachesTherapy()) : null,
                evidenceForFusion.indication().applicableType().name(),
                evidenceForFusion.indication().applicableType().doid(),
                concat(toStrings(evidenceForFusion.indication().excludedSubTypes())),
                evidenceForFusion.efficacyDescription(),
                evidenceForFusion.evidenceYear(),
                evidenceForFusion.evidenceLevel(),
                evidenceForFusion.evidenceLevelDetails(),
                evidenceForFusion.evidenceDirection(),
                concat(evidenceForFusion.urls()));
    }

    private static void writeEvidenceForCharacteristicBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull EfficacyEvidence evidenceForCharacteristic) {
        ActionableCharacteristic characteristic = evidenceForCharacteristic.molecularCriterium().characteristics().iterator().next();

        inserter.values(timestamp,
                characteristic.type(),
                characteristic.cutoffType(),
                characteristic.cutoff(),
                evidenceForCharacteristic.source(),
                characteristic.sourceDate(),
                characteristic.sourceEvent(),
                concat(characteristic.sourceUrls()),
                null,
                null,
                null,
                null,
                null,
                null,
                evidenceForCharacteristic.treatment().name(),
                !evidenceForCharacteristic.treatment().treatmentApproachesDrugClass().isEmpty()
                        ? concat(evidenceForCharacteristic.treatment().treatmentApproachesDrugClass())
                        : null,
                !evidenceForCharacteristic.treatment().treatmentApproachesTherapy().isEmpty() ? concat(evidenceForCharacteristic.treatment()
                        .treatmentApproachesTherapy()) : null,
                evidenceForCharacteristic.indication().applicableType().name(),
                evidenceForCharacteristic.indication().applicableType().doid(),
                concat(toStrings(evidenceForCharacteristic.indication().excludedSubTypes())),
                evidenceForCharacteristic.efficacyDescription(),
                evidenceForCharacteristic.evidenceYear(),
                evidenceForCharacteristic.evidenceLevel(),
                evidenceForCharacteristic.evidenceLevelDetails(),
                evidenceForCharacteristic.evidenceDirection(),
                concat(evidenceForCharacteristic.urls()));
    }

    private static void writeEvidenceForHLABatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull EfficacyEvidence evidenceForHLA) {
        ActionableHLA hla = evidenceForHLA.molecularCriterium().hla().iterator().next();

        inserter.values(timestamp,
                hla.hlaAllele(),
                evidenceForHLA.source(),
                hla.sourceDate(),
                hla.sourceEvent(),
                concat(hla.sourceUrls()),
                null,
                null,
                null,
                null,
                null,
                null,
                evidenceForHLA.treatment().name(),
                !evidenceForHLA.treatment().treatmentApproachesDrugClass().isEmpty() ? concat(evidenceForHLA.treatment()
                        .treatmentApproachesDrugClass()) : null,
                !evidenceForHLA.treatment().treatmentApproachesTherapy().isEmpty() ? concat(evidenceForHLA.treatment()
                        .treatmentApproachesTherapy()) : null,
                evidenceForHLA.indication().applicableType().name(),
                evidenceForHLA.indication().applicableType().doid(),
                concat(toStrings(evidenceForHLA.indication().excludedSubTypes())),
                evidenceForHLA.efficacyDescription(),
                evidenceForHLA.evidenceYear(),
                evidenceForHLA.evidenceLevel(),
                evidenceForHLA.evidenceLevelDetails(),
                evidenceForHLA.evidenceDirection(),
                concat(evidenceForHLA.urls()));
    }

    private void writeActionableTrials(@NotNull Timestamp timestamp, @NotNull List<ActionableTrial> actionableTrials) {
        writeHotspotActionableTrials(timestamp, filterAndExpandTrials(actionableTrials, hotspotFilter()));
        writeCodonActionableTrials(timestamp, filterAndExpandTrials(actionableTrials, codonFilter()));
        writeExonActionableTrials(timestamp, filterAndExpandTrials(actionableTrials, exonFilter()));
        writeGeneActionableTrials(timestamp, filterAndExpandTrials(actionableTrials, geneFilter()));
        writeFusionActionableTrials(timestamp, filterAndExpandTrials(actionableTrials, fusionFilter()));
        writeCharacteristicActionableTrials(timestamp, filterAndExpandTrials(actionableTrials, characteristicsFilter()));
        writeHLAActionableTrials(timestamp, filterAndExpandTrials(actionableTrials, hlaFilter()));
    }

    private void writeHotspotActionableTrials(@NotNull Timestamp timestamp, @NotNull List<ActionableTrial> trialsForHotspots) {
        for (List<ActionableTrial> batch : Iterables.partition(trialsForHotspots, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableHotspotInserter();
            batch.forEach(entry -> writeTrialsForHotspotBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeCodonActionableTrials(@NotNull Timestamp timestamp, @NotNull List<ActionableTrial> trialsForCodons) {
        for (List<ActionableTrial> batch : Iterables.partition(trialsForCodons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableCodonInserter();
            batch.forEach(entry -> writeTrialsForRangeBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeExonActionableTrials(@NotNull Timestamp timestamp, @NotNull List<ActionableTrial> trialsForExons) {
        for (List<ActionableTrial> batch : Iterables.partition(trialsForExons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableExonInserter();
            batch.forEach(entry -> writeTrialsForRangeBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeGeneActionableTrials(@NotNull Timestamp timestamp, @NotNull List<ActionableTrial> trialsForGenes) {
        for (List<ActionableTrial> batch : Iterables.partition(trialsForGenes, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableGeneInserter();
            batch.forEach(entry -> writeTrialsForGeneBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeFusionActionableTrials(@NotNull Timestamp timestamp, @NotNull List<ActionableTrial> trialsForFusions) {
        for (List<ActionableTrial> batch : Iterables.partition(trialsForFusions, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableFusionInserter();
            batch.forEach(entry -> writeTrialsForFusionBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeCharacteristicActionableTrials(@NotNull Timestamp timestamp,
            @NotNull List<ActionableTrial> trialsForCharacteristics) {
        for (List<ActionableTrial> batch : Iterables.partition(trialsForCharacteristics, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableCharacteristicInserter();
            batch.forEach(entry -> writeTrialsForCharacteristicBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeHLAActionableTrials(@NotNull Timestamp timestamp, @NotNull List<ActionableTrial> trialsForHLA) {
        for (List<ActionableTrial> batch : Iterables.partition(trialsForHLA, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = createActionableHLAInserter();
            batch.forEach(entry -> writeTrialsForHLABatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeTrialsForHotspotBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull ActionableTrial trialForHotspot) {
        // KD: This assumes we have split up trials into one entry per criterium and indication.
        ActionableHotspot hotspot = trialForHotspot.anyMolecularCriteria().iterator().next().hotspots().iterator().next();
        Indication indication = trialForHotspot.indications().iterator().next();

        inserter.values(timestamp,
                hotspot.gene(),
                hotspot.chromosome(),
                hotspot.position(),
                hotspot.ref(),
                hotspot.alt(),
                trialForHotspot.source(),
                hotspot.sourceDate(),
                hotspot.sourceEvent(),
                concat(hotspot.sourceUrls()),
                trialForHotspot.nctId(),
                trialForHotspot.title(),
                trialForHotspot.acronym(),
                trialForHotspot.genderCriterium(),
                toCountryWithCities(trialForHotspot.countries()),
                toHospitals(trialForHotspot.countries()),
                concat(trialForHotspot.therapyNames()),
                null,
                null,
                indication.applicableType().name(),
                indication.applicableType().doid(),
                concat(toStrings(indication.excludedSubTypes())),
                trialForHotspot.title(),
                trialForHotspot.evidenceYear(),
                EvidenceLevel.B,
                EvidenceLevelDetails.CLINICAL_STUDY,
                EvidenceDirection.RESPONSIVE,
                concat(trialForHotspot.urls()));
    }

    private static void writeTrialsForRangeBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull ActionableTrial trialForRange) {
        MolecularCriterium molecularCriterium = trialForRange.anyMolecularCriteria().iterator().next();
        ActionableRange range;
        if (!molecularCriterium.codons().isEmpty()) {
            range = molecularCriterium.codons().iterator().next();
        } else if (!molecularCriterium.exons().isEmpty()) {
            range = molecularCriterium.exons().iterator().next();
        } else {
            throw new IllegalStateException("Neither codon nor range present in trial on actionable range: " + molecularCriterium);
        }
        Indication indication = trialForRange.indications().iterator().next();
        inserter.values(timestamp,
                range.gene(),
                range.chromosome(),
                range.start(),
                range.end(),
                range.applicableMutationType(),
                trialForRange.source(),
                range.sourceDate(),
                range.sourceEvent(),
                concat(range.sourceUrls()),
                trialForRange.nctId(),
                trialForRange.title(),
                trialForRange.acronym(),
                trialForRange.genderCriterium(),
                toCountryWithCities(trialForRange.countries()),
                toHospitals(trialForRange.countries()),
                concat(trialForRange.therapyNames()),
                null,
                null,
                indication.applicableType().name(),
                indication.applicableType().doid(),
                concat(toStrings(indication.excludedSubTypes())),
                trialForRange.title(),
                trialForRange.evidenceYear(),
                EvidenceLevel.B,
                EvidenceLevelDetails.CLINICAL_STUDY,
                EvidenceDirection.RESPONSIVE,
                concat(trialForRange.urls()));
    }

    private static void writeTrialsForGeneBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull ActionableTrial trialForGene) {
        ActionableGene gene = trialForGene.anyMolecularCriteria().iterator().next().genes().iterator().next();
        Indication indication = trialForGene.indications().iterator().next();

        inserter.values(timestamp,
                gene.gene(),
                gene.event(),
                trialForGene.source(),
                gene.sourceDate(),
                gene.sourceEvent(),
                concat(gene.sourceUrls()),
                trialForGene.nctId(),
                trialForGene.title(),
                trialForGene.acronym(),
                trialForGene.genderCriterium(),
                toCountryWithCities(trialForGene.countries()),
                toHospitals(trialForGene.countries()),
                concat(trialForGene.therapyNames()),
                null,
                null,
                indication.applicableType().name(),
                indication.applicableType().doid(),
                concat(toStrings(indication.excludedSubTypes())),
                trialForGene.title(),
                trialForGene.evidenceYear(),
                EvidenceLevel.B,
                EvidenceLevelDetails.CLINICAL_STUDY,
                EvidenceDirection.RESPONSIVE,
                concat(trialForGene.urls()));
    }

    private static void writeTrialsForFusionBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull ActionableTrial trialForFusion) {
        ActionableFusion fusion = trialForFusion.anyMolecularCriteria().iterator().next().fusions().iterator().next();
        Indication indication = trialForFusion.indications().iterator().next();

        inserter.values(timestamp,
                fusion.geneUp(),
                fusion.minExonUp(),
                fusion.maxExonUp(),
                fusion.geneDown(),
                fusion.minExonDown(),
                fusion.maxExonDown(),
                trialForFusion.source(),
                fusion.sourceDate(),
                fusion.sourceEvent(),
                concat(fusion.sourceUrls()),
                trialForFusion.nctId(),
                trialForFusion.title(),
                trialForFusion.acronym(),
                trialForFusion.genderCriterium(),
                toCountryWithCities(trialForFusion.countries()),
                toHospitals(trialForFusion.countries()),
                concat(trialForFusion.therapyNames()),
                null,
                null,
                indication.applicableType().name(),
                indication.applicableType().doid(),
                concat(toStrings(indication.excludedSubTypes())),
                trialForFusion.title(),
                trialForFusion.evidenceYear(),
                EvidenceLevel.B,
                EvidenceLevelDetails.CLINICAL_STUDY,
                EvidenceDirection.RESPONSIVE,
                concat(trialForFusion.urls()));
    }

    private void writeTrialsForCharacteristicBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull ActionableTrial trialForCharacteristic) {
        ActionableCharacteristic characteristic =
                trialForCharacteristic.anyMolecularCriteria().iterator().next().characteristics().iterator().next();
        Indication indication = trialForCharacteristic.indications().iterator().next();

        inserter.values(timestamp,
                characteristic.type(),
                characteristic.cutoffType(),
                characteristic.cutoff(),
                trialForCharacteristic.source(),
                characteristic.sourceDate(),
                characteristic.sourceEvent(),
                concat(characteristic.sourceUrls()),
                trialForCharacteristic.nctId(),
                trialForCharacteristic.title(),
                trialForCharacteristic.acronym(),
                trialForCharacteristic.genderCriterium(),
                toCountryWithCities(trialForCharacteristic.countries()),
                toHospitals(trialForCharacteristic.countries()),
                concat(trialForCharacteristic.therapyNames()),
                null,
                null,
                indication.applicableType().name(),
                indication.applicableType().doid(),
                concat(toStrings(indication.excludedSubTypes())),
                trialForCharacteristic.title(),
                trialForCharacteristic.evidenceYear(),
                EvidenceLevel.B,
                EvidenceLevelDetails.CLINICAL_STUDY,
                EvidenceDirection.RESPONSIVE,
                concat(trialForCharacteristic.urls()));
    }

    private static void writeTrialsForHLABatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull ActionableTrial trialForHLA) {
        ActionableHLA hla = trialForHLA.anyMolecularCriteria().iterator().next().hla().iterator().next();
        Indication indication = trialForHLA.indications().iterator().next();

        inserter.values(timestamp,
                hla.hlaAllele(),
                trialForHLA.source(),
                hla.sourceDate(),
                hla.sourceEvent(),
                concat(hla.sourceUrls()),
                trialForHLA.nctId(),
                trialForHLA.title(),
                trialForHLA.acronym(),
                trialForHLA.genderCriterium(),
                toCountryWithCities(trialForHLA.countries()),
                toHospitals(trialForHLA.countries()),
                concat(trialForHLA.therapyNames()),
                null,
                null,
                indication.applicableType().name(),
                indication.applicableType().doid(),
                concat(toStrings(indication.excludedSubTypes())),
                trialForHLA.title(),
                trialForHLA.evidenceYear(),
                EvidenceLevel.B,
                EvidenceLevelDetails.CLINICAL_STUDY,
                EvidenceDirection.RESPONSIVE,
                concat(trialForHLA.urls()));
    }

    @NotNull
    private InsertValuesStepN createActionableHotspotInserter() {
        return context.insertInto(ACTIONABLEHOTSPOT,
                ACTIONABLEHOTSPOT.MODIFIED,
                ACTIONABLEHOTSPOT.GENE,
                ACTIONABLEHOTSPOT.CHROMOSOME,
                ACTIONABLEHOTSPOT.POSITION,
                ACTIONABLEHOTSPOT.REF,
                ACTIONABLEHOTSPOT.ALT,
                ACTIONABLEHOTSPOT.SOURCE,
                ACTIONABLEHOTSPOT.ENTRYDATE,
                ACTIONABLEHOTSPOT.SOURCEEVENT,
                ACTIONABLEHOTSPOT.SOURCEURLS,
                ACTIONABLEHOTSPOT.NCTID,
                ACTIONABLEHOTSPOT.TITLE,
                ACTIONABLEHOTSPOT.ACRONYM,
                ACTIONABLEHOTSPOT.GENDERCRITERIUM,
                ACTIONABLEHOTSPOT.COUNTRIESANDCITIES,
                ACTIONABLEHOTSPOT.HOSPITALSPERCITY,
                ACTIONABLEHOTSPOT.TREATMENT,
                ACTIONABLEHOTSPOT.TREATMENTAPPROACHESDRUGCLASS,
                ACTIONABLEHOTSPOT.TREATMENTAPPROACHESTHERAPY,
                ACTIONABLEHOTSPOT.APPLICABLECANCERTYPE,
                ACTIONABLEHOTSPOT.APPLICABLEDOID,
                ACTIONABLEHOTSPOT.BLACKLISTCANCERTYPES,
                ACTIONABLEHOTSPOT.EFFICACYDESCRIPTION,
                ACTIONABLEHOTSPOT.EVIDENCEYEAR,
                ACTIONABLEHOTSPOT.EVIDENCELEVEL,
                ACTIONABLEHOTSPOT.EVIDENCELEVELDETAILS,
                ACTIONABLEHOTSPOT.DIRECTION,
                ACTIONABLEHOTSPOT.EVIDENCEURLS);
    }

    @NotNull
    private InsertValuesStepN createActionableCodonInserter() {
        return context.insertInto(ACTIONABLECODON,
                ACTIONABLECODON.MODIFIED,
                ACTIONABLECODON.GENE,
                ACTIONABLECODON.CHROMOSOME,
                ACTIONABLECODON.START,
                ACTIONABLECODON.END,
                ACTIONABLECODON.APPLICABLEMUTATIONTYPE,
                ACTIONABLECODON.SOURCE,
                ACTIONABLECODON.ENTRYDATE,
                ACTIONABLECODON.SOURCEEVENT,
                ACTIONABLECODON.SOURCEURLS,
                ACTIONABLECODON.NCTID,
                ACTIONABLECODON.TITLE,
                ACTIONABLECODON.ACRONYM,
                ACTIONABLECODON.GENDERCRITERIUM,
                ACTIONABLECODON.COUNTRIESANDCITIES,
                ACTIONABLECODON.HOSPITALSPERCITY,
                ACTIONABLECODON.TREATMENT,
                ACTIONABLECODON.TREATMENTAPPROACHESDRUGCLASS,
                ACTIONABLECODON.TREATMENTAPPROACHESTHERAPY,
                ACTIONABLECODON.APPLICABLECANCERTYPE,
                ACTIONABLECODON.APPLICABLEDOID,
                ACTIONABLECODON.BLACKLISTCANCERTYPES,
                ACTIONABLECODON.EFFICACYDESCRIPTION,
                ACTIONABLECODON.EVIDENCEYEAR,
                ACTIONABLECODON.EVIDENCELEVEL,
                ACTIONABLECODON.EVIDENCELEVELDETAILS,
                ACTIONABLECODON.DIRECTION,
                ACTIONABLECODON.EVIDENCEURLS);
    }

    @NotNull
    private InsertValuesStepN createActionableExonInserter() {
        return context.insertInto(ACTIONABLEEXON,
                ACTIONABLEEXON.MODIFIED,
                ACTIONABLEEXON.GENE,
                ACTIONABLEEXON.CHROMOSOME,
                ACTIONABLEEXON.START,
                ACTIONABLEEXON.END,
                ACTIONABLEEXON.APPLICABLEMUTATIONTYPE,
                ACTIONABLEEXON.SOURCE,
                ACTIONABLEEXON.ENTRYDATE,
                ACTIONABLEEXON.SOURCEEVENT,
                ACTIONABLEEXON.SOURCEURLS,
                ACTIONABLEEXON.NCTID,
                ACTIONABLEEXON.TITLE,
                ACTIONABLEEXON.ACRONYM,
                ACTIONABLEEXON.GENDERCRITERIUM,
                ACTIONABLEEXON.COUNTRIESANDCITIES,
                ACTIONABLEEXON.HOSPITALSPERCITY,
                ACTIONABLEEXON.TREATMENT,
                ACTIONABLEEXON.TREATMENTAPPROACHESDRUGCLASS,
                ACTIONABLEEXON.TREATMENTAPPROACHESTHERAPY,
                ACTIONABLEEXON.APPLICABLECANCERTYPE,
                ACTIONABLEEXON.APPLICABLEDOID,
                ACTIONABLEEXON.BLACKLISTCANCERTYPES,
                ACTIONABLEEXON.EFFICACYDESCRIPTION,
                ACTIONABLEEXON.EVIDENCEYEAR,
                ACTIONABLEEXON.EVIDENCELEVEL,
                ACTIONABLEEXON.EVIDENCELEVELDETAILS,
                ACTIONABLEEXON.DIRECTION,
                ACTIONABLEEXON.EVIDENCEURLS);
    }

    @NotNull
    private InsertValuesStepN createActionableGeneInserter() {
        return context.insertInto(ACTIONABLEGENE,
                ACTIONABLEGENE.MODIFIED,
                ACTIONABLEGENE.GENE,
                ACTIONABLEGENE.EVENT,
                ACTIONABLEGENE.SOURCE,
                ACTIONABLEGENE.ENTRYDATE,
                ACTIONABLEGENE.SOURCEEVENT,
                ACTIONABLEGENE.SOURCEURLS,
                ACTIONABLEGENE.NCTID,
                ACTIONABLEGENE.TITLE,
                ACTIONABLEGENE.ACRONYM,
                ACTIONABLEGENE.GENDERCRITERIUM,
                ACTIONABLEGENE.COUNTRIESANDCITIES,
                ACTIONABLEGENE.HOSPITALSPERCITY,
                ACTIONABLEGENE.TREATMENT,
                ACTIONABLEGENE.TREATMENTAPPROACHESDRUGCLASS,
                ACTIONABLEGENE.TREATMENTAPPROACHESTHERAPY,
                ACTIONABLEGENE.APPLICABLECANCERTYPE,
                ACTIONABLEGENE.APPLICABLEDOID,
                ACTIONABLEGENE.BLACKLISTCANCERTYPES,
                ACTIONABLEGENE.EFFICACYDESCRIPTION,
                ACTIONABLEGENE.EVIDENCEYEAR,
                ACTIONABLEGENE.EVIDENCELEVEL,
                ACTIONABLEGENE.EVIDENCELEVELDETAILS,
                ACTIONABLEGENE.DIRECTION,
                ACTIONABLEGENE.EVIDENCEURLS);
    }

    @NotNull
    private InsertValuesStepN createActionableFusionInserter() {
        return context.insertInto(ACTIONABLEFUSION,
                ACTIONABLEFUSION.MODIFIED,
                ACTIONABLEFUSION.GENEUP,
                ACTIONABLEFUSION.MINEXONUP,
                ACTIONABLEFUSION.MAXEXONUP,
                ACTIONABLEFUSION.GENEDOWN,
                ACTIONABLEFUSION.MINEXONDOWN,
                ACTIONABLEFUSION.MAXEXONDOWN,
                ACTIONABLEFUSION.SOURCE,
                ACTIONABLEFUSION.ENTRYDATE,
                ACTIONABLEFUSION.SOURCEEVENT,
                ACTIONABLEFUSION.SOURCEURLS,
                ACTIONABLEFUSION.NCTID,
                ACTIONABLEFUSION.TITLE,
                ACTIONABLEFUSION.ACRONYM,
                ACTIONABLEFUSION.GENDERCRITERIUM,
                ACTIONABLEFUSION.COUNTRIESANDCITIES,
                ACTIONABLEFUSION.HOSPITALSPERCITY,
                ACTIONABLEFUSION.TREATMENT,
                ACTIONABLEFUSION.TREATMENTAPPROACHESDRUGCLASS,
                ACTIONABLEFUSION.TREATMENTAPPROACHESTHERAPY,
                ACTIONABLEFUSION.APPLICABLECANCERTYPE,
                ACTIONABLEFUSION.APPLICABLEDOID,
                ACTIONABLEFUSION.BLACKLISTCANCERTYPES,
                ACTIONABLEFUSION.EFFICACYDESCRIPTION,
                ACTIONABLEFUSION.EVIDENCEYEAR,
                ACTIONABLEFUSION.EVIDENCELEVEL,
                ACTIONABLEFUSION.EVIDENCELEVELDETAILS,
                ACTIONABLEFUSION.DIRECTION,
                ACTIONABLEFUSION.EVIDENCEURLS);
    }

    @NotNull
    private InsertValuesStepN createActionableCharacteristicInserter() {
        return context.insertInto(ACTIONABLECHARACTERISTIC,
                ACTIONABLECHARACTERISTIC.MODIFIED,
                ACTIONABLECHARACTERISTIC.TYPE,
                ACTIONABLECHARACTERISTIC.CUTOFFTYPE,
                ACTIONABLECHARACTERISTIC.CUTOFF,
                ACTIONABLECHARACTERISTIC.SOURCE,
                ACTIONABLECHARACTERISTIC.ENTRYDATE,
                ACTIONABLECHARACTERISTIC.SOURCEEVENT,
                ACTIONABLECHARACTERISTIC.SOURCEURLS,
                ACTIONABLECHARACTERISTIC.NCTID,
                ACTIONABLECHARACTERISTIC.TITLE,
                ACTIONABLECHARACTERISTIC.ACRONYM,
                ACTIONABLECHARACTERISTIC.GENDERCRITERIUM,
                ACTIONABLECHARACTERISTIC.COUNTRIESANDCITIES,
                ACTIONABLECHARACTERISTIC.HOSPITALSPERCITY,
                ACTIONABLECHARACTERISTIC.TREATMENT,
                ACTIONABLECHARACTERISTIC.TREATMENTAPPROACHESDRUGCLASS,
                ACTIONABLECHARACTERISTIC.TREATMENTAPPROACHESTHERAPY,
                ACTIONABLECHARACTERISTIC.APPLICABLECANCERTYPE,
                ACTIONABLECHARACTERISTIC.APPLICABLEDOID,
                ACTIONABLECHARACTERISTIC.BLACKLISTCANCERTYPES,
                ACTIONABLECHARACTERISTIC.EFFICACYDESCRIPTION,
                ACTIONABLECHARACTERISTIC.EVIDENCEYEAR,
                ACTIONABLECHARACTERISTIC.EVIDENCELEVEL,
                ACTIONABLECHARACTERISTIC.EVIDENCELEVELDETAILS,
                ACTIONABLECHARACTERISTIC.DIRECTION,
                ACTIONABLECHARACTERISTIC.EVIDENCEURLS);
    }

    @NotNull
    private InsertValuesStepN createActionableHLAInserter() {
        return context.insertInto(ACTIONABLEHLA,
                ACTIONABLEHLA.MODIFIED,
                ACTIONABLEHLA.HLAALLELE,
                ACTIONABLEHLA.SOURCE,
                ACTIONABLEHLA.ENTRYDATE,
                ACTIONABLEHLA.SOURCEEVENT,
                ACTIONABLEHLA.SOURCEURLS,
                ACTIONABLEHLA.NCTID,
                ACTIONABLEHLA.TITLE,
                ACTIONABLEHLA.ACRONYM,
                ACTIONABLEHLA.GENDERCRITERIUM,
                ACTIONABLEHLA.COUNTRIESANDCITIES,
                ACTIONABLEHLA.HOSPITALSPERCITY,
                ACTIONABLEHLA.TREATMENT,
                ACTIONABLEHLA.TREATMENTAPPROACHESDRUGCLASS,
                ACTIONABLEHLA.TREATMENTAPPROACHESTHERAPY,
                ACTIONABLEHLA.APPLICABLECANCERTYPE,
                ACTIONABLEHLA.APPLICABLEDOID,
                ACTIONABLEHLA.BLACKLISTCANCERTYPES,
                ACTIONABLEHLA.EFFICACYDESCRIPTION,
                ACTIONABLEHLA.EVIDENCEYEAR,
                ACTIONABLEHLA.EVIDENCELEVEL,
                ACTIONABLEHLA.EVIDENCELEVELDETAILS,
                ACTIONABLEHLA.DIRECTION,
                ACTIONABLEHLA.EVIDENCEURLS);
    }

    @NotNull
    private static Predicate<MolecularCriterium> hotspotFilter() {
        return molecularCriterium -> !molecularCriterium.hotspots().isEmpty();
    }

    @NotNull
    private static Predicate<MolecularCriterium> codonFilter() {
        return molecularCriterium -> !molecularCriterium.codons().isEmpty();
    }

    @NotNull
    private static Predicate<MolecularCriterium> exonFilter() {
        return molecularCriterium -> !molecularCriterium.exons().isEmpty();
    }

    @NotNull
    private static Predicate<MolecularCriterium> geneFilter() {
        return molecularCriterium -> !molecularCriterium.genes().isEmpty();
    }

    @NotNull
    private static Predicate<MolecularCriterium> fusionFilter() {
        return molecularCriterium -> !molecularCriterium.fusions().isEmpty();
    }

    @NotNull
    private static Predicate<MolecularCriterium> characteristicsFilter() {
        return molecularCriterium -> !molecularCriterium.characteristics().isEmpty();
    }

    @NotNull
    private static Predicate<MolecularCriterium> hlaFilter() {
        return molecularCriterium -> !molecularCriterium.hla().isEmpty();
    }

    @NotNull
    private static List<EfficacyEvidence> filterEfficacyEvidence(@NotNull List<EfficacyEvidence> evidences,
            @NotNull Predicate<MolecularCriterium> molecularCriteriumPredicate) {
        return evidences.stream()
                .filter(evidence -> molecularCriteriumPredicate.test(evidence.molecularCriterium()))
                .collect(Collectors.toList());
    }

    @NotNull
    private static List<ActionableTrial> filterAndExpandTrials(@NotNull List<ActionableTrial> trials,
            @NotNull Predicate<MolecularCriterium> molecularCriteriumPredicate) {
        List<ActionableTrial> expandedFilteredTrials = Lists.newArrayList();
        for (ActionableTrial trial : trials) {
            for (MolecularCriterium criterium : trial.anyMolecularCriteria()) {
                if (molecularCriteriumPredicate.test(criterium)) {
                    expandedFilteredTrials.addAll(expandWithIndicationAndCriterium(trial, criterium));
                }
            }
        }
        return expandedFilteredTrials;
    }

    @NotNull
    private static List<ActionableTrial> expandWithIndicationAndCriterium(@NotNull ActionableTrial baseTrial,
            @NotNull MolecularCriterium criterium) {
        List<ActionableTrial> expandedTrials = Lists.newArrayList();
        ImmutableActionableTrial.Builder trialBuilder =
                ImmutableActionableTrial.builder().from(baseTrial).anyMolecularCriteria(List.of(criterium));

        for (Indication indication : baseTrial.indications()) {
            expandedTrials.add(trialBuilder.indications(List.of(indication)).build());
        }
        return expandedTrials;
    }

    @NotNull
    private static Set<String> toStrings(@NotNull Set<CancerType> cancerTypes) {
        Set<String> strings = Sets.newHashSet();
        for (CancerType cancerType : cancerTypes) {
            strings.add(cancerType.name() + SUB_JOINER + cancerType.doid());
        }
        return strings;
    }

    @NotNull
    private static String toCountryWithCities(@NotNull Set<Country> countries) {
        return countries.stream()
                .map(country -> country.countryName() + "(" + String.join(SUB_JOINER, country.hospitalsPerCity().keySet()) + ")")
                .collect(Collectors.joining(MAIN_JOINER));
    }

    @NotNull
    private static String toHospitals(@NotNull Set<Country> countries) {
        StringJoiner joiner = new StringJoiner(MAIN_JOINER);
        for (Country country : countries) {
            for (Map.Entry<String, Set<Hospital>> entry : country.hospitalsPerCity().entrySet()) {
                String city = entry.getKey();
                Set<String> hospitalNames = entry.getValue().stream().map(Hospital::name).collect(Collectors.toSet());
                joiner.add(city + "(" + String.join(SUB_JOINER, hospitalNames) + ")");
            }
        }
        return joiner.toString();
    }

    @NotNull
    private static String concat(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(MAIN_JOINER);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }
}
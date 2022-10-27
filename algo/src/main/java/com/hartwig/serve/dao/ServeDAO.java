package com.hartwig.serve.dao;

import static com.hartwig.serve.database.tables.Actionablecharacteristic.ACTIONABLECHARACTERISTIC;
import static com.hartwig.serve.database.tables.Actionablefusion.ACTIONABLEFUSION;
import static com.hartwig.serve.database.tables.Actionablegene.ACTIONABLEGENE;
import static com.hartwig.serve.database.tables.Actionablehla.ACTIONABLEHLA;
import static com.hartwig.serve.database.tables.Actionablehotspot.ACTIONABLEHOTSPOT;
import static com.hartwig.serve.database.tables.Actionablerange.ACTIONABLERANGE;
import static com.hartwig.serve.database.tables.Eventinterpretation.EVENTINTERPRETATION;
import static com.hartwig.serve.database.tables.Knowncodon.KNOWNCODON;
import static com.hartwig.serve.database.tables.Knowncopynumber.KNOWNCOPYNUMBER;
import static com.hartwig.serve.database.tables.Knownexon.KNOWNEXON;
import static com.hartwig.serve.database.tables.Knownfusionpair.KNOWNFUSIONPAIR;
import static com.hartwig.serve.database.tables.Knownhotspot.KNOWNHOTSPOT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvents;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusionPair;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.extraction.events.EventInterpretation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep11;
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep16;
import org.jooq.InsertValuesStep17;
import org.jooq.InsertValuesStep20;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep9;
import org.jooq.InsertValuesStepN;

@SuppressWarnings({ "unchecked", "ResultOfMethodCallIgnored" })
public class ServeDAO {

    private static final Logger LOGGER = LogManager.getLogger(ServeDAO.class);

    private static final String MAIN_JOINER = ",";
    private static final String NAME_DOID_JOINER = ";";

    @NotNull
    private final DSLContext context;

    ServeDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        LOGGER.info("Deleting all data from SERVE database");
        context.deleteFrom(ACTIONABLEHOTSPOT).execute();
        context.deleteFrom(ACTIONABLERANGE).execute();
        context.deleteFrom(ACTIONABLEGENE).execute();
        context.deleteFrom(ACTIONABLEFUSION).execute();
        context.deleteFrom(ACTIONABLECHARACTERISTIC).execute();
        context.deleteFrom(ACTIONABLEHLA).execute();
        context.deleteFrom(KNOWNHOTSPOT).execute();
        context.deleteFrom(KNOWNCODON).execute();
        context.deleteFrom(KNOWNEXON).execute();
        context.deleteFrom(KNOWNFUSIONPAIR).execute();
        context.deleteFrom(KNOWNCOPYNUMBER).execute();
        context.deleteFrom(EVENTINTERPRETATION).execute();
    }

    void write(@NotNull ActionableEvents actionableEvents, @NotNull KnownEvents knownEvents,
            @NotNull List<EventInterpretation> eventInterpretations) {
        deleteAll();

        Timestamp timestamp = new Timestamp(new Date().getTime());

        writeActionableHotspots(timestamp, actionableEvents.hotspots());
        writeActionableRanges(timestamp, actionableEvents.ranges());
        writeActionableGenes(timestamp, actionableEvents.genes());
        writeActionableFusions(timestamp, actionableEvents.fusions());
        writeActionableCharacteristics(timestamp, actionableEvents.characteristics());
        writeActionableHLA(timestamp, actionableEvents.hla());

        writeKnownHotspots(timestamp, knownEvents.hotspots());
        writeKnownCodons(timestamp, knownEvents.codons());
        writeKnownExons(timestamp, knownEvents.exons());
        writeKnownFusionPairs(timestamp, knownEvents.fusionPairs());
        writeKnownCopyNumbers(timestamp, knownEvents.copyNumbers());

        writeEventInterpretations(timestamp, eventInterpretations);
    }

    private void writeActionableHotspots(@NotNull Timestamp timestamp, @NotNull List<ActionableHotspot> hotspots) {
        for (List<ActionableHotspot> batch : Iterables.partition(hotspots, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep20 inserter = context.insertInto(ACTIONABLEHOTSPOT,
                    ACTIONABLEHOTSPOT.MODIFIED,
                    ACTIONABLEHOTSPOT.CHROMOSOME,
                    ACTIONABLEHOTSPOT.POSITION,
                    ACTIONABLEHOTSPOT.REF,
                    ACTIONABLEHOTSPOT.ALT,
                    ACTIONABLEHOTSPOT.GENE,
                    ACTIONABLEHOTSPOT.GENEROLE,
                    ACTIONABLEHOTSPOT.PROTEINEFFECT,
                    ACTIONABLEHOTSPOT.SOURCE,
                    ACTIONABLEHOTSPOT.SOURCEEVENT,
                    ACTIONABLEHOTSPOT.SOURCEURLS,
                    ACTIONABLEHOTSPOT.TREATMENT,
                    ACTIONABLEHOTSPOT.SOURCETREATMENTAPPROACH,
                    ACTIONABLEHOTSPOT.TREATMENTAPPROACH,
                    ACTIONABLEHOTSPOT.APPLICABLECANCERTYPE,
                    ACTIONABLEHOTSPOT.APPLICABLEDOID,
                    ACTIONABLEHOTSPOT.BLACKLISTCANCERTYPES,
                    ACTIONABLEHOTSPOT.LEVEL,
                    ACTIONABLEHOTSPOT.DIRECTION,
                    ACTIONABLEHOTSPOT.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableHotspotBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableHotspotBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep20 inserter,
            @NotNull ActionableHotspot actionableHotspot) {
        inserter.values(timestamp,
                actionableHotspot.chromosome(),
                actionableHotspot.position(),
                actionableHotspot.ref(),
                actionableHotspot.alt(),
                actionableHotspot.gene(),
                actionableHotspot.geneRole().toString(),
                actionableHotspot.proteinEffect().toString(),
                actionableHotspot.source(),
                actionableHotspot.sourceEvent(),
                concat(actionableHotspot.sourceUrls()),
                actionableHotspot.treatment().name(),
                concat(actionableHotspot.treatment().sourceRelevantTreatmentApproaches()),
                concat(actionableHotspot.treatment().relevantTreatmentApproaches()),
                actionableHotspot.applicableCancerType().name(),
                actionableHotspot.applicableCancerType().doid(),
                concat(toStrings(actionableHotspot.blacklistCancerTypes())),
                actionableHotspot.level(),
                actionableHotspot.direction(),
                concat(actionableHotspot.evidenceUrls()).isEmpty());
    }

    private void writeActionableRanges(final Timestamp timestamp, final List<ActionableRange> ranges) {
        for (List<ActionableRange> batch : Iterables.partition(ranges, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStepN inserter = context.insertInto(ACTIONABLERANGE,
                    ACTIONABLERANGE.MODIFIED,
                    ACTIONABLERANGE.GENE,
                    ACTIONABLERANGE.GENEROLE,
                    ACTIONABLERANGE.PROTEINEFFECT,
                    ACTIONABLERANGE.TRANSCRIPT,
                    ACTIONABLERANGE.CHROMOSOME,
                    ACTIONABLERANGE.START,
                    ACTIONABLERANGE.END,
                    ACTIONABLERANGE.APPLICABLEMUTATIONTYPE,
                    ACTIONABLERANGE.RANGETYPE,
                    ACTIONABLERANGE.RANGERANK,
                    ACTIONABLERANGE.SOURCE,
                    ACTIONABLERANGE.SOURCEEVENT,
                    ACTIONABLERANGE.SOURCEURLS,
                    ACTIONABLERANGE.TREATMENT,
                    ACTIONABLERANGE.SOURCETREATMENTAPPROACH,
                    ACTIONABLERANGE.TREATMENTAPPROACH,
                    ACTIONABLERANGE.APPLICABLECANCERTYPE,
                    ACTIONABLERANGE.APPLICABLEDOID,
                    ACTIONABLERANGE.BLACKLISTCANCERTYPES,
                    ACTIONABLERANGE.LEVEL,
                    ACTIONABLERANGE.DIRECTION,
                    ACTIONABLERANGE.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableRangeBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableRangeBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStepN inserter,
            @NotNull ActionableRange actionableRange) {
        inserter.values(timestamp,
                actionableRange.gene(),
                actionableRange.geneRole().toString(),
                actionableRange.proteinEffect().toString(),
                actionableRange.transcript(),
                actionableRange.chromosome(),
                actionableRange.start(),
                actionableRange.end(),
                actionableRange.applicableMutationType(),
                actionableRange.rangeType(),
                actionableRange.rank(),
                actionableRange.source(),
                actionableRange.sourceEvent(),
                concat(actionableRange.sourceUrls()),
                actionableRange.treatment().name(),
                concat(actionableRange.treatment().sourceRelevantTreatmentApproaches()),
                concat(actionableRange.treatment().relevantTreatmentApproaches()),
                actionableRange.applicableCancerType().name(),
                actionableRange.applicableCancerType().doid(),
                concat(toStrings(actionableRange.blacklistCancerTypes())),
                actionableRange.level(),
                actionableRange.direction(),
                concat(actionableRange.evidenceUrls()));
    }

    private void writeActionableGenes(@NotNull Timestamp timestamp, @NotNull List<ActionableGene> genes) {
        for (List<ActionableGene> batch : Iterables.partition(genes, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep17 inserter = context.insertInto(ACTIONABLEGENE,
                    ACTIONABLEGENE.MODIFIED,
                    ACTIONABLEGENE.GENE,
                    ACTIONABLEGENE.GENEROLE,
                    ACTIONABLEGENE.PROTEINEFFECT,
                    ACTIONABLEGENE.EVENT,
                    ACTIONABLEGENE.SOURCE,
                    ACTIONABLEGENE.SOURCEEVENT,
                    ACTIONABLEGENE.SOURCEURLS,
                    ACTIONABLEGENE.TREATMENT,
                    ACTIONABLEGENE.SOURCETREATMENTAPPROACH,
                    ACTIONABLEGENE.TREATMENTAPPROACH,
                    ACTIONABLEGENE.APPLICABLECANCERTYPE,
                    ACTIONABLEGENE.APPLICABLEDOID,
                    ACTIONABLEGENE.BLACKLISTCANCERTYPES,
                    ACTIONABLEGENE.LEVEL,
                    ACTIONABLEGENE.DIRECTION,
                    ACTIONABLEGENE.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableGeneBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableGeneBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep17 inserter,
            @NotNull ActionableGene actionableGene) {
        inserter.values(timestamp,
                actionableGene.gene(),
                actionableGene.geneRole().toString(),
                actionableGene.proteinEffect().toString(),
                actionableGene.event(),
                actionableGene.source(),
                actionableGene.sourceEvent(),
                concat(actionableGene.sourceUrls()),
                actionableGene.treatment().name(),
                concat(actionableGene.treatment().sourceRelevantTreatmentApproaches()),
                concat(actionableGene.treatment().relevantTreatmentApproaches()),
                actionableGene.applicableCancerType().name(),
                actionableGene.applicableCancerType().doid(),
                concat(toStrings(actionableGene.blacklistCancerTypes())),
                actionableGene.level(),
                actionableGene.direction(),
                concat(actionableGene.evidenceUrls()));
    }

    private void writeActionableFusions(@NotNull Timestamp timestamp, @NotNull List<ActionableFusion> fusions) {
        for (List<ActionableFusion> batch : Iterables.partition(fusions, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep20 inserter = context.insertInto(ACTIONABLEFUSION,
                    ACTIONABLEFUSION.MODIFIED,
                    ACTIONABLEFUSION.GENEUP,
                    ACTIONABLEFUSION.MINEXONUP,
                    ACTIONABLEFUSION.MAXEXONUP,
                    ACTIONABLEFUSION.GENEDOWN,
                    ACTIONABLEFUSION.MINEXONDOWN,
                    ACTIONABLEFUSION.MAXEXONDOWN,
                    ACTIONABLEFUSION.PROTEINEFFECT,
                    ACTIONABLEFUSION.SOURCE,
                    ACTIONABLEFUSION.SOURCEEVENT,
                    ACTIONABLEFUSION.SOURCEURLS,
                    ACTIONABLEFUSION.TREATMENT,
                    ACTIONABLEFUSION.SOURCETREATMENTAPPROACH,
                    ACTIONABLEFUSION.TREATMENTAPPROACH,
                    ACTIONABLEFUSION.APPLICABLECANCERTYPE,
                    ACTIONABLEFUSION.APPLICABLEDOID,
                    ACTIONABLEFUSION.BLACKLISTCANCERTYPES,
                    ACTIONABLEFUSION.LEVEL,
                    ACTIONABLEFUSION.DIRECTION,
                    ACTIONABLEFUSION.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableFusionBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableFusionBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep20 inserter,
            @NotNull ActionableFusion actionableFusion) {
        inserter.values(timestamp,
                actionableFusion.geneUp(),
                actionableFusion.minExonUp(),
                actionableFusion.maxExonUp(),
                actionableFusion.geneDown(),
                actionableFusion.minExonDown(),
                actionableFusion.maxExonDown(),
                actionableFusion.proteinEffect().toString(),
                actionableFusion.source(),
                actionableFusion.sourceEvent(),
                concat(actionableFusion.sourceUrls()),
                actionableFusion.treatment().name(),
                concat(actionableFusion.treatment().sourceRelevantTreatmentApproaches()),
                concat(actionableFusion.treatment().relevantTreatmentApproaches()),
                actionableFusion.applicableCancerType().name(),
                actionableFusion.applicableCancerType().doid(),
                concat(toStrings(actionableFusion.blacklistCancerTypes())),
                actionableFusion.level(),
                actionableFusion.direction(),
                concat(actionableFusion.evidenceUrls()));
    }

    private void writeActionableCharacteristics(@NotNull Timestamp timestamp, @NotNull List<ActionableCharacteristic> characteristics) {
        for (List<ActionableCharacteristic> batch : Iterables.partition(characteristics, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep16 inserter = context.insertInto(ACTIONABLECHARACTERISTIC,
                    ACTIONABLECHARACTERISTIC.MODIFIED,
                    ACTIONABLECHARACTERISTIC.TYPE,
                    ACTIONABLECHARACTERISTIC.CUTOFFTYPE,
                    ACTIONABLECHARACTERISTIC.CUTOFF,
                    ACTIONABLECHARACTERISTIC.SOURCE,
                    ACTIONABLECHARACTERISTIC.SOURCEEVENT,
                    ACTIONABLECHARACTERISTIC.SOURCEURLS,
                    ACTIONABLECHARACTERISTIC.TREATMENT,
                    ACTIONABLECHARACTERISTIC.SOURCETREATMENTAPPROACH,
                    ACTIONABLECHARACTERISTIC.TREATMENTAPPROACH,
                    ACTIONABLECHARACTERISTIC.APPLICABLECANCERTYPE,
                    ACTIONABLECHARACTERISTIC.APPLICABLEDOID,
                    ACTIONABLECHARACTERISTIC.BLACKLISTCANCERTYPES,
                    ACTIONABLECHARACTERISTIC.LEVEL,
                    ACTIONABLECHARACTERISTIC.DIRECTION,
                    ACTIONABLECHARACTERISTIC.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableCharacteristicBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableCharacteristicBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep16 inserter,
            @NotNull ActionableCharacteristic actionableCharacteristic) {
        inserter.values(timestamp,
                actionableCharacteristic.type(),
                actionableCharacteristic.cutoffType(),
                actionableCharacteristic.cutoff(),
                actionableCharacteristic.source(),
                actionableCharacteristic.sourceEvent(),
                concat(actionableCharacteristic.sourceUrls()),
                actionableCharacteristic.treatment().name(),
                concat(actionableCharacteristic.treatment().sourceRelevantTreatmentApproaches()),
                concat(actionableCharacteristic.treatment().relevantTreatmentApproaches()),
                actionableCharacteristic.applicableCancerType().name(),
                actionableCharacteristic.applicableCancerType().doid(),
                concat(toStrings(actionableCharacteristic.blacklistCancerTypes())),
                actionableCharacteristic.level(),
                actionableCharacteristic.direction(),
                concat(actionableCharacteristic.evidenceUrls()));
    }

    private void writeActionableHLA(@NotNull Timestamp timestamp, @NotNull List<ActionableHLA> hla) {
        for (List<ActionableHLA> batch : Iterables.partition(hla, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep14 inserter = context.insertInto(ACTIONABLEHLA,
                    ACTIONABLEHLA.MODIFIED,
                    ACTIONABLEHLA.HLAALLELE,
                    ACTIONABLEHLA.SOURCE,
                    ACTIONABLEHLA.SOURCEEVENT,
                    ACTIONABLEHLA.SOURCEURLS,
                    ACTIONABLEHLA.TREATMENT,
                    ACTIONABLEHLA.SOURCETREATMENTAPPROACH,
                    ACTIONABLEHLA.TREATMENTAPPROACH,
                    ACTIONABLEHLA.APPLICABLECANCERTYPE,
                    ACTIONABLEHLA.APPLICABLEDOID,
                    ACTIONABLEHLA.BLACKLISTCANCERTYPES,
                    ACTIONABLEHLA.LEVEL,
                    ACTIONABLEHLA.DIRECTION,
                    ACTIONABLEHLA.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableHLABatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableHLABatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep14 inserter,
            @NotNull ActionableHLA actionableHLA) {
        inserter.values(timestamp,
                actionableHLA.hlaAllele(),
                actionableHLA.source(),
                actionableHLA.sourceEvent(),
                concat(actionableHLA.sourceUrls()),
                actionableHLA.treatment().name(),
                concat(actionableHLA.treatment().sourceRelevantTreatmentApproaches()),
                concat(actionableHLA.treatment().relevantTreatmentApproaches()),
                actionableHLA.applicableCancerType().name(),
                actionableHLA.applicableCancerType().doid(),
                concat(toStrings(actionableHLA.blacklistCancerTypes())),
                actionableHLA.level(),
                actionableHLA.direction(),
                concat(actionableHLA.evidenceUrls()));
    }

    private void writeKnownHotspots(@NotNull Timestamp timestamp, @NotNull Set<KnownHotspot> hotspots) {
        for (List<KnownHotspot> batch : Iterables.partition(hotspots, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep11 inserter = context.insertInto(KNOWNHOTSPOT,
                    KNOWNHOTSPOT.MODIFIED,
                    KNOWNHOTSPOT.CHROMOSOME,
                    KNOWNHOTSPOT.POSITION,
                    KNOWNHOTSPOT.REF,
                    KNOWNHOTSPOT.ALT,
                    KNOWNHOTSPOT.GENE,
                    KNOWNHOTSPOT.GENEROLE,
                    KNOWNHOTSPOT.PROTEINEFFECT,
                    KNOWNHOTSPOT.TRANSCRIPT,
                    KNOWNHOTSPOT.PROTEINANNOTATION,
                    KNOWNHOTSPOT.SOURCES);
            batch.forEach(entry -> writeKnownHotspotBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownHotspotBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep11 inserter,
            @NotNull KnownHotspot knownHotspot) {
        inserter.values(timestamp,
                knownHotspot.chromosome(),
                knownHotspot.position(),
                knownHotspot.ref(),
                knownHotspot.alt(),
                knownHotspot.gene(),
                knownHotspot.geneRole().toString(),
                knownHotspot.proteinEffect().toString(),
                knownHotspot.transcript(),
                knownHotspot.proteinAnnotation(),
                Knowledgebase.toCommaSeparatedSourceString(knownHotspot.sources()));
    }

    private void writeKnownCodons(@NotNull Timestamp timestamp, @NotNull Set<KnownCodon> codons) {
        for (List<KnownCodon> batch : Iterables.partition(codons, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep11 inserter = context.insertInto(KNOWNCODON,
                    KNOWNCODON.MODIFIED,
                    KNOWNCODON.GENE,
                    KNOWNCODON.GENEROLE,
                    KNOWNCODON.PROTEINEFFECT,
                    KNOWNCODON.TRANSCRIPT,
                    KNOWNCODON.CHROMOSOME,
                    KNOWNCODON.START,
                    KNOWNCODON.END,
                    KNOWNCODON.APPLICABLEMUTATIONTYPE,
                    KNOWNCODON.CODONRANK,
                    KNOWNCODON.SOURCES);
            batch.forEach(entry -> writeKnownCodonBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownCodonBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep11 inserter,
            @NotNull KnownCodon knownCodon) {
        inserter.values(timestamp,
                knownCodon.annotation().gene(),
                knownCodon.annotation().geneRole().toString(),
                knownCodon.annotation().proteinEffect().toString(),
                knownCodon.annotation().transcript(),
                knownCodon.annotation().chromosome(),
                knownCodon.annotation().start(),
                knownCodon.annotation().end(),
                knownCodon.annotation().applicableMutationType(),
                knownCodon.annotation().rank(),
                Knowledgebase.toCommaSeparatedSourceString(knownCodon.sources()));
    }

    private void writeKnownExons(@NotNull Timestamp timestamp, @NotNull Set<KnownExon> exons) {
        for (List<KnownExon> batch : Iterables.partition(exons, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep11 inserter = context.insertInto(KNOWNEXON,
                    KNOWNEXON.MODIFIED,
                    KNOWNEXON.GENE,
                    KNOWNEXON.GENEROLE,
                    KNOWNEXON.PROTEINEFFECT,
                    KNOWNEXON.TRANSCRIPT,
                    KNOWNEXON.CHROMOSOME,
                    KNOWNEXON.START,
                    KNOWNEXON.END,
                    KNOWNEXON.APPLICABLEMUTATIONTYPE,
                    KNOWNEXON.EXONRANK,
                    KNOWNEXON.SOURCES);
            batch.forEach(entry -> writeKnownExonBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownExonBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep11 inserter,
            @NotNull KnownExon knownExon) {
        inserter.values(timestamp,
                knownExon.annotation().gene(),
                knownExon.annotation().geneRole().toString(),
                knownExon.annotation().proteinEffect().toString(),
                knownExon.annotation().transcript(),
                knownExon.annotation().chromosome(),
                knownExon.annotation().start(),
                knownExon.annotation().end(),
                knownExon.annotation().applicableMutationType(),
                knownExon.annotation().rank(),
                Knowledgebase.toCommaSeparatedSourceString(knownExon.sources()));
    }

    private void writeKnownFusionPairs(@NotNull Timestamp timestamp, @NotNull Set<KnownFusionPair> fusionPairs) {
        for (List<KnownFusionPair> batch : Iterables.partition(fusionPairs, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep9 inserter = context.insertInto(KNOWNFUSIONPAIR,
                    KNOWNFUSIONPAIR.MODIFIED,
                    KNOWNFUSIONPAIR.GENEUP,
                    KNOWNFUSIONPAIR.MINEXONUP,
                    KNOWNFUSIONPAIR.MAXEXONUP,
                    KNOWNFUSIONPAIR.GENEDOWN,
                    KNOWNFUSIONPAIR.MINEXONDOWN,
                    KNOWNFUSIONPAIR.MAXEXONDOWN,
                    KNOWNFUSIONPAIR.PROTEINEFFECT,
                    KNOWNFUSIONPAIR.SOURCES);
            batch.forEach(entry -> writeKnownFusionPairBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownFusionPairBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep9 inserter,
            @NotNull KnownFusionPair knownFusionPair) {
        inserter.values(timestamp,
                knownFusionPair.geneUp(),
                knownFusionPair.minExonUp(),
                knownFusionPair.maxExonUp(),
                knownFusionPair.geneDown(),
                knownFusionPair.minExonDown(),
                knownFusionPair.maxExonDown(),
                knownFusionPair.proteinEffect().toString(),
                Knowledgebase.toCommaSeparatedSourceString(knownFusionPair.sources()));
    }

    private void writeKnownCopyNumbers(@NotNull Timestamp timestamp, @NotNull Set<KnownCopyNumber> copyNumbers) {
        for (List<KnownCopyNumber> batch : Iterables.partition(copyNumbers, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep6 inserter = context.insertInto(KNOWNCOPYNUMBER,
                    KNOWNCOPYNUMBER.MODIFIED,
                    KNOWNCOPYNUMBER.GENE,
                    KNOWNCOPYNUMBER.GENEROLE,
                    KNOWNCOPYNUMBER.PROTEINEFFECT,
                    KNOWNCOPYNUMBER.TYPE,
                    KNOWNCOPYNUMBER.SOURCES);
            batch.forEach(entry -> writeKnownCopyNumberBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }
    private static void writeKnownCopyNumberBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep6 inserter,
            @NotNull KnownCopyNumber knownCopyNumber) {
        inserter.values(timestamp,
                knownCopyNumber.gene(),
                knownCopyNumber.geneRole().toString(),
                knownCopyNumber.proteinEffect().toString(),
                knownCopyNumber.type(),
                Knowledgebase.toCommaSeparatedSourceString(knownCopyNumber.sources()));
    }

    private void writeEventInterpretations(@NotNull Timestamp timestamp, @NotNull List<EventInterpretation> eventInterpretations) {
        for (List<EventInterpretation> batch : Iterables.partition(eventInterpretations, Utils.DB_BATCH_INSERT_SIZE)) {
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

    @NotNull
    private static Set<String> toStrings(@NotNull Set<CancerType> cancerTypes) {
        Set<String> strings = Sets.newHashSet();
        for (CancerType cancerType : cancerTypes) {
            strings.add(cancerType.name() + NAME_DOID_JOINER + cancerType.doid());
        }
        return strings;
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
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
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep15;
import org.jooq.InsertValuesStep16;
import org.jooq.InsertValuesStep17;
import org.jooq.InsertValuesStep19;
import org.jooq.InsertValuesStep21;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep8;
import org.jooq.InsertValuesStep9;

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

        List<ActionableHotspot> actionableHotspots = actionableEvents.hotspots();
        for (List<ActionableHotspot> batch : Iterables.partition(actionableHotspots, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep17 inserter = context.insertInto(ACTIONABLEHOTSPOT,
                    ACTIONABLEHOTSPOT.MODIFIED,
                    ACTIONABLEHOTSPOT.CHROMOSOME,
                    ACTIONABLEHOTSPOT.POSITION,
                    ACTIONABLEHOTSPOT.REF,
                    ACTIONABLEHOTSPOT.ALT,
                    ACTIONABLEHOTSPOT.SOURCE,
                    ACTIONABLEHOTSPOT.SOURCEEVENT,
                    ACTIONABLEHOTSPOT.SOURCEURLS,
                    ACTIONABLEHOTSPOT.TREATMENT,
                    ACTIONABLEHOTSPOT.SOURCETREATMENTAPPROCH,
                    ACTIONABLEHOTSPOT.TREATMENTAPPROCH,
                    ACTIONABLEHOTSPOT.APPLICABLECANCERTYPE,
                    ACTIONABLEHOTSPOT.APPLICABLEDOID,
                    ACTIONABLEHOTSPOT.BLACKLISTCANCERTYPES,
                    ACTIONABLEHOTSPOT.LEVEL,
                    ACTIONABLEHOTSPOT.DIRECTION,
                    ACTIONABLEHOTSPOT.EVIDENCEURLS);
            batch.forEach(entry -> addRecordHotspots(timestamp, inserter, entry));
            inserter.execute();
        }

        List<ActionableRange> actionableRanges = actionableEvents.ranges();
        for (List<ActionableRange> batch : Iterables.partition(actionableRanges, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep21 inserter = context.insertInto(ACTIONABLERANGE,
                    ACTIONABLERANGE.MODIFIED,
                    ACTIONABLERANGE.GENE,
                    ACTIONABLERANGE.TRANSCRIPT,
                    ACTIONABLERANGE.CHROMOSOME,
                    ACTIONABLERANGE.START,
                    ACTIONABLERANGE.END,
                    ACTIONABLERANGE.MUTATIONTYPE,
                    ACTIONABLERANGE.RANGETYPE,
                    ACTIONABLERANGE.RANGERANK,
                    ACTIONABLERANGE.SOURCE,
                    ACTIONABLERANGE.SOURCEEVENT,
                    ACTIONABLERANGE.SOURCEURLS,
                    ACTIONABLERANGE.TREATMENT,
                    ACTIONABLERANGE.SOURCETREATMENTAPPROCH,
                    ACTIONABLERANGE.TREATMENTAPPROCH,
                    ACTIONABLERANGE.APPLICABLECANCERTYPE,
                    ACTIONABLERANGE.APPLICABLEDOID,
                    ACTIONABLERANGE.BLACKLISTCANCERTYPES,
                    ACTIONABLERANGE.LEVEL,
                    ACTIONABLERANGE.DIRECTION,
                    ACTIONABLERANGE.EVIDENCEURLS);
            batch.forEach(entry -> addRecordRanges(timestamp, inserter, entry));
            inserter.execute();
        }

        List<ActionableGene> actionableGenes = actionableEvents.genes();
        for (List<ActionableGene> batch : Iterables.partition(actionableGenes, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep15 inserter = context.insertInto(ACTIONABLEGENE,
                    ACTIONABLEGENE.MODIFIED,
                    ACTIONABLEGENE.GENE,
                    ACTIONABLEGENE.EVENT,
                    ACTIONABLEGENE.SOURCE,
                    ACTIONABLEGENE.SOURCEEVENT,
                    ACTIONABLEGENE.SOURCEURLS,
                    ACTIONABLEGENE.TREATMENT,
                    ACTIONABLEGENE.SOURCETREATMENTAPPROCH,
                    ACTIONABLEGENE.TREATMENTAPPROCH,
                    ACTIONABLEGENE.APPLICABLECANCERTYPE,
                    ACTIONABLEGENE.APPLICABLEDOID,
                    ACTIONABLEGENE.BLACKLISTCANCERTYPES,
                    ACTIONABLEGENE.LEVEL,
                    ACTIONABLEGENE.DIRECTION,
                    ACTIONABLEGENE.EVIDENCEURLS);
            batch.forEach(entry -> addRecordGenes(timestamp, inserter, entry));
            inserter.execute();
        }

        List<ActionableFusion> actionableFusions = actionableEvents.fusions();
        for (List<ActionableFusion> batch : Iterables.partition(actionableFusions, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep19 inserter = context.insertInto(ACTIONABLEFUSION,
                    ACTIONABLEFUSION.MODIFIED,
                    ACTIONABLEFUSION.GENEUP,
                    ACTIONABLEFUSION.MINEXONUP,
                    ACTIONABLEFUSION.MAXEXONUP,
                    ACTIONABLEFUSION.GENEDOWN,
                    ACTIONABLEFUSION.MINEXONDOWN,
                    ACTIONABLEFUSION.MAXEXONDOWN,
                    ACTIONABLEFUSION.SOURCE,
                    ACTIONABLEFUSION.SOURCEEVENT,
                    ACTIONABLEFUSION.SOURCEURLS,
                    ACTIONABLEFUSION.TREATMENT,
                    ACTIONABLEFUSION.SOURCETREATMENTAPPROCH,
                    ACTIONABLEFUSION.TREATMENTAPPROCH,
                    ACTIONABLEFUSION.APPLICABLECANCERTYPE,
                    ACTIONABLEFUSION.APPLICABLEDOID,
                    ACTIONABLEFUSION.BLACKLISTCANCERTYPES,
                    ACTIONABLEFUSION.LEVEL,
                    ACTIONABLEFUSION.DIRECTION,
                    ACTIONABLEFUSION.EVIDENCEURLS);
            batch.forEach(entry -> addRecordFusions(timestamp, inserter, entry));
            inserter.execute();
        }

        List<ActionableCharacteristic> actionableCharacteristics = actionableEvents.characteristics();
        for (List<ActionableCharacteristic> batch : Iterables.partition(actionableCharacteristics, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep16 inserter = context.insertInto(ACTIONABLECHARACTERISTIC,
                    ACTIONABLECHARACTERISTIC.MODIFIED,
                    ACTIONABLECHARACTERISTIC.NAME,
                    ACTIONABLECHARACTERISTIC.COMPARATOR,
                    ACTIONABLECHARACTERISTIC.CUTOFF,
                    ACTIONABLECHARACTERISTIC.SOURCE,
                    ACTIONABLECHARACTERISTIC.SOURCEEVENT,
                    ACTIONABLECHARACTERISTIC.SOURCEURLS,
                    ACTIONABLECHARACTERISTIC.TREATMENT,
                    ACTIONABLECHARACTERISTIC.SOURCETREATMENTAPPROCH,
                    ACTIONABLECHARACTERISTIC.TREATMENTAPPROCH,
                    ACTIONABLECHARACTERISTIC.APPLICABLECANCERTYPE,
                    ACTIONABLECHARACTERISTIC.APPLICABLEDOID,
                    ACTIONABLECHARACTERISTIC.BLACKLISTCANCERTYPES,
                    ACTIONABLECHARACTERISTIC.LEVEL,
                    ACTIONABLECHARACTERISTIC.DIRECTION,
                    ACTIONABLECHARACTERISTIC.EVIDENCEURLS);
            batch.forEach(entry -> addRecordCharacteristics(timestamp, inserter, entry));
            inserter.execute();
        }

        List<ActionableHLA> actionableHLAs = actionableEvents.hla();
        for (List<ActionableHLA> batch : Iterables.partition(actionableHLAs, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep14 inserter = context.insertInto(ACTIONABLEHLA,
                    ACTIONABLEHLA.MODIFIED,
                    ACTIONABLEHLA.HLATYPE,
                    ACTIONABLEHLA.SOURCE,
                    ACTIONABLEHLA.SOURCEEVENT,
                    ACTIONABLEHLA.SOURCEURLS,
                    ACTIONABLEHLA.TREATMENT,
                    ACTIONABLEHLA.SOURCETREATMENTAPPROCH,
                    ACTIONABLEHLA.TREATMENTAPPROCH,
                    ACTIONABLEHLA.APPLICABLECANCERTYPE,
                    ACTIONABLEHLA.APPLICABLEDOID,
                    ACTIONABLEHLA.BLACKLISTCANCERTYPES,
                    ACTIONABLEHLA.LEVEL,
                    ACTIONABLEHLA.DIRECTION,
                    ACTIONABLEHLA.EVIDENCEURLS);
            batch.forEach(entry -> addRecordHLA(timestamp, inserter, entry));
            inserter.execute();
        }

        Set<KnownHotspot> knownHotspots = knownEvents.knownHotspots();
        for (List<KnownHotspot> batch : Iterables.partition(knownHotspots, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep9 inserter = context.insertInto(KNOWNHOTSPOT,
                    KNOWNHOTSPOT.MODIFIED,
                    KNOWNHOTSPOT.CHROMOSOME,
                    KNOWNHOTSPOT.POSITION,
                    KNOWNHOTSPOT.REF,
                    KNOWNHOTSPOT.ALT,
                    KNOWNHOTSPOT.INPUTGENE,
                    KNOWNHOTSPOT.INPUTTRANSCRIPT,
                    KNOWNHOTSPOT.INPUTPROTEINANNOTATION,
                    KNOWNHOTSPOT.INPUTSOURCE);
            batch.forEach(entry -> addRecordKnownHotspots(timestamp, inserter, entry));
            inserter.execute();
        }

        Set<KnownCodon> knownCodons = knownEvents.knownCodons();
        for (List<KnownCodon> batch : Iterables.partition(knownCodons, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep9 inserter = context.insertInto(KNOWNCODON,
                    KNOWNCODON.MODIFIED,
                    KNOWNCODON.GENE,
                    KNOWNCODON.TRANSCRIPT,
                    KNOWNCODON.CHROMOSOME,
                    KNOWNCODON.START,
                    KNOWNCODON.END,
                    KNOWNCODON.MUTATIONTYPE,
                    KNOWNCODON.CODONRANK,
                    KNOWNCODON.SOURCES);
            batch.forEach(entry -> addRecordKnownCodons(timestamp, inserter, entry));
            inserter.execute();
        }

        Set<KnownExon> knownExons = knownEvents.knownExons();
        for (List<KnownExon> batch : Iterables.partition(knownExons, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep9 inserter = context.insertInto(KNOWNEXON,
                    KNOWNEXON.MODIFIED,
                    KNOWNEXON.GENE,
                    KNOWNEXON.TRANSCRIPT,
                    KNOWNEXON.CHROMOSOME,
                    KNOWNEXON.START,
                    KNOWNEXON.END,
                    KNOWNEXON.MUTATIONTYPE,
                    KNOWNEXON.EXONRANK,
                    KNOWNEXON.SOURCES);
            batch.forEach(entry -> addRecordKnownExons(timestamp, inserter, entry));
            inserter.execute();
        }

        Set<KnownFusionPair> knownFusionPairs = knownEvents.knownFusionPairs();
        for (List<KnownFusionPair> batch : Iterables.partition(knownFusionPairs, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep8 inserter = context.insertInto(KNOWNFUSIONPAIR,
                    KNOWNFUSIONPAIR.MODIFIED,
                    KNOWNFUSIONPAIR.GENEUP,
                    KNOWNFUSIONPAIR.MINEXONUP,
                    KNOWNFUSIONPAIR.MAXEXONUP,
                    KNOWNFUSIONPAIR.GENEDOWN,
                    KNOWNFUSIONPAIR.MINEXONDOWN,
                    KNOWNFUSIONPAIR.MAXEXONDOWN,
                    KNOWNFUSIONPAIR.SOURCES);
            batch.forEach(entry -> addRecordKnownFusionPairs(timestamp, inserter, entry));
            inserter.execute();
        }

        Set<KnownCopyNumber> knownCopyNumbers = knownEvents.knownCopyNumbers();
        for (List<KnownCopyNumber> batch : Iterables.partition(knownCopyNumbers, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep4 inserter = context.insertInto(KNOWNCOPYNUMBER,
                    KNOWNCOPYNUMBER.MODIFIED,
                    KNOWNCOPYNUMBER.GENE,
                    KNOWNCOPYNUMBER.TYPE,
                    KNOWNCOPYNUMBER.SOURCES);
            batch.forEach(entry -> addRecordKnownCopyNumbers(timestamp, inserter, entry));
            inserter.execute();
        }

        for (List<EventInterpretation> batch : Iterables.partition(eventInterpretations, Utils.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep6 inserter = context.insertInto(EVENTINTERPRETATION,
                    EVENTINTERPRETATION.MODIFIED,
                    EVENTINTERPRETATION.SOURCE,
                    EVENTINTERPRETATION.SOURCEEVENT,
                    EVENTINTERPRETATION.INTERPRETEDGENE,
                    EVENTINTERPRETATION.INTERPRETEDEVENT,
                    EVENTINTERPRETATION.INTERPRETEDEVENTTYPE);
            batch.forEach(entry -> addRecordEventInterpretations(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void addRecordHotspots(@NotNull Timestamp timestamp, @NotNull InsertValuesStep17 inserter,
            @NotNull ActionableHotspot actionableHotspot) {
        inserter.values(timestamp,
                actionableHotspot.chromosome(),
                actionableHotspot.position(),
                actionableHotspot.ref(),
                actionableHotspot.alt(),
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

    private static void addRecordRanges(@NotNull Timestamp timestamp, @NotNull InsertValuesStep21 inserter,
            @NotNull ActionableRange actionableRange) {
        inserter.values(timestamp,
                actionableRange.gene(),
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

    private static void addRecordGenes(@NotNull Timestamp timestamp, @NotNull InsertValuesStep15 inserter,
            @NotNull ActionableGene actionableGene) {
        inserter.values(timestamp,
                actionableGene.gene(),
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

    private static void addRecordFusions(@NotNull Timestamp timestamp, @NotNull InsertValuesStep19 inserter,
            @NotNull ActionableFusion actionableFusion) {
        inserter.values(timestamp,
                actionableFusion.geneUp(),
                actionableFusion.minExonUp(),
                actionableFusion.maxExonUp(),
                actionableFusion.geneDown(),
                actionableFusion.minExonDown(),
                actionableFusion.maxExonDown(),
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

    private static void addRecordCharacteristics(@NotNull Timestamp timestamp, @NotNull InsertValuesStep16 inserter,
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

    private static void addRecordHLA(@NotNull Timestamp timestamp, @NotNull InsertValuesStep14 inserter,
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

    private static void addRecordKnownHotspots(@NotNull Timestamp timestamp, @NotNull InsertValuesStep9 inserter,
            @NotNull KnownHotspot knownHotspot) {
        inserter.values(timestamp,
                knownHotspot.chromosome(),
                knownHotspot.position(),
                knownHotspot.ref(),
                knownHotspot.alt(),
                knownHotspot.gene(),
                knownHotspot.transcript(),
                knownHotspot.proteinAnnotation(),
                Knowledgebase.toCommaSeparatedSourceString(knownHotspot.sources()));
    }

    private static void addRecordKnownCodons(@NotNull Timestamp timestamp, @NotNull InsertValuesStep9 inserter,
            @NotNull KnownCodon knownCodon) {
        inserter.values(timestamp,
                knownCodon.annotation().gene(),
                knownCodon.annotation().transcript(),
                knownCodon.annotation().chromosome(),
                knownCodon.annotation().start(),
                knownCodon.annotation().end(),
                knownCodon.annotation().applicableMutationType(),
                knownCodon.annotation().rank(),
                Knowledgebase.toCommaSeparatedSourceString(knownCodon.sources()));
    }

    private static void addRecordKnownExons(@NotNull Timestamp timestamp, @NotNull InsertValuesStep9 inserter,
            @NotNull KnownExon knownExon) {
        inserter.values(timestamp,
                knownExon.annotation().gene(),
                knownExon.annotation().transcript(),
                knownExon.annotation().chromosome(),
                knownExon.annotation().start(),
                knownExon.annotation().end(),
                knownExon.annotation().applicableMutationType(),
                knownExon.annotation().rank(),
                Knowledgebase.toCommaSeparatedSourceString(knownExon.sources()));
    }

    private static void addRecordKnownFusionPairs(@NotNull Timestamp timestamp, @NotNull InsertValuesStep8 inserter,
            @NotNull KnownFusionPair knownFusionPairs) {
        inserter.values(timestamp,
                knownFusionPairs.geneUp(),
                knownFusionPairs.minExonUp(),
                knownFusionPairs.maxExonUp(),
                knownFusionPairs.geneDown(),
                knownFusionPairs.minExonDown(),
                knownFusionPairs.maxExonDown(),
                Knowledgebase.toCommaSeparatedSourceString(knownFusionPairs.sources()));
    }

    private static void addRecordKnownCopyNumbers(@NotNull Timestamp timestamp, @NotNull InsertValuesStep4 inserter,
            @NotNull KnownCopyNumber knownCopyNumber) {
        inserter.values(timestamp,
                knownCopyNumber.gene(),
                knownCopyNumber.type(),
                Knowledgebase.toCommaSeparatedSourceString(knownCopyNumber.sources()));
    }

    private static void addRecordEventInterpretations(@NotNull Timestamp timestamp, @NotNull InsertValuesStep6 inserter,
            @NotNull EventInterpretation eventInterpretation) {
        inserter.values(timestamp,
                eventInterpretation.source(),
                eventInterpretation.sourceEvent(),
                eventInterpretation.interpretedGene(),
                eventInterpretation.interpretedEvent(),
                eventInterpretation.interpretedEventType());
    }

    @NotNull
    public static Set<String> toStrings(@NotNull Set<CancerType> cancerTypes) {
        Set<String> strings = Sets.newHashSet();
        for (CancerType cancerType : cancerTypes) {
            strings.add(cancerType.name() + NAME_DOID_JOINER + cancerType.doid());
        }
        return strings;
    }

    @NotNull
    public static String concat(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(MAIN_JOINER);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }
}